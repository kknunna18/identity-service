package com.mysociety.identity.handler;

import com.mysociety.identity.dto.CreateUserRequest;
import com.mysociety.identity.dto.UserInfoResponse;
import com.mysociety.identity.entity.AppUser;
import com.mysociety.identity.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class UserHandler {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public UserHandler(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserInfoResponse createUser(CreateUserRequest request) {
        if (request.email() == null || request.email().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (appUserRepository.findByEmailIgnoreCase(request.email()).isPresent()) {
            throw new IllegalArgumentException("User already exists with this email");
        }

        AppUser user = new AppUser();
        user.setEmail(request.email());
        user.setMobileNumber(request.mobileNumber());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPreferredLanguage(request.preferredLanguage() != null ? request.preferredLanguage() : "en");
        user.setTimezone(request.timezone() != null ? request.timezone() : "Asia/Kolkata");
        user.setEmailVerified(false);
        user.setMobileVerified(false);
        user.setMfaEnabled(false);
        user.setFailedLoginCount(0);
        user.setStatus("INVITED");
        OffsetDateTime now = OffsetDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setVersion(0L);

        AppUser saved = appUserRepository.save(user);
        return UserInfoResponse.from(saved);
    }

    public List<UserInfoResponse> getAllUsers() {
        return appUserRepository.findAll().stream()
                .map(UserInfoResponse::from)
                .toList();
    }

    public UserInfoResponse getUserById(UUID userId) {
        return appUserRepository.findById(userId)
                .map(UserInfoResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }
}
