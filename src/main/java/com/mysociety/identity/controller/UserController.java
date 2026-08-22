package com.mysociety.identity.controller;

import com.mysociety.identity.dto.CreateUserRequest;
import com.mysociety.identity.dto.UserInfoResponse;
import com.mysociety.identity.handler.UserHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserHandler userHandler;

    public UserController(UserHandler userHandler) {
        this.userHandler = userHandler;
    }

    @PostMapping
    public ResponseEntity<UserInfoResponse> createUser(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userHandler.createUser(request));
    }

    @GetMapping
    public ResponseEntity<List<UserInfoResponse>> getAllUsers() {
        return ResponseEntity.ok(userHandler.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserInfoResponse> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userHandler.getUserById(id));
    }
}
