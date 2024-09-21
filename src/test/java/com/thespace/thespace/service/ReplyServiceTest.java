package com.thespace.thespace.service;

import com.thespace.thespace.domain.Board;
import com.thespace.thespace.dto.ReplyDTO;
import com.thespace.thespace.repository.BoardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ReplyServiceTest
  {
    @Autowired
    private ReplyService replyService;
    @Autowired
    private BoardRepository boardRepository;

    @Test
    void reply()
      {
        Board board = Board.builder()
            .content("1234")
            .writer("1234")
            .title("1234")
            .build();
        boardRepository.save(board);

        Long bno = board.getBno();
        ReplyDTO replyDTO = ReplyDTO.builder()
            .replyContent("test")
            .replyWriter("test")
            .bno(bno)
            .path("Board")
            .build();

        replyService.register(bno, replyDTO);
      }
  }