package com.thespace.thespace.repository;

import com.thespace.thespace.domain.Board;
import com.thespace.thespace.service.BoardService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@Log4j2
@SpringBootTest
class BoardRepositoryTests
  {
    @Autowired
    BoardRepository boardRepository;

    @Autowired
    BoardService boardService;

    @Test
    public void registerTest()
      {
        Board board = Board.builder()
            .title("testTitle")
            .content("Test content")
            .writer("tester")
            .build();

        Long bno = boardRepository.save(board).getBno();
        log.info(bno);
      }
  }