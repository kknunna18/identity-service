package com.mysociety.identity.handler;

import com.mysociety.identity.dto.AuthResponse;
import com.mysociety.identity.dto.LoginRequest;
import com.mysociety.identity.dto.UserInfoResponse;
import com.mysociety.identity.dto.v1.LoginRequestV1;
import com.mysociety.identity.dto.v1.LoginResponseV1;
import com.mysociety.identity.entity.AppUser;
import com.mysociety.identity.repository.AppUserRepository;
import com.mysociety.identity.security.JwtService;
import com.mysociety.identity.service.AuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthHandler {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository appUserRepository;
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthHandler(AuthenticationManager authenticationManager,
                       AppUserRepository appUserRepository,
                       JwtService jwtService,
                       AuthenticationService authenticationService) {
        this.authenticationManager = authenticationManager;
        this.appUserRepository = appUserRepository;
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    public AuthResponse login(LoginRequest loginRequest) {
        String username = loginRequest.username();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, loginRequest.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        AppUser appUser = appUserRepository.findByEmailOrMobileNumber(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                appUser.getEmail() != null ? appUser.getEmail() : appUser.getMobileNumber(),
                appUser.getPasswordHash(),
                java.util.List.of()
        );

        // generate a token without society/roles for legacy handler
        String token = jwtService.generateToken(appUser.getEmail() == null ? appUser.getMobileNumber() : appUser.getEmail(), appUser.getId(), null, java.util.List.of(), java.util.List.of());
        return new AuthResponse(token, UserInfoResponse.from(appUser));
    }

    // v1 login should be delegated to AuthenticationService
    public LoginResponseV1 loginV1(LoginRequestV1 request) {
        return authenticationService.login(request);
    }

    public UserInfoResponse currentUser(String username) {
        AppUser appUser = appUserRepository.findByEmailOrMobileNumber(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return UserInfoResponse.from(appUser);
    }
}
