package com.thespace.thespace.service;

import com.thespace.thespace.dto.CommunityDTO;
import com.thespace.thespace.dto.PageReqDTO;
import com.thespace.thespace.dto.PageResDTO;

public interface CommunityService
  {
    Long createCommunity(CommunityDTO communityDTO);
    boolean check(String communityName);
    PageResDTO<CommunityDTO> getCommunityList(PageReqDTO pageReqDTO);
    void deleteCommunity(Long communityId);
    CommunityDTO getCommunity(String communityName);
    Long getCommunityIdByName(String communityName);
    void updateCommunity(CommunityDTO communityDTO);
  }
