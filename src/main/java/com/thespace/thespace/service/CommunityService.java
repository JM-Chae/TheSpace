package com.thespace.thespace.service;

import com.thespace.thespace.dto.CommunityDTO;
import com.thespace.thespace.dto.PageReqDTO;
import com.thespace.thespace.dto.PageResDTO;

public interface CommunityService
  {
    Long createCommunity(String communityName);
    boolean check(String communityName);
    PageResDTO<CommunityDTO> getCommunityList(PageReqDTO pageReqDTO);
    void deleteCommunity(Long communityId);
    CommunityDTO getCommunity(String communityName);
  }
