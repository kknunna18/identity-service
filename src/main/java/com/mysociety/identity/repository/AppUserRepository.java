package com.mysociety.identity.repository;

import com.mysociety.identity.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    Optional<AppUser> findByEmailIgnoreCase(String email);

    Optional<AppUser> findByMobileNumber(String mobileNumber);

    @Query("select u from AppUser u where lower(u.email) = lower(:login) or u.mobileNumber = :login")
    Optional<AppUser> findByEmailOrMobileNumber(@Param("login") String login);
}
