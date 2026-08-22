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

    public AuthenticationService(AppUserRepository appUserRepository,
                                 HouseholdMembershipRepository householdMembershipRepository,
                                 UserSocietyRoleRepository userSocietyRoleRepository,
                                 com.mysociety.identity.repository.RolePermissionRepository rolePermissionRepository,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService) {
        this.appUserRepository = appUserRepository;
        this.householdMembershipRepository = householdMembershipRepository;
        this.userSocietyRoleRepository = userSocietyRoleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponseV1 login(LoginRequestV1 request) {
        String email = request.email();
        if (email == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        email = email.trim().toLowerCase(Locale.ROOT);

        Optional<AppUser> userOpt = appUserRepository.findByEmailIgnoreCase(email);

        if (userOpt.isEmpty()) {
            // Don't reveal existence
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        AppUser user = userOpt.get();

        // Accept only ACTIVE users
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is not active");
        }

        // Check locked_until
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
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        // Successful login: reset counters
        user.setFailedLoginCount(0);
        user.setLockedUntil(null);
        user.setLastLoginAt(OffsetDateTime.now(ZoneOffset.UTC));
        appUserRepository.save(user);

        // Load active societies via household membership
        List<HouseholdMembership> memberships = householdMembershipRepository.findActiveByUserId(user.getId());
        List<SocietySummary> available = memberships.stream()
                .map(hm -> new SocietySummary(hm.getSociety().getId(), hm.getSociety().getCode(), hm.getSociety().getName()))
                .collect(Collectors.toList());

        if (available.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No active society membership");
        }

        UUID selectedSocietyId = null;
        if (request.societyId() != null) {
            try {
                selectedSocietyId = UUID.fromString(request.societyId());
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid societyId");
            }
        }

        SocietySummary selected = null;
        if (available.size() == 1 && selectedSocietyId == null) {
            selected = available.get(0);
            selectedSocietyId = selected.id();
        } else if (available.size() > 1 && selectedSocietyId == null) {
            return new LoginResponseV1(
                    null,
                    "Bearer",
                    null,
                    true,
                    UserInfoResponse.from(user),
                    available,
                    null,
                    List.of(),
                    List.of()
            );
        }

        // verify selected belongs to available
        if (selectedSocietyId != null) {
            UUID sid = selectedSocietyId;
            Optional<SocietySummary> match = available.stream().filter(s -> s.id().equals(sid)).findFirst();
            if (match.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requested society not available");
            }
            selected = match.get();
        }

        // Load roles and permissions
        List<UserSocietyRole> roles = userSocietyRoleRepository.findActiveRolesByUserAndSociety(user.getId(), selectedSocietyId);
        List<String> roleNames = roles.stream().map(r -> r.getRole().getName()).collect(Collectors.toList());

        // collect permission codes via RolePermissionRepository
        List<java.util.UUID> roleIds = roles.stream().map(r -> r.getRole().getId()).collect(Collectors.toList());
        List<String> permissions = java.util.List.of();
        if (!roleIds.isEmpty()) {
            var rps = rolePermissionRepository.findByRoleIds(roleIds);
            permissions = rps.stream().map(rp -> rp.getPermission().getCode()).distinct().collect(Collectors.toList());
        }

        // Generate token
        String token = jwtService.generateToken(user.getEmail() == null ? user.getMobileNumber() : user.getEmail(),
                user.getId(),
                selectedSocietyId,
                roleNames,
                permissions);

        long expiresIn = jwtService.getExpirationSeconds();

        return new LoginResponseV1(
                token,
                "Bearer",
                expiresIn,
                false,
                UserInfoResponse.from(user),
                available,
                selected,
                roleNames,
                permissions
        );
    }
}
