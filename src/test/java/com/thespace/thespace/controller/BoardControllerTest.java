package com.thespace.thespace.controller;

import static com.thespace.thespace.config.RestDocsConfig.restDocsConfig;
import static com.thespace.thespace.config.RestDocsConfig.write;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thespace.thespace.config.DataBaseCleaner;
import com.thespace.thespace.domain.Board;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@ActiveProfiles("test")
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
            new ArrayList<>()
        ));

        Category category = categoryRepository.save(new Category(
            community.getCommunityName(),
            "test",
            "test",
            community,
            new ArrayList<>()
        ));

        BoardPostDTO boardPostDTO = new BoardPostDTO(
            "test1",
            "content1",
            "testUser",
            "testUser",
            new ArrayList<>(),
            category.getCategoryId()
        );

        //when
        ResultActions result = mockMvc.perform(post("/board").content(objectMapper.writeValueAsString(
            boardPostDTO)).contentType(MediaType.APPLICATION_JSON));

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
    void boardRead() throws Exception {
        //given
        Community community = communityRepository.save(new Community(
            "test",
            "test",
            new ArrayList<>()
        ));

        Category category = categoryRepository.save(new Category(
            community.getCommunityName(),
            "test",
            "test",
            community,
            new ArrayList<>()
        ));

        Board board = boardRepository.save(new Board(
            "test",
            community.getCommunityName(),
            "content",
            "123456",
            "A24WS122A",
            category
        ));

        //when
        ResultActions result = mockMvc.perform(get("/board/{bno}", board.getBno()));

        //then
        result.andExpect(status().isOk()).andExpectAll(
            jsonPath("$.title").value(board.getTitle()),
            jsonPath("$.path").value(board.getPath()),
            jsonPath("$.content").value(board.getContent()),
            jsonPath("$.writer").value("123456"),
            jsonPath("$.writerUuid").value("A24WS122A"),
            jsonPath("$.categoryId").value(category.getCategoryId())
        );

        //docs
        result.andDo(write().document(responseFields(
            fieldWithPath("bno").description(
                "Number of Board"),
            fieldWithPath("title").description("Title"),
            fieldWithPath("path").description("Name of community"),
            fieldWithPath("content").description("Content"),
            fieldWithPath("writer").description("Writer"),
            fieldWithPath("writerUuid").description("WriterUuid"),
            fieldWithPath("createDate").description("Create date"),
            fieldWithPath("modDate").description("Modified date"),
            fieldWithPath("viewCount").description("View count / def = 0"),
            fieldWithPath("vote").description("Vote count / def = 0"),
            fieldWithPath("rCount").description("Reply count / def = 0"),
            fieldWithPath("fileNames").description("Name of attached files"),
            fieldWithPath("categoryId").description("Category Id")
        )));
    }

//    @Test
//    void boardList() throws Exception {
//        //given
//        Community community = communityRepository.save(new Community(
//            "test",
//            "test",
//            new ArrayList<>()
//        ));
//
//        Category category = categoryRepository.save(new Category(
//            community.getCommunityName(),
//            "test",
//            "test",
//            community,
//            new ArrayList<>()
//        ));
//
//        for (int i = 1; i <= 100; i++) {
//            Board board = boardRepository.save(new Board(
//                "test" + i,
//                community.getCommunityName(),
//                "content" + i,
//                "tester" + i % 3,
//                "tester " + i % 3,
//                category
//            ));
//        }
//
//        PageReqDTO pageReqDTO = new PageReqDTO(1, 10, "t", "1", "", "");
//
//        //when
//        ResultActions result = mockMvc.perform(get("/board/list")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(pageReqDTO)));
//
//        //then
//        result.andExpect(status().isOk());
//
//        //docs
//        result.andDo(write().document(
//            requestFields())
//        );
//    }

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