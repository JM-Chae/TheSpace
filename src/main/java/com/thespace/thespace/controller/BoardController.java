package com.thespace.thespace.controller;

import com.thespace.thespace.dto.BoardDTO;
import com.thespace.thespace.dto.PageReqDTO;
import com.thespace.thespace.dto.PageResDTO;
import com.thespace.thespace.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController
  {
    private final BoardService boardService;

    @GetMapping("/")
    public void postGet()
      {
      }

    @PostMapping("/post")
    public String post(@Valid @RequestBody BoardDTO boardDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes)
      {
        if(bindingResult.hasErrors())
          {
            Long bno = boardService.post(boardDTO);
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/board/post";
          }

        Long bno = boardService.post(boardDTO);

        return "redirect:/board/read" + "/" + bno;
      }

    @GetMapping({"/read/{bno}", "/modify/{bno}"})
    public BoardDTO readGet(@PathVariable("bno") Long bno, Model model)
      {
        BoardDTO boardDTO = boardService.read(bno);
        model.addAttribute("dtoList", boardDTO);

        return boardDTO;
      }

    @GetMapping("/list")
    public PageResDTO<BoardDTO> listGet(PageReqDTO pageReqDTO, Model model)
      {
        PageResDTO<BoardDTO> getList = boardService.list(pageReqDTO);
        model.addAttribute("getList", getList);
        return getList;
      }

    @PutMapping("/modify")
        public String modifyPost(@Valid @RequestBody BoardDTO boardDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes)
        {
          if(bindingResult.hasErrors())
            {
              boardService.modify(boardDTO);
              redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
              return "redirect:/board/read" + "/" + boardDTO.getBno();

            }
          boardService.modify(boardDTO);
          return "redirect:/board/read" + "/" + boardDTO.getBno();

      }

    @DeleteMapping("/delete/{bno}")
    public void delete(@PathVariable("bno") Long bno)
      {
        boardService.delete(bno);
      }
  }
