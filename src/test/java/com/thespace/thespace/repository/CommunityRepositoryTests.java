package com.thespace.thespace.repository;

import com.thespace.thespace.domain.Community;
import com.thespace.thespace.service.CommunityService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
class CommunityRepositoryTests
  {
    @Autowired
    CommunityService communityService;

    @Autowired
    CommunityRepository communityRepository;

    @Test
    public void createCommuTest()
      {
        Community community = Community.builder()
            .communityName("The Space Test Community")
            .build();

        Long communityId = communityRepository.save(community).getCommunityId();
        log.info("communityId : " + communityId);
      }
  }