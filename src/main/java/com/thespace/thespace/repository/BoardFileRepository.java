package com.thespace.thespace.repository;


import com.thespace.thespace.domain.BoardFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardFileRepository extends  JpaRepository<BoardFile, String>
  {
    List<BoardFile> findByBoardBno(Long bno);
    void deleteByBoard_bno(Long bno);
  }
