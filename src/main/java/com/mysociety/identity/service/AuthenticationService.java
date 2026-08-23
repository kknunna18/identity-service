package com.mysociety.identity.service;

import com.mysociety.identity.dto.UserInfoResponse;
import com.mysociety.identity.dto.v1.LoginRequestV1;
import com.mysociety.identity.dto.v1.LoginResponseV1;
import com.mysociety.identity.dto.v1.SocietySummary;
import com.mysociety.identity.entity.AppUser;
import com.mysociety.identity.entity.HouseholdMembership;
import com.mysociety.identity.entity.UserSocietyRole;
import com.mysociety.identity.repository.AppUserRepository;
import com.mysociety.identity.repository.HouseholdMembershipRepository;
import com.mysociety.identity.repository.UserSocietyRoleRepository;
import com.mysociety.identity.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    private final AppUserRepository appUserRepository;
    private final HouseholdMembershipRepository householdMembershipRepository;
    private final UserSocietyRoleRepository userSocietyRoleRepository;
    private final com.mysociety.identity.repository.RolePermissionRepository rolePermissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LoginContextService loginContextService;

    public AuthenticationService(AppUserRepository appUserRepository,
                                 HouseholdMembershipRepository householdMembershipRepository,
                                 UserSocietyRoleRepository userSocietyRoleRepository,
                                 com.mysociety.identity.repository.RolePermissionRepository rolePermissionRepository,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService,
                                 LoginContextService loginContextService) {
        this.appUserRepository = appUserRepository;
        this.householdMembershipRepository = householdMembershipRepository;
        this.userSocietyRoleRepository = userSocietyRoleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.loginContextService = loginContextService;
    }

    public LoginResponseV1 login(LoginRequestV1 request) {
        String login = request.username() != null ? request.username() : request.email();
        if (login == null || login.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password.");
        }
        login = login.trim();

        Optional<AppUser> userOpt = appUserRepository.findByEmailOrMobileNumber(login);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password.");
        }

        AppUser user = userOpt.get();
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is not active");
        }
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(OffsetDateTime.now(ZoneOffset.UTC))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is locked");
        }

        boolean passwordMatches = passwordEncoder.matches(request.password() == null ? "" : request.password(), user.getPasswordHash());
        if (!passwordMatches) {
            int failed = user.getFailedLoginCount() == null ? 0 : user.getFailedLoginCount();
            failed++;
            user.setFailedLoginCount(failed);
            if (failed >= 5) {
                user.setLockedUntil(OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(15));
            }
            appUserRepository.save(user);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password.");
        }

        user.setFailedLoginCount(0);
        user.setLockedUntil(null);
        user.setLastLoginAt(OffsetDateTime.now(ZoneOffset.UTC));
        appUserRepository.save(user);

        List<HouseholdMembership> memberships = householdMembershipRepository.findActiveByUserId(user.getId());
        List<SocietySummary> available = memberships.stream()
                .map(hm -> new SocietySummary(hm.getSociety().getId(), hm.getSociety().getCode(), hm.getSociety().getName()))
                .distinct()
                .collect(Collectors.toList());

        if (available.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your account does not have an active society membership.");
        }

        Set<UUID> authorizedSocieties = memberships.stream()
                .map(hm -> hm.getSociety().getId())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (authorizedSocieties.size() == 1) {
            UUID selectedSocietyId = authorizedSocieties.iterator().next();
            SocietySummary selected = available.stream().filter(s -> s.id().equals(selectedSocietyId)).findFirst().orElse(null);
            List<UserSocietyRole> roles = userSocietyRoleRepository.findActiveRolesByUserAndSociety(user.getId(), selectedSocietyId);
            List<String> roleNames = roles.stream().map(r -> r.getRole().getName()).collect(Collectors.toList());
            List<java.util.UUID> roleIds = roles.stream().map(r -> r.getRole().getId()).collect(Collectors.toList());
            List<String> permissions = java.util.List.of();
            if (!roleIds.isEmpty()) {
                var rps = rolePermissionRepository.findByRoleIds(roleIds);
                permissions = rps.stream().map(rp -> rp.getPermission().getCode()).distinct().collect(Collectors.toList());
            }
            String token = jwtService.generateToken(user.getEmail() == null ? user.getMobileNumber() : user.getEmail(), user.getId(), selectedSocietyId, roleNames, permissions);
            return new LoginResponseV1(token, "Bearer", jwtService.getExpirationSeconds(), false, UserInfoResponse.from(user), available, selected, roleNames, permissions);
        }

        loginContextService.create(user.getId(), authorizedSocieties);
        return new LoginResponseV1(null, "Bearer", 300L, true, UserInfoResponse.from(user), available, null, List.of(), List.of());
    }

    public LoginResponseV1 selectSociety(String loginContextToken, String societyId) {
        if (loginContextToken == null || loginContextToken.isBlank() || societyId == null || societyId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Your login session has expired. Please sign in again.");
        }
        UUID selectedSocietyId;
        try {
            selectedSocietyId = UUID.fromString(societyId);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access the selected society.");
        }

        LoginContextService.StoredLoginContext context = loginContextService.validateAndConsume(loginContextToken, selectedSocietyId);
        Optional<AppUser> userOpt = appUserRepository.findById(context.userId());
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Your login session has expired. Please sign in again.");
        }
        AppUser user = userOpt.get();
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is not active");
        }

        boolean membershipExists = householdMembershipRepository.findActiveByUserId(user.getId()).stream()
                .anyMatch(hm -> hm.getSociety().getId().equals(selectedSocietyId));
        if (!membershipExists) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access the selected society.");
        }

        List<UserSocietyRole> roles = userSocietyRoleRepository.findActiveRolesByUserAndSociety(user.getId(), selectedSocietyId);
        List<String> roleNames = roles.stream().map(r -> r.getRole().getName()).collect(Collectors.toList());
        List<java.util.UUID> roleIds = roles.stream().map(r -> r.getRole().getId()).collect(Collectors.toList());
        List<String> permissions = java.util.List.of();
        if (!roleIds.isEmpty()) {
            var rps = rolePermissionRepository.findByRoleIds(roleIds);
            permissions = rps.stream().map(rp -> rp.getPermission().getCode()).distinct().collect(Collectors.toList());
        }

        SocietySummary selected = householdMembershipRepository.findActiveByUserId(user.getId()).stream()
                .filter(hm -> hm.getSociety().getId().equals(selectedSocietyId))
                .map(hm -> new SocietySummary(hm.getSociety().getId(), hm.getSociety().getCode(), hm.getSociety().getName()))
                .findFirst().orElse(null);

        String token = jwtService.generateToken(user.getEmail() == null ? user.getMobileNumber() : user.getEmail(), user.getId(), selectedSocietyId, roleNames, permissions);
        return new LoginResponseV1(token, "Bearer", jwtService.getExpirationSeconds(), false, UserInfoResponse.from(user), java.util.List.of(), selected, roleNames, permissions);
    }
}
