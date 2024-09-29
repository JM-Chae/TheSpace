package com.thespace.thespace.service;

import com.thespace.thespace.domain.Board;
import com.thespace.thespace.domain.Reply;
import com.thespace.thespace.domain.User;
import com.thespace.thespace.dto.LikeDTO;
import com.thespace.thespace.repository.BoardRepository;
import com.thespace.thespace.repository.ReplyRepository;
import com.thespace.thespace.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LikeServiceTests
  {
    @Autowired
    private LikeService likeService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private ReplyRepository replyRepository;

    @Test
    void like()
      {
        User user = User.builder().id("testID1").password("testPassword").name("testName").email("test@test.com").uuid("testUUID").build();
        userRepository.save(user);
        Board board = Board.builder()
            .title("testTitle")
            .content("testContent")
            .writer(user.getName())
            .build();
        Long bno = boardRepository.save(board).getBno();
        Reply reply = Reply.builder()
            .replyWriter(user.getName())
            .replyContent("test")
            .board(board)
            .build();
        Long rno = replyRepository.save(reply).getRno();
        LikeDTO likeDTO = LikeDTO.builder().userId(user.getId()).bno(bno).build();
        LikeDTO likeDTO2 = LikeDTO.builder().userId(user.getId()).rno(rno).build();

        likeService.like(likeDTO);
        likeService.like(likeDTO2);
//        likeService.like(likeDTO); delete
//        likeService.like(likeDTO2); delete
      }
  }