package com.thespace.spaceweb.controller;

import static com.thespace.config.RestDocsConfig.CsrfRestDocumentationRequestBuilders.delete;
import static com.thespace.config.RestDocsConfig.CsrfRestDocumentationRequestBuilders.post;
import static com.thespace.config.RestDocsConfig.write;
import static org.springframework.restdocs.cli.CliDocumentation.curlRequest;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
import com.thespace.spaceweb.reply.Reply;
import com.thespace.spaceweb.reply.ReplyDTOs.Register;
import com.thespace.spaceweb.reply.ReplyRepository;
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
class ReplyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataBaseCleaner dataBaseCleaner;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        dataBaseCleaner.clear();
        User user = userRepository.save(new User(
            "testerUser",
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
    void replyRegister() throws Exception {
        //given
        Community community = communityRepository.save(new Community("test", "test"));

        Category category = categoryRepository.save(new Category(
            "test",
            "test",
            community,
            new ArrayList<>()
        ));

        User user = new User("testerUser",
            "testerUUID",
            "test",
            "test@test.test",
            "password",
            new ArrayList<>(),
            "nice to meet you",
            "😊"
        );

        Board board = boardRepository.save(new Board(
            "test",
            "content",
            community,
            category,
            user
        ));

        Register rDto = new Register(
            "test",
            "",
            0L,
            0L
        );

        //when
        ResultActions result = mockMvc.perform(post(
            "/board/{bno}/reply",
            board.getBno()
        ).contentType(MediaType.APPLICATION_JSON).header("_csrf", "dummyCsrfToken")
            .cookie(new Cookie("JSESSIONID", "example-session-id"))
            .content(objectMapper.writeValueAsString(rDto)));

        //then
        result.andExpect(status().isOk()).andDo(print());

        //docs
        result.andDo(write().document(requestHeaders(),
            responseBody(),
            requestBody(),
            curlRequest(),
            httpRequest(),
            httpResponse(),
            requestFields(
                fieldWithPath("replyContent").description(
                    "Content of reply"),
                fieldWithPath("tag").description("The writer name of the tagged reply."),
                fieldWithPath("parentRno").description("Parent comment number for this comment."),
                fieldWithPath("tagRno").description(
                    "The number of comments that this comment is tagging.")
            ),
            pathParameters(parameterWithName("bno").description("Board Number")),
            requestCookies(cookieWithName("JSESSIONID").description(
                "Authenticated user session ID cookie")),
            requestHeaders(headerWithName("_csrf").description("CSRF Token"))
        ));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void replyDelete() throws Exception {
        //given
        Community community = communityRepository.save(new Community("test", "test"));

        Category category = categoryRepository.save(new Category(
            "test",
            "test",
            community,
            new ArrayList<>()
        ));

        User user = new User("testerUser",
            "testerUUID",
            "tester",
            "test@test.test",
            "password",
            new ArrayList<>(),
            "nice to meet you",
            "😊"
        );

        Board board = boardRepository.save(new Board(
            "test",
            "content",
            community,
            category,
            user
        ));

        Reply reply = replyRepository.save(new Reply(
            0L,
            0L,
            "test",
            user,
            "",
            board
        ));

        //when
        ResultActions result = mockMvc.perform(delete(
            "/board/{bno}/reply/{rno}",
            board.getBno(),
            reply.getRno()
        ).header("_csrf", "dummyCsrfToken")
            .cookie(new Cookie("JSESSIONID", "example-session-id")));

        //then
        result.andExpect(status().isOk()).andDo(print());

        //docs
        result.andDo(write().document(requestHeaders(),
            responseBody(),
            requestBody(),
            curlRequest(),
            httpRequest(),
            httpResponse(),
            pathParameters(
                parameterWithName("bno").description("Board Number"),
                parameterWithName("rno").description("Reply Number to delete")
            ),
            requestCookies(cookieWithName("JSESSIONID").description(
                "Authenticated user session ID cookie")),
            requestHeaders(headerWithName("_csrf").description("CSRF Token"))
        ));
    }

    @Test
    void replyList() throws Exception {
        //given
        Community community = communityRepository.save(new Community("test", "test"));

        Category category = categoryRepository.save(new Category(
            "test",
            "test",
            community,
            new ArrayList<>()
        ));

        User user = new User("testerUser",
            "testerUUID",
            "test",
            "test@test.test",
            "password",
            new ArrayList<>(),
            "nice to meet you",
            "😊"
        );

        Board board = boardRepository.save(new Board(
            "test",
            "content",
            community,
            category,
            user
        ));

        for (int i = 1; i <= 2; i++) {
            Reply reply = replyRepository.save(new Reply(
                0L,
                0L,
                "test" + i,
                user,
                "",
                board
            ));

            replyRepository.save(new Reply(
                reply.getRno(),
                0L,
                "test" + (i + 1),
                user,
                reply.getUser().getId(),
                board
            ));
        }

        //when
        ResultActions result = mockMvc.perform(get("/board/{bno}/reply", board.getBno()).header(
                "_csrf",
                "dummyCsrfToken"
            )
            .cookie(new Cookie("JSESSIONID", "example-session-id")));

        //then
        result.andExpect(status().isOk()).andExpectAll(
            jsonPath("$.total").value("4"),
            jsonPath("$.dtoList[0].replyContent").value("test1"),
            jsonPath("$.dtoList[1].replyContent").value("test2")
        ).andDo(print());

        //docs
        result.andDo(write().document(requestHeaders(),
            responseBody(),
            requestBody(),
            curlRequest(),
            httpRequest(),
            httpResponse(),
            pathParameters(
                parameterWithName("bno").description("Board Number")
            ),
            relaxedResponseFields(
                fieldWithPath("total").description("Total number of reply on this board"),
                fieldWithPath("dtoList[].rno").description("Reply Number"),
                fieldWithPath("dtoList[].replyContent").description("Content of reply"),
                fieldWithPath("dtoList[].replyWriter").description("Writer name of reply"),
                fieldWithPath("dtoList[].replyWriterUuid").description("Writer UUID of reply"),
                fieldWithPath("dtoList[].tag").description("The writer name of the tagged reply."),
                fieldWithPath("dtoList[].replyDate").description("Date of reply registered"),
                fieldWithPath("dtoList[].childCount").description("Count of nested reply"),
                fieldWithPath("dtoList[].parentRno").description(
                    "Parent comment number for this comment."),
                fieldWithPath("dtoList[].taggedCount").description("Count of tagged."),
                fieldWithPath("dtoList[].vote").description("Count of like")
            ),
            requestCookies(cookieWithName("JSESSIONID").description(
                "Authenticated user session ID cookie")),
            requestHeaders(headerWithName("_csrf").description("CSRF Token"))
        ));
    }
}