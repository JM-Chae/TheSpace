package com.thespace.spaceweb.repository;


import com.thespace.spaceweb.domain.BoardFile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardFileRepository extends  JpaRepository<BoardFile, String>
  {
    List<BoardFile> findByBoardBno(Long bno);
    void deleteByBoard_bno(Long bno);
  }
