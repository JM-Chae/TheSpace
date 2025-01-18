package com.thespace.thespace.controller;

import com.thespace.thespace.dto.page.PageReqDTO;
import com.thespace.thespace.dto.page.PageResDTO;
import com.thespace.thespace.dto.reply.ReplyDTO;
import com.thespace.thespace.dto.reply.ReplyRegisterDTO;
import com.thespace.thespace.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/board/{bno}/reply")
    public void register(@PathVariable("bno") Long bno,
        @RequestBody ReplyRegisterDTO replyRegisterDTO) {
        replyService.register(bno, replyRegisterDTO);
    }

    @DeleteMapping("/reply/{rno}")
    public void delete(@PathVariable("rno") Long rno, @RequestParam("bno") Long bno) {
        replyService.delete(bno, rno);
    }

    @GetMapping("/board/{bno}/reply")
    public PageResDTO<ReplyDTO> list(@PathVariable("bno") Long bno, PageReqDTO pageReqDTO) {

        return replyService.getListReply(bno, pageReqDTO);
    }

    @GetMapping("/board/{bno}/reply/{rno}")
    public PageResDTO<ReplyDTO> nestedListGet(@PathVariable("rno") Long rno,
        @PathVariable("bno") Long bno, PageReqDTO pageReqDTO) {

        return replyService.getListNestedReply(rno, bno, pageReqDTO);
    }
}
