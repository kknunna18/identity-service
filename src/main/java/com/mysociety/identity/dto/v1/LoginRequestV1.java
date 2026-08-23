package com.mysociety.identity.dto.v1;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginRequestV1(
        @JsonProperty("username") @JsonAlias({"email"}) String username,
        @JsonProperty("password") String password,
        @JsonProperty("societyId") String societyId
) {
    public String email() {
        return username;
    }
}
