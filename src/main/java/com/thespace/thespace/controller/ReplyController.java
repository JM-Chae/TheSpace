package com.thespace.thespace.controller;

import com.thespace.thespace.dto.page.PageResDTO;
import com.thespace.thespace.dto.reply.ReplyDTO;
import com.thespace.thespace.dto.reply.ReplyRegisterDTO;
import com.thespace.thespace.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/board/{bno}/reply")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping
    public void register(@PathVariable("bno") Long bno,
        @RequestBody ReplyRegisterDTO replyRegisterDTO, Authentication authentication) {
        replyService.register(bno, replyRegisterDTO, authentication);
    }

    @DeleteMapping("/{rno}")
    public void delete(@PathVariable("bno") Long bno, @PathVariable("rno") Long rno, Authentication authentication) {
        replyService.delete(bno, rno, authentication);
    }

    @GetMapping
    public PageResDTO<ReplyDTO> list(@PathVariable("bno") Long bno) {

        return replyService.getListReply(bno);
    }

    @GetMapping("/{rno}")
    public PageResDTO<ReplyDTO> nestedListGet(@PathVariable("rno") Long rno,
        @PathVariable("bno") Long bno) {

        return replyService.getListNestedReply(rno, bno);
    }
}
