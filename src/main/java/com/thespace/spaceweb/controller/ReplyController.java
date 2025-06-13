package com.thespace.spaceweb.controller;

import com.thespace.spaceweb.dto.ReplyDTOs;
import com.thespace.spaceweb.dto.ReplyDTOs.Info;
import com.thespace.spaceweb.dto.page.PageResDTO;
import com.thespace.spaceweb.service.ReplyService;
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
        @RequestBody ReplyDTOs.Register replyRegisterDTO, Authentication authentication) {
        replyService.register(bno, replyRegisterDTO, authentication);
    }

    @DeleteMapping("/{rno}")
    public void delete(@PathVariable("bno") Long bno, @PathVariable("rno") Long rno, Authentication authentication) {
        replyService.delete(bno, rno, authentication);
    }

    @GetMapping
    public PageResDTO<Info> list(@PathVariable("bno") Long bno) {

        return replyService.getListReply(bno);
    }

    @GetMapping("/{rno}")
    public PageResDTO<Info> nestedListGet(@PathVariable("rno") Long rno,
        @PathVariable("bno") Long bno) {

        return replyService.getListNestedReply(rno, bno);
    }
}
