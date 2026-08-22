package com.mysociety.identity.repository;

import com.mysociety.identity.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {

    @Query("select rp from RolePermission rp where rp.role.id in :roleIds")
    List<RolePermission> findByRoleIds(@Param("roleIds") List<UUID> roleIds);
}
