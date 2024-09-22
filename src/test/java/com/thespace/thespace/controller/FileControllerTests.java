package com.thespace.thespace.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thespace.thespace.domain.Category;
import com.thespace.thespace.domain.Community;
import com.thespace.thespace.dto.BoardDTO;
import com.thespace.thespace.repository.BoardRepository;
import com.thespace.thespace.repository.CategoryRepository;
import com.thespace.thespace.repository.CommunityRepository;
import com.thespace.thespace.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class FileControllerTests
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
    @Autowired
    ResourceLoader resourceLoader;

    @BeforeEach
    public void clean()
      {
        boardRepository.deleteAll();
        categoryRepository.deleteAll();
        communityRepository.deleteAll();
      }


    @Test
    public void runAllTest() throws Exception
      {
        creatToTestDB();
        testFileUpload();
      }

    BoardDTO boardDTO = new BoardDTO();
    Long bno;
    String content;
    Long categoryId;

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
        categoryId = categoryRepository.save(category).getCategoryId();


      }

    public void testFileUpload() throws Exception
      {
        String fileName = "file.png";
        Resource resource = resourceLoader.getResource("classpath:/static/" + fileName);
        MockMultipartFile file = new MockMultipartFile("fileList", fileName, MediaType.IMAGE_PNG_VALUE, resource.getInputStream());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
        .file(file).contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andReturn();

        String res = result.getResponse().getContentAsString();
        JsonArray jsonArray = new JsonParser().parse(res).getAsJsonArray();
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        String name = jsonObject.get("fileId").getAsString().replace("\"","")+"_"+fileName;

        List<String> fileNames = List.of(name);
        log.info(fileNames.toString());
        boardDTO = BoardDTO.builder()
            .title("haha")
            .content("hihi")
            .writer("hoho")
            .categoryId(categoryId)
            .fileNames(fileNames)
            .build();

        content = gson.toJson(boardDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/board/post")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print());
      }
  }