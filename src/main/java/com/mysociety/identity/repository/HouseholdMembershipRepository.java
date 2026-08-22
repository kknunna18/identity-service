package com.mysociety.identity.repository;

import com.mysociety.identity.entity.HouseholdMembership;
import com.mysociety.identity.entity.Society;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface HouseholdMembershipRepository extends JpaRepository<HouseholdMembership, UUID> {

    @Query("select hm from HouseholdMembership hm where hm.user.id = :userId and (hm.moveOutDate is null or hm.moveOutDate > current_date)")
    List<HouseholdMembership> findActiveByUserId(@Param("userId") UUID userId);

}
