package com.thespace.thespace.controller;

import com.google.gson.Gson;
import com.thespace.thespace.domain.Category;
import com.thespace.thespace.domain.Community;
import com.thespace.thespace.dto.BoardDTO;
import com.thespace.thespace.repository.BoardRepository;
import com.thespace.thespace.repository.CategoryRepository;
import com.thespace.thespace.repository.CommunityRepository;
import com.thespace.thespace.service.BoardService;
import lombok.extern.log4j.Log4j2;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc
@WithMockUser(username = "123456")
public class BoardControllerTests
  {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private BoardService boardService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Gson gson;

//    @BeforeEach
//    public void clean()
//      {
//        boardRepository.deleteAll();
//        categoryRepository.deleteAll();
//        communityRepository.deleteAll();
//      }


        @Test
        public void runAllTest() throws Exception
          {
            creatToTestDB();
            testPost();
//            testRead();
            testModify(); //testReadAndModify
            testDelete();
            testList();
            testBoardNotFoundException();
          }

    BoardDTO boardDTO = new BoardDTO();
    BoardDTO boardDTO2 = new BoardDTO();
    Long bno;
    String content;

    public void creatToTestDB()
      {
        //Create Test Commu
        Community community = Community.builder()
            .communityName("commu12134")
            .build();
        Long communityId = communityRepository.save(community).getCommunityId();
        Optional<Community> communityOptional = communityRepository.findById(communityId);

        //Create Test Cate
        Category category = Category.builder()
            .categoryName("cat11e")
            .categoryType("open11")
            .community(communityOptional.orElseThrow())
            .path(communityOptional.get().getCommunityName())
            .build();
        String categoryName = categoryRepository.save(category).getCategoryName();

        boardDTO = BoardDTO.builder()
            .title("haha")
            .content("hihi")
            .writer("hoho")
            .categoryName(categoryName)
            .build();

        content = gson.toJson(boardDTO);
        bno = boardService.post(boardDTO);

        //List test DB

        for(int i = 1; i <= 20; i++)
          {
            boardDTO2 = BoardDTO.builder()
                .title("testList "+i)
                .content("testContent "+i)
                .writer("tester "+i)
                .categoryName(categoryName)
                .build();
            boardService.post(boardDTO2);
          }
      }

    public void testPost() throws Exception
      {
        mockMvc.perform(MockMvcRequestBuilders.post("/board/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string(String.valueOf((bno + (21L)))))
            .andDo(MockMvcResultHandlers.print());
      }

    public void testRead() throws Exception
      {
        mockMvc.perform(MockMvcRequestBuilders.get("/board/read/{bno}", bno)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(boardDTO.getTitle()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(boardDTO.getContent()))
            .andDo(MockMvcResultHandlers.print());
      }

    public void testModify() throws Exception
      {
        boardDTO = BoardDTO.builder().bno(bno).title("modify").content("modify").build();
        content = gson.toJson(boardDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/board/modify")
                .header("boardDTO", boardDTO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
            .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        Thread.sleep(1000);
        testRead();
      }

    public void testDelete() throws Exception
      {
        mockMvc.perform(MockMvcRequestBuilders.delete("/board/delete/{bno}", bno))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print());

        if (boardRepository.existsById(bno))
          {
            throw new Exception();
          }
      }

    public void testList() throws Exception
      {

        mockMvc.perform(MockMvcRequestBuilders.get("/board/list")
                .param("page", "2")
                .param("type", "t")
                .param("keyword", "testList 1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.dtoList.length()", Matchers.is(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.total", Matchers.is(11)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dtoList[0].title").value("testList 1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dtoList[0].writer").value("tester 1"))
            .andDo(MockMvcResultHandlers.print());
      }

    public void testBoardNotFoundException() throws Exception
      {
        mockMvc.perform(MockMvcRequestBuilders.delete("/board/delete/{bno}", 111111L))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andDo(MockMvcResultHandlers.print());

        boardDTO = BoardDTO.builder().bno(111111L).title("modify").content("modify").build();
        content = gson.toJson(boardDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/board/modify")
                .header("boardDTO", boardDTO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andDo(MockMvcResultHandlers.print());

        mockMvc.perform(MockMvcRequestBuilders.get("/board/read/{bno}", 111111L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
           .andDo(MockMvcResultHandlers.print());

      }

  }