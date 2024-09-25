package com.thespace.thespace.service;

import com.thespace.thespace.dto.PageReqDTO;
import com.thespace.thespace.dto.PageResDTO;
import com.thespace.thespace.dto.ReplyDTO;

public interface ReplyService
  {
    Long register(Long bno, ReplyDTO replyDTO);
    void delete(Long bno, Long rno);
    PageResDTO<ReplyDTO> getListReply(Long bno, PageReqDTO pageReqDTO);
  }
