package com.thespace.thespace.controller;

import com.thespace.thespace.dto.PageReqDTO;
import com.thespace.thespace.dto.PageResDTO;
import com.thespace.thespace.dto.ReplyDTO;
import com.thespace.thespace.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public void delete(@PathVariable("rno")Long rno, @RequestParam("bno") Long bno)
      {
        replyService.delete(bno, rno);
      }

    @GetMapping("/board/{bno}/reply")
    public PageResDTO<ReplyDTO> listGet(@PathVariable("bno") Long bno, PageReqDTO pageReqDTO)
      {
        PageResDTO<ReplyDTO> rdtoList = replyService.getListReply(bno, pageReqDTO);

        return rdtoList;
      }

    @GetMapping("/board/{bno}/reply/{rno}")
    public PageResDTO<ReplyDTO> nestedListGet(@PathVariable("rno") Long rno, @PathVariable("bno") Long bno, PageReqDTO pageReqDTO)
      {
        PageResDTO<ReplyDTO> nrdtoList = replyService.getListNestedReply(rno, bno, pageReqDTO);

        return nrdtoList;
      }
  }
