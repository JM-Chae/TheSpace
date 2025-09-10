package com.thespace.spaceweb.controller;

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
import static org.springframework.restdocs.payload.PayloadDocumentation.requestBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.thespace.common.page.PageReqDTO;
import com.thespace.config.DataBaseCleaner;
import com.thespace.spaceweb.friendship.Friendship;
import com.thespace.spaceweb.friendship.FriendshipRepository;
import com.thespace.spaceweb.friendship.Status;
import com.thespace.spaceweb.user.User;
import com.thespace.spaceweb.user.UserRepository;
import com.thespace.spaceweb.user.UserRole;
import com.thespace.spaceweb.user.UserRoleRepository;
import com.thespace.spaceweb.user.UserRoleService;
import jakarta.servlet.http.Cookie;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
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
class FriendshipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataBaseCleaner dataBaseCleaner;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setup() {
        dataBaseCleaner.clear();
        user1 = userRepository.save(new User(
            "testerUser",
            "testerUUID",
            "tester",
            "test@test.test",
            "password",
            new ArrayList<>(),
            "nice to meet you",
            "😊"
        ));
        user2 = userRepository.save(new User(
            "friendUser",
            "friendUUID",
            "friend",
            "friend@test.test",
            "password",
            new ArrayList<>(),
            "nice to meet you",
            "😊"
        ));
        Long rid = userRoleRepository.save(new UserRole("ROLE_USER", new ArrayList<>())).getId();
        userRoleService.setRole(user1.getId(), rid);
        userRoleService.setRole(user2.getId(), rid);
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void request() throws Exception {

        //given
        String toUserUuid = "friendUUID";

        //when
        ResultActions result = mockMvc.perform(post("/friend")
            .queryParam("toUserUuid", toUserUuid)
            .header("_csrf", "dummyCsrfToken")
            .cookie(new Cookie("JSESSIONID", "example-session-id"))
            .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk()).andDo(print());

        //docs
        result.andDo(write().document(
            pathParameters(),
            queryParameters(parameterWithName("toUserUuid").description("UUID of the target user.")),
            requestHeaders(headerWithName("_csrf").description("CSRF Token")),
            responseBody(),
            requestBody(),
            curlRequest(),
            httpRequest(),
            httpResponse(),
            requestCookies(
                cookieWithName("JSESSIONID").description("Authenticated user session ID cookie(Requesting user)"))
        ));
    }

    @Test
    @WithUserDetails(value = "friendUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void requestToUser() throws Exception {

        //given
        String toUserUuid = "testerUUID";
        friendshipRepository.save(new Friendship(user1, user2, Status.REQUEST));

        //when
        ResultActions result = mockMvc.perform(post("/friend")
            .queryParam("toUserUuid", toUserUuid)
            .header("_csrf", "dummyCsrfToken")
            .cookie(new Cookie("JSESSIONID", "example-session-id"))
            .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk()).andDo(print());
        Assertions.assertEquals(Status.ACCEPTED, friendshipRepository.findByFromAndTo(user1, user2).get().getStatus());
        Assertions.assertEquals(Status.ACCEPTED, friendshipRepository.findByFromAndTo(user2, user1).get().getStatus());

        //docs
        result.andDo(write().document(
            pathParameters(),
            queryParameters(parameterWithName("toUserUuid").description("UUID of the target user.")),
            requestHeaders(headerWithName("_csrf").description("CSRF Token")),
            responseBody(),
            requestBody(),
            curlRequest(),
            httpRequest(),
            httpResponse(),
            requestCookies(
                cookieWithName("JSESSIONID").description("Authenticated user session ID cookie(Requesting user)"))
        ));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void accept() throws Exception {
        //given
        Long fid = friendshipRepository.save(new Friendship(user2, user1, Status.REQUEST)).getFid();

        //when
        ResultActions result = mockMvc.perform(post("/friend/{fid}", fid)
            .header("_csrf", "dummyCsrfToken")
            .cookie(new Cookie("JSESSIONID", "example-session-id"))
            .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk()).andDo(print());
        Assertions.assertEquals(Status.ACCEPTED, friendshipRepository.findByFromAndTo(user1, user2).get().getStatus());
        Assertions.assertEquals(Status.ACCEPTED, friendshipRepository.findByFromAndTo(user2, user1).get().getStatus());

        //docs
        result.andDo(write().document(
            pathParameters(parameterWithName("fid").description("Id of the requested friendship.")),
            requestHeaders(headerWithName("_csrf").description("CSRF Token")),
            responseBody(),
            requestBody(),
            curlRequest(),
            httpRequest(),
            httpResponse(),
            requestCookies(
                cookieWithName("JSESSIONID").description("Authenticated user session ID cookie(Requesting user)"))
        ));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void block() throws Exception {

        //given
        String toUserUuid = "friendUUID";

        //when
        ResultActions result = mockMvc.perform(post("/friend/block")
                .queryParam("toUserUuid", toUserUuid)
            .header("_csrf", "dummyCsrfToken")
            .cookie(new Cookie("JSESSIONID", "example-session-id"))
            .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk()).andDo(print());
        Assertions.assertEquals(Status.BLOCKED, friendshipRepository.findByFromAndTo(user1, user2).get().getStatus());

        //docs
        result.andDo(write().document(
            queryParameters(parameterWithName("toUserUuid").description("UUID of the target user.")),
            requestHeaders(headerWithName("_csrf").description("CSRF Token")),
            responseBody(),
            requestBody(),
            curlRequest(),
            httpRequest(),
            httpResponse(),
            requestCookies(
                cookieWithName("JSESSIONID").description("Authenticated user session ID cookie(Requesting user)"))
        ));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void memo() throws Exception {
        //given
        Long fid = friendshipRepository.save(new Friendship(user1, user2, Status.ACCEPTED)).getFid();
        String memo = "note";

        //when
        ResultActions result = mockMvc.perform(put("/friend/{fid}/note", fid)
                .queryParam("note", memo)
            .header("_csrf", "dummyCsrfToken")
            .cookie(new Cookie("JSESSIONID", "example-session-id"))
            .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk()).andDo(print());
        Assertions.assertEquals(memo, friendshipRepository.findByFromAndTo(user1, user2).get().getNote());

        //docs
        result.andDo(write().document(
            pathParameters(parameterWithName("fid").description("Id of the target friendship.")),
            queryParameters(parameterWithName("note").description("content of note.")),
            requestHeaders(headerWithName("_csrf").description("CSRF Token")),
            responseBody(),
            requestBody(),
            curlRequest(),
            httpRequest(),
            httpResponse(),
            requestCookies(
                cookieWithName("JSESSIONID").description("Authenticated user session ID cookie(Requesting user)"))
        ));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void getListFriend() throws Exception {
        //given
        friendshipRepository.save(new Friendship(user2, user1, Status.ACCEPTED));
        friendshipRepository.save(new Friendship(user1, user2, Status.ACCEPTED));
        PageReqDTO pageReqDTO = new PageReqDTO(1, 10, "", "", null, null);

        //when
        ResultActions result = mockMvc.perform(get("/friend/list")
            .queryParam("page", "1")
            .queryParam("size", "10")
            .header("_csrf", "dummyCsrfToken")
            .cookie(new Cookie("JSESSIONID", "example-session-id")));

        //then
        result.andExpect(status().isOk()).andDo(print());

        //docs
    }
}