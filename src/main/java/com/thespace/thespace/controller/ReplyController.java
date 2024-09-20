package com.thespace.thespace.controller;

import com.thespace.thespace.dto.ReplyDTO;
import com.thespace.thespace.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/reply")
@RequiredArgsConstructor
public class ReplyController
  {
    private final ReplyService replyService;

    @PostMapping("/")
    public void register(@RequestBody ReplyDTO replyDTO)
      {
        replyService.register(replyDTO);
      }
  }
