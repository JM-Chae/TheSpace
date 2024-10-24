package com.thespace.thespace.service;

import com.thespace.thespace.domain.Board;
import com.thespace.thespace.domain.Like;
import com.thespace.thespace.domain.Reply;
import com.thespace.thespace.domain.User;
import com.thespace.thespace.dto.LikeDTO;
import com.thespace.thespace.exception.PostNotFound;
import com.thespace.thespace.exception.ReplyNotFound;
import com.thespace.thespace.exception.UserNotFound;
import com.thespace.thespace.repository.BoardRepository;
import com.thespace.thespace.repository.LikeRepository;
import com.thespace.thespace.repository.ReplyRepository;
import com.thespace.thespace.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeServiceImpl implements LikeService
  {
    private LikeRepository likeRepository;
    private UserRepository userRepository;
    private BoardRepository boardRepository;
    private ReplyRepository replyRepository;

    @Autowired
    public void setRepository(LikeRepository likeRepository, UserRepository userRepository, BoardRepository boardRepository, ReplyRepository replyRepository)
      {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
        this.replyRepository = replyRepository;
      }

    @Transactional
    public int like(LikeDTO likeDTO)
      {
        String result = likeDTO.getBno() > 0L ? "bno" : likeDTO.getRno() > 0L ? "rno" : "fail";
        String userId = likeDTO.getUserId();
        User user = userRepository.findById(userId).orElseThrow(UserNotFound::new);
        int res =0;

        switch (result)
          {
            case "bno":
            {
              Board board = boardRepository.findById(likeDTO.getBno()).orElseThrow(PostNotFound::new);
              boolean exists = likeRepository.findByBoardAndUser(board, user).isPresent();

              if (exists)
                {
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

            case "rno":
            {
              Reply reply = replyRepository.findById(likeDTO.getRno()).orElseThrow(ReplyNotFound::new);
              boolean exists = likeRepository.findByReplyAndUser(reply, user).isPresent();

              if (exists)
                {
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
