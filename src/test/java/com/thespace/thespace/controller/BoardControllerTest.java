package com.thespace.thespace.controller;

import static com.thespace.thespace.config.RestDocsConfig.CsrfRestDocumentationRequestBuilders.delete;
import static com.thespace.thespace.config.RestDocsConfig.CsrfRestDocumentationRequestBuilders.patch;
import static com.thespace.thespace.config.RestDocsConfig.CsrfRestDocumentationRequestBuilders.post;
import static com.thespace.thespace.config.RestDocsConfig.write;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.restdocs.cli.CliDocumentation.curlRequest;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.http.HttpDocumentation.httpRequest;
import static org.springframework.restdocs.http.HttpDocumentation.httpResponse;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thespace.thespace.config.DataBaseCleaner;
import com.thespace.thespace.domain.Board;
import com.thespace.thespace.domain.Category;
import com.thespace.thespace.domain.Community;
import com.thespace.thespace.domain.User;
import com.thespace.thespace.domain.UserRole;
import com.thespace.thespace.dto.BoardDTOs.Modify;
import com.thespace.thespace.dto.BoardDTOs.Post;
import com.thespace.thespace.exception.PostNotFound;
import com.thespace.thespace.repository.BoardRepository;
import com.thespace.thespace.repository.CategoryRepository;
import com.thespace.thespace.repository.CommunityRepository;
import com.thespace.thespace.repository.UserRepository;
import com.thespace.thespace.repository.UserRoleRepository;
import com.thespace.thespace.service.UserService;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataBaseCleaner dataBaseCleaner;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @BeforeEach
    void setup() {
        dataBaseCleaner.clear();
        User user = userRepository.save(new User(
            "testerUser",
            "testerUUID",
            "tester",
            "test@test.test",
            "password",
            new ArrayList<>()
        ));
        userRoleRepository.save(new UserRole("ROLE_USER", new ArrayList<>()));
        userService.setRole(user.getId(), "ROLE_USER");
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void boardPost() throws Exception {
        //given
        Community community = communityRepository.save(new Community(
            "test",
            "test"
        ));

        Category category = categoryRepository.save(new Category(
            community.getCommunityName(),
            "test",
            "test",
            community,
            new ArrayList<>()
        ));

        Post postDTO = new Post(
            "test1",
            "content1",
            new ArrayList<>(),
            category.getCategoryId()
        );

        //when
        ResultActions result = mockMvc.perform(post("/board").content(objectMapper.writeValueAsString(
            postDTO)).contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk()).andDo(print());
        Board board = boardRepository.findById(1L).orElseThrow();
        if (!(board.getTitle().equals(postDTO.title()) &&
            board.getContent().equals(postDTO.content()))) {
            throw new Exception();
        }

        //docs
        result.andDo(write().document(requestHeaders(), responseBody(), requestBody(), curlRequest(), httpRequest(), httpResponse(),
            requestFields(
            fieldWithPath("title").description("Title."),
            fieldWithPath("content").description("Content."),
            fieldWithPath("fileNames").description("Name of attached files."),
            fieldWithPath("categoryId").description("Category Id.")
        )));
    }

    @Test
    void boardRead() throws Exception {
        //given
        Community community = communityRepository.save(new Community(
            "test",
            "test"
        ));

        Category category = categoryRepository.save(new Category(
            community.getCommunityName(),
            "test",
            "test",
            community,
            new ArrayList<>()
        ));

        User user = new User(
            "testerUser",
            "testerUUID",
            "tester",
            "test@test.test",
            "password",
            new ArrayList<>()
        );

        Board board = boardRepository.save(new Board(
            "test",
            community.getCommunityName(),
            "content",
            category,
            user
        ));

        //when
        ResultActions result = mockMvc.perform(get("/board/{bno}", board.getBno()));

        //then
        result.andExpect(status().isOk()).andExpectAll(
            jsonPath("$.title").value(board.getTitle()),
            jsonPath("$.path").value(board.getPath()),
            jsonPath("$.content").value(board.getContent()),
            jsonPath("$.writer").value("tester"),
            jsonPath("$.writerUuid").value("testerUUID"),
            jsonPath("$.categoryId").value(category.getCategoryId())
        ).andDo(print());

        //docs
        result.andDo(write().document(requestHeaders(), responseBody(), requestBody(), curlRequest(), httpRequest(), httpResponse(),
            relaxedResponseFields(
            fieldWithPath("bno").description(
                "Number of Board."),
            fieldWithPath("title").description("Title."),
            fieldWithPath("path").description("Name of community."),
            fieldWithPath("content").description("Content."),
            fieldWithPath("writer").description("Writer ID"),
            fieldWithPath("writerUuid").description("Writer UUID"),
            fieldWithPath("createDate").description("Create date."),
            fieldWithPath("modDate").description("Modified date."),
            fieldWithPath("viewCount").description("View count / def = 0."),
            fieldWithPath("vote").description("Vote count / def = 0."),
            fieldWithPath("rCount").description("Reply count / def = 0."),
            fieldWithPath("fileNames").description("Name of attached files."),
            fieldWithPath("categoryId").description("Category Id.")
        ), pathParameters(
            parameterWithName("bno").description(
                "Number of Board.")
        )));
    }

    @Test
    void boardList() throws Exception {
        //given
        Community community = communityRepository.save(new Community(
            "test",
            "test"
        ));

        Category category = categoryRepository.save(new Category(
            community.getCommunityName(),
            "test123",
            "test",
            community,
            new ArrayList<>()
        ));

        User user = new User(
            "testerUser",
            "testerUUID",
            "test",
            "test@test.test",
            "password",
            new ArrayList<>()
        );

        for (int i = 1; i <= 100; i++) {
            boardRepository.save(new Board(
                "test" + i,
                community.getCommunityName(),
                "content" + i,
                category,
                user
            ));
        }

        //when
        ResultActions result = mockMvc.perform(get("/board/list")
            .queryParam("page", "1")
            .queryParam("size", "10")
            .queryParam("type", "t")
            .queryParam("keyword", "1")
            .queryParam("path", community.getCommunityName())
            .queryParam("category", "")).andDo(print());

        //then
        result.andExpect(status().isOk()).andExpectAll(
            jsonPath("$.dtoList[0].bno").value(100),
            jsonPath("$.page").value(1),
            jsonPath("$.size").value(10),
            jsonPath("$.total").value(20),
            jsonPath("$.start").value(1),
            jsonPath("$.end").value(2)
        );

        //docs
        result.andDo(write().document(requestHeaders(), responseBody(), requestBody(), curlRequest(), httpRequest(), httpResponse(),
            queryParameters(
            parameterWithName("page").description(
                "Page number to display in the full query results."),
            parameterWithName("size").description("Number of rows to display search results."),
            parameterWithName("type").description(
                "Search condition identifier. +" + "\n" + "Operation 'or' for search criteria. +"
                    + "\n" + "t: title +" + "\n" + "c: content +" + "\n" + "w: writer +" + "\n"
                    + "u: uuid +" + "\n" + "r: reply content"),
            parameterWithName("keyword").description("Search Keyword"),
            parameterWithName("path").description("The community to be searched for. +" + "\n"
                + "Operation 'and' for search criteria."),
            parameterWithName("category").description("Category of Search Destinations. +" + "\n"
                + "Operation 'and' for search criteria.")
        ), relaxedResponseFields(
            fieldWithPath("page").description("Page number to display in the full query results."),
            fieldWithPath("size").description("Number of rows to display search results."),
            fieldWithPath("total").description("The total number of rows of search results."),
            fieldWithPath("start").description("1 if search results exist, 0 otherwise."),
            fieldWithPath("end").description("Last page of total search results, total / size"),
            fieldWithPath("prev").description(
                "True if there are more than 2 pages currently being viewed."),
            fieldWithPath("next").description("True if page is less than end."),
            fieldWithPath("dtoList").description("Search results row data.")
        )));
    }

    @Test
    void boardModify() throws Exception {
        //given
        Community community = communityRepository.save(new Community(
            "test",
            "test"
        ));

        Category category = categoryRepository.save(new Category(
            community.getCommunityName(),
            "test",
            "test",
            community,
            new ArrayList<>()
        ));

        Category category2 = categoryRepository.save(new Category(
            community.getCommunityName(),
            "modify",
            "modify",
            community,
            new ArrayList<>()
        ));

        User user = new User(
            "testerUser",
            "testerUUID",
            "tester",
            "test@test.test",
            "password",
            new ArrayList<>()
        );

        Board board = boardRepository.save(new Board(
            "test",
            community.getCommunityName(),
            "content",
            category,
            user
        ));

        Modify modifyDTO = new Modify(
            board.getBno(),
            "modify",
            "modify",
            board.getUser().getId(),
            category2.getCategoryId(),
            new ArrayList<>()
        );

        //when
        ResultActions result = mockMvc.perform(patch("/board").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(modifyDTO))).andDo(print());

        //then
        result.andExpect(status().isOk());
        Board resultBoard = boardRepository.findById(board.getBno()).orElseThrow(PostNotFound::new);
        if (!(resultBoard.getBno().equals(board.getBno()) && resultBoard.getTitle().equals("modify")
            && resultBoard.getContent().equals("modify") && resultBoard.getCategory()
            .getCategoryId()
            .equals(category2.getCategoryId()))) {
            throw new Exception();
        }

        //docs
        result.andDo(write().document(requestHeaders(), responseBody(), requestBody(), curlRequest(), httpRequest(), httpResponse(),
            requestFields(
            fieldWithPath("bno").description(
                "Number of Board."),
            fieldWithPath("title").description("Title."),
            fieldWithPath("content").description("Content."),
            fieldWithPath("writer").description("Writer."),
            fieldWithPath("categoryId").description("Category Id."),
            fieldWithPath("fileNames").description("Name of attached files.")
        )));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void boardDelete() throws Exception {
        //given
        Community community = communityRepository.save(new Community(
            "test",
            "test"
        ));

        Category category = categoryRepository.save(new Category(
            community.getCommunityName(),
            "test",
            "test",
            community,
            new ArrayList<>()
        ));

        User user = new User(
            "testerUser",
            "testerUUID",
            "tester",
            "test@test.test",
            "password",
            new ArrayList<>()
        );

        Board board = boardRepository.save(new Board(
            "test",
            community.getCommunityName(),
            "content",
            category,
            user
        ));

        //when
        ResultActions result = mockMvc.perform(delete("/board/{bno}", board.getBno()))
            .andDo(print());

        //then
        result.andExpect(status().isOk());
        assertThrows(
            PostNotFound.class,
            () -> boardRepository.findById(board.getBno()).orElseThrow(PostNotFound::new)
        );

        //docs
        result.andDo(write().document(requestHeaders(), responseBody(), requestBody(), curlRequest(), httpRequest(), httpResponse(),
            pathParameters(parameterWithName("bno").description(
                "Number of Board."))
        ));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void boardAdminDelete() throws Exception {
        //given
        Community community = communityRepository.save(new Community(
            "test",
            "test"
        ));

        Category category = categoryRepository.save(new Category(
            community.getCommunityName(),
            "test",
            "test",
            community,
            new ArrayList<>()
        ));

        User user = new User(
            "tester",
            "tester",
            "tester",
            "test@test.test",
            "password",
            new ArrayList<>()
        );
        userRepository.save(user);

        Board board = boardRepository.save(new Board(
            "test",
            community.getCommunityName(),
            "content",
            category,
            user
        ));

        User userAdmin = new User(
            "testerUser",
            "testerUUID",
            "tester",
            "test@test.test",
            "password",
            new ArrayList<>()
        );

        UserRole userRole = userRoleRepository.save(new UserRole(
            "ADMIN_" + community.getCommunityName(),
            new ArrayList<>()
        ));
        userService.setRole(userAdmin.getId(), userRole.getRole());

        //when
        ResultActions result = mockMvc.perform(delete(
            "/board/{bno}/admin",
            board.getBno()
        ).queryParam("communityName", community.getCommunityName())).andDo(print());

        //then
        result.andExpect(status().isOk());
        assertThrows(
            PostNotFound.class,
            () -> boardRepository.findById(board.getBno()).orElseThrow(PostNotFound::new)
        );

        //docs
        result.andDo(write().document(requestHeaders(), responseBody(), requestBody(), curlRequest(), httpRequest(), httpResponse(),
            pathParameters(parameterWithName("bno").description(
                "Number of Board.")),
            queryParameters(
                parameterWithName("communityName").description("The community name of the post.")
            )
        ));
    }
}