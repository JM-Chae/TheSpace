package com.thespace.spaceweb.controller;

import com.thespace.spaceweb.dto.BoardDTOs;
import com.thespace.spaceweb.dto.BoardDTOs.Info;
import com.thespace.spaceweb.dto.page.PageReqDTO;
import com.thespace.spaceweb.dto.page.PageResDTO;
import com.thespace.spaceweb.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public Long post(@Valid @RequestBody BoardDTOs.Post postDTO, Authentication authentication) {
        return boardService.post(postDTO, authentication);
    }

    @GetMapping("/{bno}")
    public Info read(@PathVariable("bno") Long bno) {
        return boardService.read(bno);
    }

    @GetMapping("/list")
    public PageResDTO<Info> list(@ModelAttribute PageReqDTO pageReqDTO) {
        return boardService.list(pageReqDTO);
    }

    @PatchMapping
    public void modify(@Valid @RequestBody BoardDTOs.Modify modifyDTO) {
        boardService.modify(modifyDTO);
    }

    @DeleteMapping("/{bno}")
    public void delete(@PathVariable("bno") Long bno, Authentication authentication) {

        boardService.delete(bno, authentication);
    }

    @DeleteMapping("/{bno}/admin")
    public void delete(@PathVariable("bno") Long bno, Authentication authentication,
        @RequestParam("communityName") String communityName) {

        boardService.delete(bno, authentication, communityName);
    }
}
