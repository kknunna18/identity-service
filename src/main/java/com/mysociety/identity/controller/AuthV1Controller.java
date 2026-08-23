package com.mysociety.identity.controller;

import com.mysociety.identity.dto.v1.LoginRequestV1;
import com.mysociety.identity.dto.v1.LoginResponseV1;
import com.mysociety.identity.dto.v1.SelectSocietyRequest;
import com.mysociety.identity.handler.AuthHandler;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/select-society")
    public ResponseEntity<LoginResponseV1> selectSociety(@Valid @RequestBody SelectSocietyRequest request) {
        return ResponseEntity.ok(authHandler.selectSociety(request));
    }
}
