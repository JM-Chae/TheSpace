package com.thespace.spacechat.controller;


import static com.thespace.config.RestDocsConfig.CsrfRestDocumentationRequestBuilders.delete;
import static com.thespace.config.RestDocsConfig.CsrfRestDocumentationRequestBuilders.post;
import static com.thespace.config.RestDocsConfig.CsrfRestDocumentationRequestBuilders.put;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thespace.config.DataBaseCleaner;
import com.thespace.spacechat.room.ChatRoomDTOs;
import com.thespace.spacechat.room.ChatRoomDTOs.Info;
import com.thespace.spacechat.room.ChatRoomService;
import com.thespace.spaceweb.user.User;
import com.thespace.spaceweb.user.UserRepository;
import com.thespace.spaceweb.user.UserRole;
import com.thespace.spaceweb.user.UserRoleRepository;
import com.thespace.spaceweb.user.UserService;
import jakarta.servlet.http.Cookie;
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

        createDTO = new ChatRoomDTOs.Create("Test Room", "Test", members);
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void create() throws Exception {
        //given createDTO

        //when
        ResultActions result = mockMvc.perform(post("/chat/room")
            .header("_csrf", "dummyCsrfToken")
            .cookie(new Cookie("JSESSIONID", "example-session-id"))
            .content(objectMapper.writeValueAsString(createDTO))
            .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk()).andDo(print());

        //docs
        result.andDo(write().document(
            requestHeaders(headerWithName("_csrf").description("CSRF Token")),
            relaxedResponseFields(
                fieldWithPath("roomId").description("Room ID"),
                fieldWithPath("name").description("Room name"),
                fieldWithPath("manager").description("Room manager"),
                fieldWithPath("description").description("Room description"),
                fieldWithPath("members").description("Joined members"),
                fieldWithPath("createdAt").description("Created time"),
                fieldWithPath("modifiedAt").description("Last modified time")),
            responseBody(),
            requestBody(),
            curlRequest(),
            httpRequest(),
            httpResponse(),
            requestFields(
                fieldWithPath("name").description("ChatRoomName"),
                fieldWithPath("description").description("ChatRoomDescription"),
                fieldWithPath("members").description("Join members")),
            requestCookies(
                cookieWithName("JSESSIONID").description("Authenticated user session ID cookie"))
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
            relaxedResponseFields(
                fieldWithPath("roomId").description("Room ID"),
                fieldWithPath("name").description("Room name"),
                fieldWithPath("manager").description("Room manager"),
                fieldWithPath("description").description("Room description"),
                fieldWithPath("members").description("Joined members"),
                fieldWithPath("createdAt").description("Created time"),
                fieldWithPath("modifiedAt").description("Last modified time")),
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
            .header("_csrf", "dummyCsrfToken")
            .cookie(new Cookie("JSESSIONID", "example-session-id"))
            .content(objectMapper.writeValueAsString(dto))
            .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk()).andDo(print());

        //docs
        result.andDo(write().document(
            pathParameters(parameterWithName("rid").description("Room ID")),
            requestHeaders(headerWithName("_csrf").description("CSRF Token")),
            relaxedResponseFields(
                fieldWithPath("roomId").description("Room ID"),
                fieldWithPath("name").description("Room name"),
                fieldWithPath("manager").description("Room manager"),
                fieldWithPath("description").description("Room description"),
                fieldWithPath("members").description("Joined members"),
                fieldWithPath("createdAt").description("Created time"),
                fieldWithPath("modifiedAt").description("Last modified time")),
            responseBody(),
            requestBody(),
            curlRequest(),
            httpRequest(),
            httpResponse(),
            requestFields(
                fieldWithPath("name").description("ChatRoomName"),
                fieldWithPath("description").description("ChatRoomDescription")),
            requestCookies(
                cookieWithName("JSESSIONID").description("Authenticated user session ID cookie"))
            ));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void invite() throws Exception {
        //given
        Authentication user = SecurityContextHolder.getContext().getAuthentication();
        Long rid = chatRoomService.create(user ,createDTO).roomId();
        List<String> members = new ArrayList<>();

        for(int i = 1; i <= 5; i++) {
            User member = userRepository.save(new User(
                "testerUser" + i,
                "testerUUID" + i,
                "tester",
                "test@test.test" + i,
                "password",
                new ArrayList<>()
            ));
            userService.setRole(member.getId(), "ROLE_USER");

            members.add("testerUUID" + i);
        }

        ChatRoomDTOs.Invite dto = new ChatRoomDTOs.Invite(members);

        //when
        ResultActions result = mockMvc.perform(post("/chat/room/{rid}/members", rid)
            .header("_csrf", "dummyCsrfToken")
            .cookie(new Cookie("JSESSIONID", "example-session-id"))
            .content(objectMapper.writeValueAsString(dto))
            .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk()).andDo(print());

        //docs
        result.andDo(write().document(
            pathParameters(parameterWithName("rid").description("Room ID")),
            requestHeaders(headerWithName("_csrf").description("CSRF Token")),
            relaxedResponseFields(
                fieldWithPath("roomId").description("Room ID"),
                fieldWithPath("name").description("Room name"),
                fieldWithPath("manager").description("Room manager"),
                fieldWithPath("description").description("Room description"),
                fieldWithPath("members").description("Joined members"),
                fieldWithPath("createdAt").description("Created time"),
                fieldWithPath("modifiedAt").description("Last modified time")),
            responseBody(),
            requestBody(),
            curlRequest(),
            httpRequest(),
            httpResponse(),
            requestFields(
                fieldWithPath("members").description("A target members to invite")),
            requestCookies(
                cookieWithName("JSESSIONID").description("Authenticated user session ID cookie"))
        ));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void kick() throws Exception {
        //given
        Authentication user = SecurityContextHolder.getContext().getAuthentication();
        List<String> members = new ArrayList<>();

        for(int i = 1; i <= 5; i++) {
            User member = userRepository.save(new User(
                "testerUser" + i,
                "testerUUID" + i,
                "tester",
                "test@test.test" + i,
                "password",
                new ArrayList<>()
            ));
            userService.setRole(member.getId(), "ROLE_USER");
            members.add("testerUUID" + i);
        }

        createDTO.members().addAll(members);
        Info room = chatRoomService.create(user ,createDTO);

        //when
        ResultActions result = mockMvc.perform(delete("/chat/room/{rid}/members", room.roomId())
            .header("_csrf", "dummyCsrfToken")
            .cookie(new Cookie("JSESSIONID", "example-session-id"))
            .queryParam("targetUuid", "testerUUID3")
            .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk()).andDo(print());

        //docs
        result.andDo(write().document(
            pathParameters(parameterWithName("rid").description("Room ID")),
            requestHeaders(headerWithName("_csrf").description("CSRF Token")),
            relaxedResponseFields(
                fieldWithPath("roomId").description("Room ID"),
                fieldWithPath("name").description("Room name"),
                fieldWithPath("manager").description("Room manager"),
                fieldWithPath("description").description("Room description"),
                fieldWithPath("members").description("Joined members"),
                fieldWithPath("createdAt").description("Created time"),
                fieldWithPath("modifiedAt").description("Last modified time")),
            responseBody(),
            requestBody(),
            curlRequest(),
            httpRequest(),
            httpResponse(),
            queryParameters(
                parameterWithName("targetUuid").description("Target to kick.")),
            requestCookies(
                cookieWithName("JSESSIONID").description("Authenticated user session ID cookie"))
        ));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void quit() throws Exception {
        //given
        Authentication user = SecurityContextHolder.getContext().getAuthentication();
        Info room = chatRoomService.create(user ,createDTO);

        //when
        ResultActions result = mockMvc.perform(delete("/chat/room/{rid}/members/me", room.roomId())
            .header("_csrf", "dummyCsrfToken")
            .cookie(new Cookie("JSESSIONID", "example-session-id"))
            .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk()).andDo(print());

        //docs
        result.andDo(write().document(
            pathParameters(parameterWithName("rid").description("Room ID")),
            requestHeaders(headerWithName("_csrf").description("CSRF Token")),
            responseBody(),
            requestBody(),
            curlRequest(),
            httpRequest(),
            httpResponse(),
            requestCookies(
                cookieWithName("JSESSIONID").description("Authenticated user session ID cookie"))
        ));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void getMyRooms() throws Exception {
        //given
        Authentication user = SecurityContextHolder.getContext().getAuthentication();
        chatRoomService.create(user ,createDTO);

        //when
        ResultActions result = mockMvc.perform(get("/chat/room/my")
            .header("_csrf", "dummyCsrfToken")
            .cookie(new Cookie("JSESSIONID", "example-session-id"))
            .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk()).andDo(print());

        //docs
        result.andDo(write().document(
            requestHeaders(headerWithName("_csrf").description("CSRF Token")),
            responseFields(
                fieldWithPath("[].rid").description("Room ID"),
                fieldWithPath("[].name").description("Room name"),
                fieldWithPath("[].lastSentMessage").description("Last sent message"),
                fieldWithPath("[].lastSentAt").description("Time of last sent message")),
            responseBody(),
            requestBody(),
            curlRequest(),
            httpRequest(),
            httpResponse(),
            requestCookies(
                cookieWithName("JSESSIONID").description("Authenticated user session ID cookie"))
        ));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteRoom() throws Exception {
        //given
        Authentication user = SecurityContextHolder.getContext().getAuthentication();
        Info room = chatRoomService.create(user ,createDTO);

        //when
        ResultActions result = mockMvc.perform(delete("/chat/room/{rid}", room.roomId())
            .header("_csrf", "dummyCsrfToken")
            .cookie(new Cookie("JSESSIONID", "example-session-id"))
            .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk()).andDo(print());

        //docs
        result.andDo(write().document(
            pathParameters(parameterWithName("rid").description("Room ID")),
            requestHeaders(headerWithName("_csrf").description("CSRF Token")),
            responseBody(),
            requestBody(),
            curlRequest(),
            httpRequest(),
            httpResponse(),
            requestCookies(
                cookieWithName("JSESSIONID").description("Authenticated user session ID cookie"))
        ));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void delegate() throws Exception {
        //given
        Authentication user = SecurityContextHolder.getContext().getAuthentication();
        List<String> members = new ArrayList<>();

        for(int i = 1; i <= 5; i++) {
            User member = userRepository.save(new User(
                "testerUser" + i,
                "testerUUID" + i,
                "tester",
                "test@test.test" + i,
                "password",
                new ArrayList<>()
            ));
            userService.setRole(member.getId(), "ROLE_USER");
            members.add("testerUUID" + i);
        }

        createDTO.members().addAll(members);
        Info room = chatRoomService.create(user ,createDTO);

        //when
        ResultActions result = mockMvc.perform(put("/chat/room/{rid}/manager", room.roomId())
            .header("_csrf", "dummyCsrfToken")
            .cookie(new Cookie("JSESSIONID", "example-session-id"))
            .queryParam("targetUuid", "testerUUID3")
            .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk()).andDo(print());

        //docs
        result.andDo(write().document(
            pathParameters(parameterWithName("rid").description("Room ID")),
            requestHeaders(headerWithName("_csrf").description("CSRF Token")),
            responseBody(),
            relaxedResponseFields(
                fieldWithPath("roomId").description("Room ID"),
                fieldWithPath("name").description("Room name"),
                fieldWithPath("manager").description("Room manager"),
                fieldWithPath("description").description("Room description"),
                fieldWithPath("members").description("Joined members"),
                fieldWithPath("createdAt").description("Created time"),
                fieldWithPath("modifiedAt").description("Last modified time")),
            requestBody(),
            curlRequest(),
            httpRequest(),
            httpResponse(),
            queryParameters(
                parameterWithName("targetUuid").description("Target to delegate manager.")),
            requestCookies(
                cookieWithName("JSESSIONID").description("Authenticated user session ID cookie"))
        ));
    }
}