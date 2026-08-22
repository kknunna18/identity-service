package com.mysociety.identity.dto;

public record AuthResponse(String token, UserInfoResponse user) {
}
