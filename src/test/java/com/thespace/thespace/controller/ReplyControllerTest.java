package com.thespace.thespace.controller;

import static com.thespace.thespace.config.RestDocsConfig.write;
import static org.springframework.restdocs.cli.CliDocumentation.curlRequest;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.http.HttpDocumentation.httpRequest;
import static org.springframework.restdocs.http.HttpDocumentation.httpResponse;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
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
import com.thespace.thespace.config.DataBaseCleaner;
import com.thespace.thespace.domain.Board;
import com.thespace.thespace.domain.Category;
import com.thespace.thespace.domain.Community;
import com.thespace.thespace.domain.Reply;
import com.thespace.thespace.domain.User;
import com.thespace.thespace.domain.UserRole;
import com.thespace.thespace.dto.ReplyDTOs.Register;
import com.thespace.thespace.repository.BoardRepository;
import com.thespace.thespace.repository.CategoryRepository;
import com.thespace.thespace.repository.CommunityRepository;
import com.thespace.thespace.repository.ReplyRepository;
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
    private UserService userService;

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
            new ArrayList<>()
        ));
        userRoleRepository.save(new UserRole("ROLE_USER", new ArrayList<>()));
        userService.setRole(user.getId(), "ROLE_USER");
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void replyRegister() throws Exception {
        //given
        Community community = communityRepository.save(new Community("test", "test"));

        Category category = categoryRepository.save(new Category(
            community.getCommunityName(),
            "test",
            "test",
            community,
            new ArrayList<>()
        ));

        User user = new User("testerUser", "testerUUID", "test", "test@test.test", "password", new ArrayList<>());

        Board board = boardRepository.save(new Board(
            "test",
            community.getCommunityName(),
            "content",
            category,
            user
        ));

        Register rDto = new Register(
            "test",
            "",
            board.getBno() + "/"
        );

        //when
        ResultActions result = mockMvc.perform(post(
            "/board/{bno}/reply",
            board.getBno()
        ).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(rDto)));

        //then
        result.andExpect(status().isOk()).andDo(print());

        //docs
        result.andDo(write().document(requestHeaders(), responseBody(), requestBody(), curlRequest(), httpRequest(), httpResponse(),
            requestFields(
            fieldWithPath("replyContent").description(
                "Content of reply"),
            fieldWithPath("tag").description("The writer name of the tagged reply."),
            fieldWithPath("path").description("{BNO}/{RNO}(optional)/ +" + "\n"
                + "Board Number is placed as the top path, and if it is nested reply, Reply Number is inserted as the subsequent path for it.")
        ), pathParameters(parameterWithName("bno").description("Board Number"))));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void replyDelete() throws Exception {
        //given
        Community community = communityRepository.save(new Community("test", "test"));

        Category category = categoryRepository.save(new Category(
            community.getCommunityName(),
            "test",
            "test",
            community,
            new ArrayList<>()
        ));

        User user = new User("testerUser", "testerUUID", "tester", "test@test.test", "password", new ArrayList<>());

        Board board = boardRepository.save(new Board(
            "test",
            community.getCommunityName(),
            "content",
            category,
            user
        ));

        Reply reply = replyRepository.save(new Reply(
            board.getPath() + "/",
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
        ));

        //then
        result.andExpect(status().isOk()).andDo(print());

        //docs
        result.andDo(write().document(requestHeaders(), responseBody(), requestBody(), curlRequest(), httpRequest(), httpResponse(),
            pathParameters(
            parameterWithName("bno").description("Board Number"),
            parameterWithName("rno").description("Reply Number to delete")
        )));
    }

    @Test
    void replyList() throws Exception {
        //given
        Community community = communityRepository.save(new Community("test", "test"));

        Category category = categoryRepository.save(new Category(
            community.getCommunityName(),
            "test",
            "test",
            community,
            new ArrayList<>()
        ));

        User user = new User("testerUser", "testerUUID", "test", "test@test.test", "password", new ArrayList<>());

        Board board = boardRepository.save(new Board(
            "test",
            community.getCommunityName(),
            "content",
            category,
            user
        ));

        for (int i = 1; i <= 2; i++) {
            Reply reply = replyRepository.save(new Reply(
                board.getBno() + "/",
                "test" + i,
                user,
                "",
                board
            ));

            replyRepository.save(new Reply(
                board.getBno() + "/" + reply.getRno(),
                "test" + i,
                user,
                reply.getUser().getId(),
                board
            ));
        }

        //when
        ResultActions result = mockMvc.perform(get("/board/{bno}/reply", board.getBno()));

        //then
        result.andExpect(status().isOk()).andExpectAll(
            jsonPath("$.total").value("2"),
            jsonPath("$.dtoList[0].replyContent").value("test1"),
            jsonPath("$.dtoList[1].replyContent").value("test2")
        ).andDo(print());

        //docs
        result.andDo(write().document(requestHeaders(), responseBody(), requestBody(), curlRequest(), httpRequest(), httpResponse(),
            pathParameters(
            parameterWithName("bno").description("Board Number")
        ), relaxedResponseFields(
            fieldWithPath("total").description("Total number of reply on this board"),
            fieldWithPath("dtoList[].rno").description("Reply Number"),
            fieldWithPath("dtoList[].replyContent").description("Content of reply"),
            fieldWithPath("dtoList[].replyWriter").description("Writer name of reply"),
            fieldWithPath("dtoList[].replyWriterUuid").description("Writer UUID of reply"),
            fieldWithPath("dtoList[].tag").description("The writer name of the tagged reply."),
            fieldWithPath("dtoList[].replyDate").description("Date of reply registered"),
            fieldWithPath("dtoList[].isNested").description("Count of nested reply"),
            fieldWithPath("dtoList[].path").description("{BNO}/{RNO}(optional)/ +" + "\n"
                + "Board Number is placed as the top path, and if it is nested reply, Reply Number is inserted as the subsequent path for it."),
            fieldWithPath("dtoList[].vote").description("Count of like")
        )));
    }

    @Test
    void replyNestedListGet() throws Exception {
        //given
        Community community = communityRepository.save(new Community("test", "test"));

        Category category = categoryRepository.save(new Category(
            community.getCommunityName(),
            "test",
            "test",
            community,
            new ArrayList<>()
        ));

        User user = new User("testerUser", "testerUUID", "test", "test@test.test", "password", new ArrayList<>());

        Board board = boardRepository.save(new Board(
            "test",
            community.getCommunityName(),
            "content",
            category,
            user
        ));

        for (int i = 1; i <= 2; i++) {
            Reply reply = replyRepository.save(new Reply(
                board.getBno() + "/",
                "test" + i,
                user,
                "",
                board
            ));

            if (i > 1) {
                for (int j = 3; j <= 4; j++) {
                    replyRepository.save(new Reply(
                        board.getBno() + "/" + reply.getRno(),
                        "test" + (i + j),
                        user,
                        reply.getUser().getId(),
                        board
                    ));
                }
            }
        }

        //when
        ResultActions result = mockMvc.perform(get("/board/{bno}/reply/{rno}", board.getBno(), 2));

        //then
        result.andExpect(status().isOk()).andExpectAll(
            jsonPath("$.total").value("2"),
            jsonPath("$.dtoList[0].replyContent").value("test5")
        ).andDo(print());

        //docs
        result.andDo(write().document(requestHeaders(), responseBody(), requestBody(), curlRequest(), httpRequest(), httpResponse(),
            pathParameters(
            parameterWithName("bno").description("Board Number"),
            parameterWithName("rno").description("Reply Number")
        ), relaxedResponseFields(
            fieldWithPath("total").description("Total number of reply on this board"),
            fieldWithPath("dtoList[].rno").description("Reply Number"),
            fieldWithPath("dtoList[].replyContent").description("Content of reply"),
            fieldWithPath("dtoList[].replyWriter").description("Writer name of reply"),
            fieldWithPath("dtoList[].replyWriterUuid").description("Writer UUID of reply"),
            fieldWithPath("dtoList[].tag").description("The writer name of the tagged reply."),
            fieldWithPath("dtoList[].replyDate").description("Date of reply registered"),
            fieldWithPath("dtoList[].isNested").description("Count of nested reply"),
            fieldWithPath("dtoList[].path").description("{BNO}/{RNO}(optional)/ +" + "\n"
                + "Board Number is placed as the top path, and if it is nested reply, Reply Number is inserted as the subsequent path for it."),
            fieldWithPath("dtoList[].vote").description("Count of like")
        )));
    }
}