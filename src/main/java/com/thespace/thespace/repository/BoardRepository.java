package com.thespace.thespace.repository;

import com.thespace.thespace.domain.Board;
import com.thespace.thespace.repository.getList.GetListBoard;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long>, GetListBoard
  {
    @EntityGraph(attributePaths = {"fileSet"})
    @Query("select b from Board b where b.bno =:bno")
    Optional<Board> findByIdWithFiles(Long bno);
  }
