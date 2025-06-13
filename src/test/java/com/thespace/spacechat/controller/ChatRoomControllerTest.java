package com.thespace.spacechat.controller;


import static com.thespace.config.RestDocsConfig.CsrfRestDocumentationRequestBuilders.post;
import static com.thespace.config.RestDocsConfig.CsrfRestDocumentationRequestBuilders.put;
import static com.thespace.config.RestDocsConfig.write;
import static org.springframework.restdocs.cli.CliDocumentation.curlRequest;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.http.HttpDocumentation.httpRequest;
import static org.springframework.restdocs.http.HttpDocumentation.httpResponse;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thespace.config.DataBaseCleaner;
import com.thespace.spacechat.dto.ChatRoomDTOs;
import com.thespace.spacechat.service.ChatRoomService;
import com.thespace.spaceweb.domain.User;
import com.thespace.spaceweb.domain.UserRole;
import com.thespace.spaceweb.repository.UserRepository;
import com.thespace.spaceweb.repository.UserRoleRepository;
import com.thespace.spaceweb.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("test")
@ExtendWith(RestDocumentationExtension.class)
class ChatRoomControllerTest {

    private static ChatRoomDTOs.Create createDTO;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private DataBaseCleaner dataBaseCleaner;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
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

        List<String> members = new ArrayList<>();
        members.add("testerUUID");

        createDTO = new ChatRoomDTOs.Create("Test Room", "Test", members);
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void create() throws Exception {
        //given createDTO

        //when
        ResultActions result = mockMvc.perform(post("/chat/room")
            .content(objectMapper.writeValueAsString(createDTO))
            .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk()).andDo(print());

        //docs
        result.andDo(write().document(
            requestHeaders(),
            responseBody(),
            requestBody(),
            curlRequest(),
            httpRequest(),
            httpResponse(),
            requestFields(
                fieldWithPath("name").description("ChatRoomName"),
                fieldWithPath("description").description("ChatRoomDescription"),
                fieldWithPath("members").description("Join members")
            )
        ));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void getRoom() throws Exception {
        //given
        Authentication user = SecurityContextHolder.getContext().getAuthentication();
        Long rid = chatRoomService.create(user ,createDTO).roomId();

        //when
        ResultActions result = mockMvc.perform(get("/chat/room/{rid}", rid)
            .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk()).andDo(print());

        //docs
        result.andDo(write().document(
            pathParameters(parameterWithName("rid").description("Room ID")),
            requestHeaders(),
            responseBody(),
            requestBody(),
            curlRequest(),
            httpRequest(),
            httpResponse()
        ));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void update() throws Exception {
        //given
        Authentication user = SecurityContextHolder.getContext().getAuthentication();
        Long rid = chatRoomService.create(user ,createDTO).roomId();
        ChatRoomDTOs.Update dto = new ChatRoomDTOs.Update("Update Change", "Update test");

        //when
        ResultActions result = mockMvc.perform(put("/chat/room/update/{rid}", rid)
            .content(objectMapper.writeValueAsString(dto))
            .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk()).andDo(print());

        //docs
        result.andDo(write().document(
            pathParameters(parameterWithName("rid").description("Room ID")),
            requestHeaders(),
            responseBody(),
            requestBody(),
            curlRequest(),
            httpRequest(),
            httpResponse(),
            requestFields(
                fieldWithPath("name").description("ChatRoomName"),
                fieldWithPath("description").description("ChatRoomDescription"))
        ));
    }
}