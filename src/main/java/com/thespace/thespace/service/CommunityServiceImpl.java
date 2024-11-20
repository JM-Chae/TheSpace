package com.thespace.thespace.service;



import com.thespace.thespace.domain.Community;
import com.thespace.thespace.dto.CommunityDTO;
import com.thespace.thespace.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class CommunityServiceImpl implements CommunityService
  {
    private CommunityRepository communityRepository;

    @Autowired
    public void setCommunityRepository(CommunityRepository communityRepository)
      {
        this.communityRepository=communityRepository;
      }

    public Long createCommunity(CommunityDTO communityDTO)
      {
        Community community= Community.builder()
            .communityId(communityDTO.getCommunityId())
            .communityName(communityDTO.getCommunityName())
            .build();

        Long communityId = communityRepository.save(community).getCommunityId();
        return communityId;
      }

    @Override
    public boolean check(String communityName)
      {
        return !communityRepository.existsByCommunityName(communityName);
      }
  }
