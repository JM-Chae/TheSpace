package com.thespace.thespace.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thespace.thespace.domain.Board;
import com.thespace.thespace.domain.BoardFile;
import com.thespace.thespace.domain.Category;
import com.thespace.thespace.domain.Community;
import com.thespace.thespace.dto.BoardDTO;
import com.thespace.thespace.repository.BoardFileRepository;
import com.thespace.thespace.repository.BoardRepository;
import com.thespace.thespace.repository.CategoryRepository;
import com.thespace.thespace.repository.CommunityRepository;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

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
    private BoardFileRepository boardFileRepository;
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
    @Transactional
    @Rollback(false)
    public void runAllTest() throws Exception
      {
        creatToTestDB();
        testFileUpload();
        testFileGet();
        testFileDelete();
      }

    BoardDTO boardDTO = new BoardDTO();
    Long bno;
    String content;
    String categoryName;
    String filename;

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
        categoryName = categoryRepository.save(category).getCategoryName();


      }

    public void testFileUpload() throws Exception
      {
        String fileName = "file.png";
        Resource resource = resourceLoader.getResource("classpath:/static/" + fileName);
        MockMultipartFile file = new MockMultipartFile("fileList", fileName, MediaType.IMAGE_PNG_VALUE, resource.getInputStream());

        MvcResult result1 = mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
        .file(file).contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andReturn();

        String res1 = result1.getResponse().getContentAsString();
        JsonArray jsonArray = JsonParser.parseString(res1).getAsJsonArray();
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        String name = jsonObject.get("fileId").getAsString().replace("\"","")+"_"+fileName;

        List<String> fileNames = List.of(name);
        log.info(fileNames.toString());
        boardDTO = BoardDTO.builder()
            .title("haha")
            .content("hihi")
            .writer("hoho")
            .categoryName(categoryName)
            .fileNames(fileNames)
            .build();

        content = gson.toJson(boardDTO);

        MvcResult result2 = mockMvc.perform(MockMvcRequestBuilders.post("/board/post")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andReturn();

        String res2 = result2.getResponse().getContentAsString();
        bno = Long.valueOf(res2.replace("redirect:/board/read/", ""));
      }

    public void testFileGet() throws Exception
      {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/board/read/{bno}", bno)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andReturn();

        String res1 = result.getResponse().getContentAsString();
        JsonObject jsonObject = JsonParser.parseString(res1).getAsJsonObject();
        filename = jsonObject.get("fileNames").getAsString().replace("\"","");

        log.info(filename);

        mockMvc.perform(MockMvcRequestBuilders.get("/get/{filename}", filename))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print());
      }

    public void testFileDelete() throws Exception
    {
      mockMvc.perform(MockMvcRequestBuilders.delete("/delete/{filename}", filename))
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andDo(MockMvcResultHandlers.print());

      Optional<Board> boardOptional = boardRepository.findById(bno);
      String fileId = filename.substring(0,8);
      Optional<BoardFile> boardFile = boardFileRepository.findById(fileId);
      if (boardOptional.isPresent())
        {

          boolean result = !boardOptional.get().getFileSet().contains(boardFile);
              log.info(result ? "Successfully deleted" : "Failed to deleted");
        }
    }
  }