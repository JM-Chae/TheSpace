package com.thespace.thespace.service;

import com.thespace.thespace.dto.ReplyDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ReplyServiceTest
  {
    @Autowired
    private ReplyService replyService;

    @Test
    void reply()
      {
        ReplyDTO replyDTO = ReplyDTO.builder()
            .replyContent("test")
            .replyWriter("test")
            .bno(1255L)
            .path("Board")
            .build();

        replyService.register(replyDTO);
      }
  }