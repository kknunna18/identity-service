package com.mysociety.identity.controller;

import com.mysociety.identity.dto.v1.LoginRequestV1;
import com.mysociety.identity.dto.v1.LoginResponseV1;
import com.mysociety.identity.handler.AuthHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthV1Controller {

    private final AuthHandler authHandler;

    public AuthV1Controller(AuthHandler authHandler) {
        this.authHandler = authHandler;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseV1> login(@Valid @RequestBody LoginRequestV1 request) {
        return ResponseEntity.ok(authHandler.loginV1(request));
    }
}
