package com.thespace.thespace.repository;

import com.thespace.thespace.domain.Community;
import com.thespace.thespace.repository.getList.GetListCommunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long>, GetListCommunity
  {
    Community findByCommunityName(String name);
    boolean existsByCommunityName(String name);

    @Query("SELECT c.communityId FROM Community c WHERE UPPER(c.communityName) = :communityName")
    Long findCommunityIdByNameIgnoreCase(@Param("communityName") String communityName);
  }
