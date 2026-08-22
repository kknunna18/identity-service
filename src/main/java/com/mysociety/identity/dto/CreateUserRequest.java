package com.mysociety.identity.dto;

public record CreateUserRequest(
        String email,
        String mobileNumber,
        String password,
        String firstName,
        String lastName,
        String preferredLanguage,
        String timezone
) {
}
