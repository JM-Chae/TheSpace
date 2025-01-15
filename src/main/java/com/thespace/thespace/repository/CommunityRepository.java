package com.thespace.thespace.repository;

import com.thespace.thespace.domain.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    Community findByCommunityName(String name);

    boolean existsByCommunityName(String name);

    @Query("SELECT c.communityId FROM Community c WHERE UPPER(c.communityName) = :communityName")
    Long findCommunityIdByNameIgnoreCase(@Param("communityName") String communityName);
}
