package com.thespace.spaceweb.board;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardFileRepository extends  JpaRepository<BoardFile, String>
  {
    List<BoardFile> findByBoardBno(Long bno);
    void deleteByBoard_bno(Long bno);
  }
