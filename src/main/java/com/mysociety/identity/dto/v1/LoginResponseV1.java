package com.mysociety.identity.dto.v1;

import com.mysociety.identity.dto.UserInfoResponse;

import java.util.List;

public record LoginResponseV1(
        String accessToken,
        String tokenType,
        Long expiresIn,
        boolean requiresSocietySelection,
        UserInfoResponse currentUser,
        List<SocietySummary> availableSocieties,
        SocietySummary selectedSociety,
        List<String> roles,
        List<String> permissions
) {
}
