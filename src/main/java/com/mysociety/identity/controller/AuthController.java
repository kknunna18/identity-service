package com.mysociety.identity.controller;

import com.mysociety.identity.dto.AuthResponse;
import com.mysociety.identity.dto.LoginRequest;
import com.mysociety.identity.dto.UserInfoResponse;
import com.mysociety.identity.handler.AuthHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthHandler authHandler;

    public AuthController(AuthHandler authHandler) {
        this.authHandler = authHandler;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authHandler.login(loginRequest));
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> currentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(authHandler.currentUser(authentication.getName()));
    }
}
