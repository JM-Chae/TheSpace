package com.thespace.thespace.controller;

import com.thespace.thespace.dto.BoardDTO;
import com.thespace.thespace.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController
  {
    private final BoardService boardService;

    @GetMapping("/post")
    public void postGet()
      {
      }

    @PostMapping("/post")
    public String postPost(@Valid BoardDTO boardDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes)
      {
        Long bno = boardService.register(boardDTO);

        return "redirect:/board/read" + "/" + bno;
      }
  }
