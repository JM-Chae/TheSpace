package com.thespace.thespace.controller;

import com.thespace.thespace.dto.BoardDTO;
import com.thespace.thespace.dto.PageReqDTO;
import com.thespace.thespace.dto.PageResDTO;
import com.thespace.thespace.service.BoardService;
import com.thespace.thespace.service.UserRoleService;
import com.thespace.thespace.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final UserService userService;
    private final UserRoleService userRoleService;

    @PostMapping("/post")
    public Long post(@Valid @RequestBody BoardDTO boardDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes)
      {
        if (bindingResult.hasErrors())
          {
            Long bno = boardService.post(boardDTO);
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return null;
          }

        Long bno = boardService.post(boardDTO);

        return bno;
      }

    @GetMapping("/read/{bno}")
    public BoardDTO readGet(@PathVariable("bno") Long bno)
      {
        BoardDTO boardDTO = boardService.read(bno);

        return boardDTO;
      }

    @GetMapping("/list")
    public PageResDTO<BoardDTO> listGet(@RequestParam("path") String path, @RequestParam("size")int size, @RequestParam("category") String category, @RequestParam("page") int page, @RequestParam("keyword") String keyword, @RequestParam("type") String type)
      {
        PageReqDTO pageReqDTO = PageReqDTO.builder()
            .size(size)
            .page(page)
            .keyword(keyword)
            .type(type)
            .category(category)
            .path(path)
            .build();
        PageResDTO<BoardDTO> getList = boardService.list(pageReqDTO);

        return getList;
      }

    @PutMapping("/modify")
    public void modifyPost(@Valid @RequestBody BoardDTO boardDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes)
      {
        if (bindingResult.hasErrors())
          {
            boardService.modify(boardDTO);
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
          }
        boardService.modify(boardDTO);
      }

    @DeleteMapping("/delete/{bno}")
    public void delete(@PathVariable("bno") Long bno, @RequestParam("userId") String userId)
      {
        if (boardService.writerCheck(bno, userId))
          {
            boardService.delete(bno);
          }
      }

    @DeleteMapping("/delete/{bno}/admin")
    public void delete(@PathVariable("bno") Long bno, @RequestParam("userId") String userId, @RequestParam("communityName") String communityName)
      {
        if (userService.findUserRoles(userId).contains(userRoleService.findRoleId("ADMIN_" + communityName)))
          {
            boardService.delete(bno);
          }
      }
  }
