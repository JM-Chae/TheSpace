package com.thespace.thespace.repository.getList;

import com.thespace.thespace.dto.ReplyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetListReply
  {
    Page<ReplyDTO> getListReply(Long bno, String[] types, String keyword, Pageable pageable);
  }
