package com.thespace.thespace.repository;

import com.thespace.thespace.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long>
  {
  }
