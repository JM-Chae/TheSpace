package com.thespace.thespace.controller;

import com.thespace.thespace.dto.LikeDTO;
import com.thespace.thespace.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
public class LikeController
  {
    private LikeService likeService;
    @Autowired
    public void setLikeService(LikeService likeService)
      {
        this.likeService = likeService;
      }

    @PutMapping
    public void like(LikeDTO likeDTO)
      {
        likeService.like(likeDTO);
      }
  }
