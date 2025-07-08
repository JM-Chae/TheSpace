package com.thespace.spaceweb.controller;

import static com.thespace.config.RestDocsConfig.CsrfRestDocumentationRequestBuilders.put;
import static com.thespace.config.RestDocsConfig.write;
import static org.springframework.restdocs.cli.CliDocumentation.curlRequest;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
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
import com.thespace.config.DataBaseCleaner;
import com.thespace.spaceweb.board.Board;
import com.thespace.spaceweb.board.BoardRepository;
import com.thespace.spaceweb.category.Category;
import com.thespace.spaceweb.category.CategoryRepository;
import com.thespace.spaceweb.community.Community;
import com.thespace.spaceweb.community.CommunityRepository;
import com.thespace.spaceweb.like.LikeDTO;
import com.thespace.spaceweb.user.User;
import com.thespace.spaceweb.user.UserRepository;
import com.thespace.spaceweb.user.UserRole;
import com.thespace.spaceweb.user.UserRoleRepository;
import com.thespace.spaceweb.user.UserRoleService;
import jakarta.servlet.http.Cookie;
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
    private UserRoleService userRoleService;

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
        User user = userRepository.save(new User("testerUser",
            "testerUUID",
            "tester",
            "test@test.test",
            "password",
            new ArrayList<>(),
            "nice to meet you",
            "😊"
        ));
        Long rid = userRoleRepository.save(new UserRole("ROLE_USER", new ArrayList<>())).getId();
        userRoleService.setRole(user.getId(), rid);
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void likeLike() throws Exception {
        //given
        Community community = communityRepository.save(new Community("test", "test"));

        Category category = categoryRepository.save(new Category("test",
            "test",
            community,
            new ArrayList<>()
        ));

        User boardUser = new User("testerUser",
            "testerUUID",
            "tester",
            "test@test.test",
            "password",
            new ArrayList<>(),
            "nice to meet you",
            "😊"
        );

        Board board = boardRepository.save(new Board("test",
            "content",
            community,
            category,
            boardUser
        ));

        LikeDTO likeDTO = new LikeDTO(board.getBno(), 0L);

        //when
        ResultActions result1 = mockMvc.perform(put("/like").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(likeDTO))
            .header("_csrf", "dummyCsrfToken")
            .cookie(new Cookie("JSESSIONID", "example-session-id")));
        ResultActions result2 = mockMvc.perform(put("/like").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(likeDTO))
            .header("_csrf", "dummyCsrfToken")
            .cookie(new Cookie("JSESSIONID", "example-session-id")));

        //then
        result1.andExpect(status().isOk()).andExpect(jsonPath("$").value(1));
        result2.andExpect(status().isOk()).andExpect(jsonPath("$").value(-1));

        //docs
        result1.andDo(write().document(requestHeaders(),
            responseBody(),
            requestBody(),
            curlRequest(),
            httpRequest(),
            httpResponse(),
            requestFields(
                fieldWithPath("bno").description("If perform target is Post, its number."),
                fieldWithPath("rno").description("If perform target is Reply, its number.")
            ),
            requestCookies(cookieWithName("JSESSIONID").description(
                "Authenticated user session ID cookie")),
            requestHeaders(headerWithName("_csrf").description("CSRF Token"))
        ));
    }
}