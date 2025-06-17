package com.thespace.spaceweb.board;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @EntityGraph(attributePaths = {"fileSet"})
    @Query("select b from Board b where b.bno =:bno")
    Optional<Board> findByIdWithFiles(@Param("bno") Long bno);
}
