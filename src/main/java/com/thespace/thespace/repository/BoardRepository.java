package com.thespace.thespace.repository;

import com.thespace.thespace.domain.Board;
import com.thespace.thespace.repository.getList.GetListBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long>, GetListBoard
  {
  }
