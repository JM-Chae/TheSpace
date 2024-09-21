package com.thespace.thespace.controller;

import com.google.gson.Gson;
import com.thespace.thespace.domain.Board;
import com.thespace.thespace.domain.Category;
import com.thespace.thespace.domain.Community;
import com.thespace.thespace.domain.Reply;
import com.thespace.thespace.dto.ReplyDTO;
import com.thespace.thespace.repository.BoardRepository;
import com.thespace.thespace.repository.CategoryRepository;
import com.thespace.thespace.repository.CommunityRepository;
import com.thespace.thespace.repository.ReplyRepository;
import com.thespace.thespace.service.ReplyService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;


@SpringBootTest
@Log4j2
@AutoConfigureMockMvc
class ReplyControllerTests
  {
    @Autowired
    private ReplyService replyService;
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Gson gson;

    @BeforeEach
    public void clean()
      {
        replyRepository.deleteAll();
        boardRepository.deleteAll();
        categoryRepository.deleteAll();
        communityRepository.deleteAll();
      }


    @Test
    public void runAllTest() throws Exception
      {
        creatToTestDB();
        testRegister();
        testDelete();
        testGetList();
      }

    ReplyDTO replyDTO, replyDTO2 = new ReplyDTO();
    String content;
    Long rno;
    Long bno;
    Optional<Reply> replyOptional;
    Optional<Reply> replyOptional2;

    public void creatToTestDB()
      {
        //Create Test Commu
        Community community = Community.builder()
            .communityName("commu")
            .build();
        Long communityId = communityRepository.save(community).getCommunityId();
        Optional<Community> communityOptional = communityRepository.findById(communityId);

        //Create Test Cate
        Category category = Category.builder()
            .categoryName("cate")
            .categoryType("open")
            .community(communityOptional.orElseThrow())
            .path(communityOptional.get().getCommunityName())
            .build();
        Long categoryId = categoryRepository.save(category).getCategoryId();
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);

        Board board = Board.builder()
            .title("haha")
            .content("hihi")
            .writer("hoho")
            .category(categoryOptional.orElseThrow())
            .path(categoryOptional.get().getCategoryName())
            .build();
        bno = boardRepository.save(board).getBno();
        Optional<Board> boardOptional = boardRepository.findById(bno);

        replyDTO = ReplyDTO.builder()
            .replyContent("Test")
            .replyWriter("Test")
            .bno(bno)
            .path(bno.toString())
            .build();

        content = gson.toJson(replyDTO);
        rno = replyService.register(bno, replyDTO);

        for(int i = 1; i <= 60; i++)
          {
            replyDTO2 = ReplyDTO.builder()
                .replyContent("testContent "+i)
                .replyWriter("tester "+i)
                .bno(bno)
                .path(bno.toString())
                .build();
            replyService.register(bno, replyDTO2);
          }
      }

    public void testRegister() throws Exception
      {
        mockMvc.perform(MockMvcRequestBuilders.post("/board/{bno}/reply", bno)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print());

        replyOptional = replyRepository.findById(rno);
        replyOptional2 = replyRepository.findById(rno + 1L);
        List<Reply> result1 = replyOptional.stream().toList();
        List<Reply> result2 = replyOptional2.stream().toList();

        if(result1.equals(result2))
          {
            throw new Exception();
          }
      }

    public void testDelete() throws Exception
      {
        mockMvc.perform(MockMvcRequestBuilders.delete("/reply/{rno}", rno)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print());

        if(replyRepository.existsById(rno))
          {
            throw new Exception();
          }
      }

    public void testGetList() throws Exception
      {
        mockMvc.perform(MockMvcRequestBuilders.get("/board/{bno}/reply", bno)
            .contentType(MediaType.APPLICATION_JSON)
                .param("page", "2"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.dtoList[0].replyContent").value("testContent "+ 31))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dtoList[10].replyContent").value("testContent "+ 41))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dtoList[20].replyContent").value("testContent "+ 51))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dtoList[29].replyContent").value("testContent "+ 60))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dtoList[0].replyWriter").value("tester "+ 31))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dtoList[10].replyWriter").value("tester "+ 41))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dtoList[20].replyWriter").value("tester "+ 51))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dtoList[29].replyWriter").value("tester "+ 60))
            .andDo(MockMvcResultHandlers.print());
      }
  }