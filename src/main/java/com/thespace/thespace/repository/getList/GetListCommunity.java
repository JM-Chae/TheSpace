package com.thespace.thespace.repository.getList;

import com.thespace.thespace.dto.CommunityDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetListCommunity
  {
    public Page<CommunityDTO> getList(String[] types, String keyword, Pageable pageable);
  }
