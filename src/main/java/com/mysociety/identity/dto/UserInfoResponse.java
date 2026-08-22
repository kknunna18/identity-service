package com.mysociety.identity.dto;

import com.mysociety.identity.entity.AppUser;

import java.util.UUID;

public record UserInfoResponse(UUID id,
                              String email,
                              String mobileNumber,
                              String firstName,
                              String lastName,
                              String status) {

    public static UserInfoResponse from(AppUser appUser) {
        return new UserInfoResponse(
                appUser.getId(),
                appUser.getEmail(),
                appUser.getMobileNumber(),
                appUser.getFirstName(),
                appUser.getLastName(),
                appUser.getStatus()
        );
    }
}
