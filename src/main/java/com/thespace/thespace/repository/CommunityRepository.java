package com.thespace.thespace.repository;

import com.thespace.thespace.domain.Community;
import com.thespace.thespace.repository.getList.GetListCommunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long>, GetListCommunity
  {
    Community findByCommunityName(String name);
    boolean existsByCommunityName(String name);

  }
