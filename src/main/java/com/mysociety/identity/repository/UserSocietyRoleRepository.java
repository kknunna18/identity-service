package com.mysociety.identity.repository;

import com.mysociety.identity.entity.UserSocietyRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserSocietyRoleRepository extends JpaRepository<UserSocietyRole, UUID> {

    @Query("select usr from UserSocietyRole usr where usr.user.id = :userId and usr.society.id = :societyId and usr.isActive = true")
    List<UserSocietyRole> findActiveRolesByUserAndSociety(@Param("userId") UUID userId, @Param("societyId") UUID societyId);

    @Query("select usr from UserSocietyRole usr where usr.user.id = :userId and usr.isActive = true")
    List<UserSocietyRole> findActiveRolesByUser(@Param("userId") UUID userId);
}
