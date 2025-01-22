package com.thespace.thespace.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.thespace.thespace.config.DataBaseCleaner;
import com.thespace.thespace.domain.Category;
import com.thespace.thespace.domain.Community;
import com.thespace.thespace.dto.board.BoardPostDTO;
import com.thespace.thespace.repository.BoardRepository;
import com.thespace.thespace.repository.CategoryRepository;
import com.thespace.thespace.repository.CommunityRepository;
import io.github.hejow.restdocs.generator.Document;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @Autowired
    private DataBaseCleaner dataBaseCleaner;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    @BeforeEach
    void setup(
        WebApplicationContext webApplicationContext,
        RestDocumentationContextProvider restDocumentation
    ) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .alwaysDo(print())
            .apply(documentationConfiguration(restDocumentation))
            .addFilter(new CharacterEncodingFilter("UTF-8", true))
            .build();
        dataBaseCleaner.clear();
    }

    @Test
    void post() throws Exception {
        //given
        Community community = communityRepository.save(
            new Community(null, "test", "test", new ArrayList<>()));
        Category category = categoryRepository.save(
            new Category(null, community.getCommunityName(), "test", "test", community,
                new ArrayList<>()));

        BoardPostDTO boardPostDTO = new BoardPostDTO("test1", "content1", "testUser", "testUser",
            new ArrayList<>(), category.getCategoryId());
        String content = gson.toJson(boardPostDTO);

        //when
        ResultActions result = mockMvc.perform(
            RestDocumentationRequestBuilders.post("/board").content(content).contentType(
                MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk());

        //docs
        result.andDo(Document.builder()
            .identifier("Board Post Success")
            .tag(Tag.USER)
            .summary("Post new Board")
            .description("Post new Board")
            .result(result)
            .buildAndGenerate());
    }

    @Test
    void read() {
    }

    @Test
    void list() {
    }

    @Test
    void modifyPost() {
    }

    @Test
    void delete() {
    }

    @Test
    void testDelete() {
    }
}