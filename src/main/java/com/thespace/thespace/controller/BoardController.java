package com.thespace.thespace.controller;

import com.thespace.thespace.dto.board.BoardDTO;
import com.thespace.thespace.dto.board.BoardModifyDTO;
import com.thespace.thespace.dto.board.BoardPostDTO;
import com.thespace.thespace.dto.page.PageReqDTO;
import com.thespace.thespace.dto.page.PageResDTO;
import com.thespace.thespace.service.BoardService;
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
    public Long post(@Valid @RequestBody BoardPostDTO boardPostDTO, Authentication authentication) {
        return boardService.post(boardPostDTO, authentication);
    }

    @GetMapping("/{bno}")
    public BoardDTO read(@PathVariable("bno") Long bno) {
        return boardService.read(bno);
    }

    @GetMapping("/list")
    public PageResDTO<BoardDTO> list(@ModelAttribute PageReqDTO pageReqDTO) {
        return boardService.list(pageReqDTO);
    }

    @PatchMapping
    public void modify(@Valid @RequestBody BoardModifyDTO boardModifyDTO) {
        boardService.modify(boardModifyDTO);
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
