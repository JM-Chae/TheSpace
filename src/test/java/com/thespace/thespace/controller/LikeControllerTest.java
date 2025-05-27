package com.thespace.thespace.controller;

import static com.thespace.thespace.config.RestDocsConfig.CsrfRestDocumentationRequestBuilders.put;
import static com.thespace.thespace.config.RestDocsConfig.write;
import static org.springframework.restdocs.cli.CliDocumentation.curlRequest;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.http.HttpDocumentation.httpRequest;
import static org.springframework.restdocs.http.HttpDocumentation.httpResponse;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thespace.thespace.config.DataBaseCleaner;
import com.thespace.thespace.domain.Board;
import com.thespace.thespace.domain.Category;
import com.thespace.thespace.domain.Community;
import com.thespace.thespace.domain.User;
import com.thespace.thespace.domain.UserRole;
import com.thespace.thespace.dto.LikeDTO;
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
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataBaseCleaner dataBaseCleaner;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CategoryRepository categoryRepository;

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
    void likeLike() throws Exception {
        //given
        Community community = communityRepository.save(new Community("test", "test"));

        Category category = categoryRepository.save(new Category(
            community.getCommunityName(),
            "test",
            "test",
            community,
            new ArrayList<>()
        ));

        User boardUser = new User("testerUser", "testerUUID", "tester", "test@test.test", "password", new ArrayList<>());

        Board board = boardRepository.save(new Board(
            "test",
            community.getCommunityName(),
            "content",
            category,
            boardUser
        ));

        LikeDTO likeDTO = new LikeDTO(board.getBno(), 0L);

        //when
        ResultActions result1 = mockMvc.perform(put("/like").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(likeDTO)));
        ResultActions result2 = mockMvc.perform(put("/like").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(likeDTO)));

        //then
        result1.andExpect(status().isOk()).andExpect(jsonPath("$").value(1));
        result2.andExpect(status().isOk()).andExpect(jsonPath("$").value(-1));

        //docs
        result1.andDo(write().document(requestHeaders(), responseBody(), requestBody(), curlRequest(), httpRequest(), httpResponse(),
            requestFields(
            fieldWithPath("bno").description("If perform target is Post, its number."),
            fieldWithPath("rno").description("If perform target is Reply, its number.")
        )));
    }
}