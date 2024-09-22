package com.thespace.thespace.service;

import com.thespace.thespace.domain.Board;
import com.thespace.thespace.dto.BoardDTO;
import com.thespace.thespace.dto.PageReqDTO;
import com.thespace.thespace.dto.PageResDTO;


public interface BoardService
  {
    Long post(BoardDTO boardDTO);
    BoardDTO read(Long bno);
    void modify(BoardDTO boardDTO);
    void delete(Long bno);
    PageResDTO<BoardDTO> list(PageReqDTO pageReqDTO);

    default Board dtoToEntity(BoardDTO boardDTO)
      {
        Board board = new Board();
        return board;
      }
  }
