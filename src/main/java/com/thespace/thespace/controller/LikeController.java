package com.thespace.thespace.controller;

import com.thespace.thespace.dto.like.LikeDTO;
import com.thespace.thespace.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
public class LikeController {

    private final LikeService likeService;

    @PutMapping
    public int like(@RequestBody LikeDTO likeDTO) {
        return likeService.like(likeDTO);
    }
}
