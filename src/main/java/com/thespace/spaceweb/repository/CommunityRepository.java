package com.thespace.spaceweb.repository;

import com.thespace.spaceweb.domain.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    Community findByCommunityName(String name);

    boolean existsByCommunityName(String name);

    @Query("SELECT c.communityId FROM Community c WHERE UPPER(c.communityName) = :communityName")
    Long findCommunityIdByNameIgnoreCase(@Param("communityName") String communityName);
}
