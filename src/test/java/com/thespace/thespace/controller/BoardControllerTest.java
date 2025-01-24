package com.thespace.thespace.controller;

import static com.thespace.thespace.config.RestDocsConfig.restDocsConfig;
import static com.thespace.thespace.config.RestDocsConfig.write;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thespace.thespace.config.DataBaseCleaner;
import com.thespace.thespace.domain.Category;
import com.thespace.thespace.domain.Community;
import com.thespace.thespace.dto.board.BoardPostDTO;
import com.thespace.thespace.repository.BoardRepository;
import com.thespace.thespace.repository.CategoryRepository;
import com.thespace.thespace.repository.CommunityRepository;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;


@SpringBootTest
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class BoardControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataBaseCleaner dataBaseCleaner;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentation) {
        mockMvc = restDocsConfig(context, restDocumentation);
        dataBaseCleaner.clear();
    }

    @Test
    void boardPost() throws Exception {
        //given
        Community community = communityRepository.save(new Community(
            "test",
            "test",
            new ArrayList<>())
        );

        Category category = categoryRepository.save(
            new Category(
                community.getCommunityName(),
                "test",
                "test", community,
                new ArrayList<>())
        );

        BoardPostDTO boardPostDTO = new BoardPostDTO(
            "test1",
            "content1",
            "testUser",
            "testUser",
            new ArrayList<>(),
            category.getCategoryId()
        );

        //when
        ResultActions result = mockMvc.perform(
            RestDocumentationRequestBuilders.post("/board")
                .content(objectMapper.writeValueAsString(boardPostDTO))
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk());

        //docs
        result.andDo(write().document(requestFields(
            fieldWithPath("title").description("Title"),
            fieldWithPath("content").description("Content"),
            fieldWithPath("writer").description("Writer"),
            fieldWithPath("writerUuid").description("WriterUuid"),
            fieldWithPath("fileNames").description("Name of attached files"),
            fieldWithPath("categoryId").description("Category Id")
        )));
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