package com.thespace.thespace.controller;

import com.thespace.thespace.dto.PageReqDTO;
import com.thespace.thespace.dto.PageResDTO;
import com.thespace.thespace.dto.ReplyDTO;
import com.thespace.thespace.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReplyController
  {
    private final ReplyService replyService;

    @PostMapping("/board/{bno}/reply")
    public void register(@PathVariable("bno")Long bno, @RequestBody ReplyDTO replyDTO)
      {
        replyService.register(bno, replyDTO);
      }

    @DeleteMapping("/reply/{rno}")
    public void delete(@PathVariable("rno")Long rno)
      {
        replyService.delete(rno);
      }

    @GetMapping("/board/{bno}/reply")
    public PageResDTO<ReplyDTO> listGet(@PathVariable("bno") Long bno, PageReqDTO pageReqDTO, Model model)
      {
        PageResDTO<ReplyDTO> rdtoList = replyService.getListReply(bno, pageReqDTO);
        model.addAttribute("rdtoList", rdtoList);

        return rdtoList;
      }
  }
