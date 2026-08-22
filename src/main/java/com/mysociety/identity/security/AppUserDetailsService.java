package com.mysociety.identity.security;

import com.mysociety.identity.entity.AppUser;
import com.mysociety.identity.repository.AppUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public AppUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = appUserRepository.findByEmailOrMobileNumber(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new AppUserPrincipal(user);
    }
}
