package com.thespace.thespace.repository;

import com.thespace.thespace.domain.Board;
import com.thespace.thespace.domain.Like;
import com.thespace.thespace.domain.Reply;
import com.thespace.thespace.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByBoardAndUser(Board board, User user);

    Optional<Like> findByReplyAndUser(Reply reply, User user);

    void deleteByBoardAndUser(Board board, User user);

    void deleteByReplyAndUser(Reply reply, User user);

}
