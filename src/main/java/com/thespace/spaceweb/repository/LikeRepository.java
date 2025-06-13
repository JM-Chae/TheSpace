package com.thespace.spaceweb.repository;

import com.thespace.spaceweb.domain.Board;
import com.thespace.spaceweb.domain.Like;
import com.thespace.spaceweb.domain.Reply;
import com.thespace.spaceweb.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByBoardAndUser(Board board, User user);

    Optional<Like> findByReplyAndUser(Reply reply, User user);

    void deleteByBoardAndUser(Board board, User user);

    void deleteByReplyAndUser(Reply reply, User user);

}
