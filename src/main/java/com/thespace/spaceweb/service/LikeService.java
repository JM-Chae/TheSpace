package com.thespace.spaceweb.service;

import com.thespace.spaceweb.domain.Board;
import com.thespace.spaceweb.domain.Like;
import com.thespace.spaceweb.domain.Reply;
import com.thespace.spaceweb.domain.User;
import com.thespace.spaceweb.dto.LikeDTO;
import com.thespace.spaceweb.exception.PostNotFound;
import com.thespace.spaceweb.exception.ReplyNotFound;
import com.thespace.spaceweb.repository.BoardRepository;
import com.thespace.spaceweb.repository.LikeRepository;
import com.thespace.spaceweb.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;

    @Transactional
    public int like(LikeDTO likeDTO, Authentication authentication) {
        String result = likeDTO.bno() > 0L ? "bno" : likeDTO.rno() > 0L ? "rno" : "fail";
        User user = (User) authentication.getPrincipal();
        int res = 0;
        switch (result) {
            case "bno": {
                Board board = boardRepository.findById(likeDTO.bno())
                    .orElseThrow(PostNotFound::new);
                boolean exists = likeRepository.findByBoardAndUser(board, user).isPresent();

                if (exists) {
                    likeRepository.deleteByBoardAndUser(board, user);
                    board.setVote(board.getVote() - 1L);
                    boardRepository.save(board);
                    res = -1;

                    break;
                }

                Like like = Like.builder()
                    .board(board)
                    .user(user)
                    .build();
                board.setVote(board.getVote() + 1L);
                boardRepository.save(board);
                likeRepository.save(like);
                res = 1;

                break;
            }

            case "rno": {
                Reply reply = replyRepository.findById(likeDTO.rno())
                    .orElseThrow(ReplyNotFound::new);
                boolean exists = likeRepository.findByReplyAndUser(reply, user).isPresent();

                if (exists) {
                    likeRepository.deleteByReplyAndUser(reply, user);
                    reply.setVote(reply.getVote() - 1L);
                    replyRepository.save(reply);
                    res = -1;

                    break;
                }

                Like like = Like.builder()
                    .reply(reply)
                    .user(user)
                    .build();
                reply.setVote(reply.getVote() + 1L);
                replyRepository.save(reply);
                likeRepository.save(like);
                res = 1;

                break;
            }
            case "fail":
                break; //Exception ?
        }
        return res;
    }
}
