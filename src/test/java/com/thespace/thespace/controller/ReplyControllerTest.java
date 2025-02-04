package com.thespace.thespace.controller;

import static com.thespace.thespace.config.RestDocsConfig.restDocsConfig;
import static com.thespace.thespace.config.RestDocsConfig.write;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thespace.thespace.config.DataBaseCleaner;
import com.thespace.thespace.domain.Board;
import com.thespace.thespace.domain.Category;
import com.thespace.thespace.domain.Community;
import com.thespace.thespace.domain.Reply;
import com.thespace.thespace.dto.reply.ReplyRegisterDTO;
import com.thespace.thespace.repository.BoardRepository;
import com.thespace.thespace.repository.CategoryRepository;
import com.thespace.thespace.repository.CommunityRepository;
import com.thespace.thespace.repository.ReplyRepository;
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
class ReplyControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private DataBaseCleaner dataBaseCleaner;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;


    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentation) {
        mockMvc = restDocsConfig(context, restDocumentation);
        dataBaseCleaner.clear();
    }

    @Test
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

        Board board = boardRepository.save(new Board(
            "test",
            community.getCommunityName(),
            "content",
            "123456",
            "A24WS122A",
            category
        ));

        ReplyRegisterDTO rDto = new ReplyRegisterDTO(
            "test",
            "tester",
            "tester",
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
        result.andExpect(status().isOk());

        //docs
        result.andDo(write().document(requestFields(
            fieldWithPath("replyContent").description(
                "Content of reply"),
            fieldWithPath("replyWriter").description("Writer name"),
            fieldWithPath("replyWriterUuid").description("Writer UUID"),
            fieldWithPath("tag").description("The writer name of the tagged reply."),
            fieldWithPath("path").description("{BNO}/{RNO}(optional)/ +" + "\n"
                + "Board Number is placed as the top path, and if it is nested reply, Reply Number is inserted as the subsequent path for it.")
        ), pathParameters(parameterWithName("bno").description("Board Number"))));
    }

    @Test
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

        Board board = boardRepository.save(new Board(
            "test",
            community.getCommunityName(),
            "content",
            "123456",
            "A24WS122A",
            category
        ));

        Reply reply = replyRepository.save(new Reply(
            board.getPath() + "/",
            "test",
            "tester",
            "tester",
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
        result.andExpect(status().isOk());

        //docs
        result.andDo(write().document(pathParameters(
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

        Board board = boardRepository.save(new Board(
            "test",
            community.getCommunityName(),
            "content",
            "123456",
            "A24WS122A",
            category
        ));

        for (int i = 1; i <= 2; i++) {
            Reply reply = replyRepository.save(new Reply(
                board.getBno() + "/",
                "test" + i,
                "tester" + i,
                "tester" + i,
                "",
                board
            ));

            replyRepository.save(new Reply(
                board.getBno() + "/" + reply.getRno(),
                "test" + i,
                "tester" + i,
                "tester" + i,
                reply.getReplyWriter(),
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
        );

        //docs
        result.andDo(write().document(pathParameters(
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

        Board board = boardRepository.save(new Board(
            "test",
            community.getCommunityName(),
            "content",
            "123456",
            "A24WS122A",
            category
        ));

        for (int i = 1; i <= 2; i++) {
            Reply reply = replyRepository.save(new Reply(
                board.getBno() + "/",
                "test" + i,
                "tester" + i,
                "tester" + i,
                "",
                board
            ));

            if (i > 1) {
                for (int j = 3; j <= 4; j++) {
                    replyRepository.save(new Reply(
                        board.getBno() + "/" + reply.getRno(),
                        "test" + (i + j),
                        "tester" + (i + j),
                        "tester" + (i + j),
                        reply.getReplyWriter(),
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
        );

        //docs
        result.andDo(write().document(pathParameters(
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