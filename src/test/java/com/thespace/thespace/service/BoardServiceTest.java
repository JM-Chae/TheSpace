package com.thespace.thespace.service;

import com.thespace.thespace.domain.Board;
import com.thespace.thespace.dto.BoardDTO;
import com.thespace.thespace.exception.PostNotFound;
import com.thespace.thespace.repository.BoardRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class BoardServiceTest
  {
    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardRepository boardRepository;

    @AfterEach
    public void clean()
      {
        boardRepository.deleteAll();
      }

    @Test
    void testExceptionAll()
      {
        createTestDB();
        testReadException();
        testModifyException();
        testDeleteException();
      }

    Board board;
    BoardDTO boardDTO;

    void createTestDB()
      {
        board = Board.builder()
            .title("Title")
            .content("Content")
            .build();
        boardRepository.save(board);
        boardDTO = BoardDTO.builder()
            .bno(board.getBno()+1L)
            .title(board.getTitle())
            .content(board.getContent())
            .build();
      }

    void testReadException()
      {
        Assertions.assertThrows(PostNotFound.class,()-> boardService.read(board.getBno()+1L));
      }

    void testModifyException()
      {
        Assertions.assertThrows(PostNotFound.class,()-> boardService.modify(boardDTO));
      }

    void testDeleteException()
      {
        Assertions.assertThrows(PostNotFound.class,()-> boardService.delete(board.getBno()+1L));
      }
  }