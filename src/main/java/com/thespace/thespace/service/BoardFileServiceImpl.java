package com.thespace.thespace.service;

import com.thespace.thespace.domain.Board;
import com.thespace.thespace.domain.BoardFile;
import com.thespace.thespace.repository.BoardFileRepository;
import com.thespace.thespace.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class BoardFileServiceImpl implements BoardFileService
  {
    private BoardFileRepository boardFileRepository;
    private BoardRepository boardRepository;

    @Autowired
    public void setBoardFileRepository(BoardFileRepository boardFileRepository, BoardRepository boardRepository)
      {
        this.boardFileRepository = boardFileRepository;
        this.boardRepository = boardRepository;
      }

    public void deleteBoardFile(String boardFileName)
      {
        Optional<BoardFile> boardFile = boardFileRepository.findById(boardFileName);
        if(boardFile.isPresent())
          {
            Long bno = boardFile.get().getBoard().getBno();
            Optional<Board> board = boardRepository.findById(bno);

            if (board.isPresent())
              {
                Set<BoardFile> fileSet = board.get().getFileSet();
                fileSet.remove(boardFile.get());
                boardRepository.save(board.get());
              }

            boardFileRepository.deleteById(boardFileName);
          }



      }
  }
