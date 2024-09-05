package com.thespace.thespace.service;

import com.thespace.thespace.domain.Board;
import com.thespace.thespace.dto.BoardDTO;
import com.thespace.thespace.repository.BoardRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService
  {
    private BoardRepository boardRepository;

    @Autowired
    public void setBoardRepository(BoardRepository boardRepository)
      {
        this.boardRepository = boardRepository;
      }

    public Long register(BoardDTO boardDTO)
      {
        Board board = Board.builder()
            .bno(boardDTO.getBno())
            .title(boardDTO.getTitle())
            .content(boardDTO.getContent())
            .writer(boardDTO.getWriter())
            .build();

        Long bno = boardRepository.save(board).getBno();
        return bno;
      }
  }
