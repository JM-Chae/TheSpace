package com.thespace.spaceweb.like;

import com.thespace.spaceweb.board.Board;
import com.thespace.spaceweb.reply.Reply;
import com.thespace.spaceweb.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByBoardAndUser(Board board, User user);

    Optional<Like> findByReplyAndUser(Reply reply, User user);

    void deleteByBoardAndUser(Board board, User user);

    void deleteByReplyAndUser(Reply reply, User user);

}
