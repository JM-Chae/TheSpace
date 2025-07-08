package com.thespace.spaceweb.controller;

import static com.thespace.config.RestDocsConfig.CsrfRestDocumentationRequestBuilders.patch;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.requestBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thespace.config.DataBaseCleaner;
import com.thespace.spaceweb.user.User;
import com.thespace.spaceweb.user.UserDTOs;
import com.thespace.spaceweb.user.UserDTOs.Register;
import com.thespace.spaceweb.user.UserDTOs.UpdateInfo;
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
class UserControllerTest {

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
            "Nice to meet you",
            "😊"
        ));
        Long rid = userRoleRepository.save(new UserRole("ROLE_USER", new ArrayList<>())).getId();
        userRoleService.setRole(user.getId(), rid);
    }

    @Test
    void userCheck() throws Exception {
        //given
        String id = "testerID";

        //when
        ResultActions result = mockMvc.perform(get("/user/checkid").queryParam("id", id));

        //then
        result.andExpect(status().isOk()).andExpect(jsonPath("$").value("true"));

        //docs
        result.andDo(write().document(
            queryParameters(parameterWithName("id").description(
                "The ID to check.")),
            requestHeaders(),
            responseBody(),
            requestBody(),
            httpRequest(),
            httpResponse(),
            curlRequest()
        ));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void userGetUserInfo() throws Exception {
        //given user "testerUser"

        //when
        ResultActions result = mockMvc.perform(get("/user/info"));

        //then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.uuid").value("testerUUID"))
            .andDo(print());

        //docs
        result.andDo(write().document(
            responseFields(
                fieldWithPath("uuid").description(
                    "UUID of login user"),
                fieldWithPath("name").description("Nickname of login user"),
                fieldWithPath("roles").description("Roles(in community) of login user")
            ),
            requestHeaders(),
            responseBody(),
            requestBody(),
            httpRequest(),
            httpResponse(),
            curlRequest()
        ));
    }

    @Test
    void userRegister() throws Exception {
        //given
        Register dto = new Register(
            "AAAAAA",
            "AAAAAA",
            "AAAA@AAAA.AAA",
            "password",
            "Nice to meet you",
            "😊"
        );

        //when
        ResultActions result = mockMvc.perform(post("/user").queryParam("checkid", "true")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)));

        //then
        result.andExpect(status().isOk()).andDo(print());

        //docs
        result.andDo(write().document(
            requestFields(
                fieldWithPath("id").description("ID to register"),
                fieldWithPath("name").description("Nickname to register"),
                fieldWithPath("email").description("Email to register"),
                fieldWithPath("password").description("password to register"),
                fieldWithPath("introduce").description("introduce"),
                fieldWithPath("signature").description("An emoji to express oneself")
            ),
            requestHeaders(),
            responseBody(),
            requestBody(),
            httpRequest(),
            httpResponse(),
            curlRequest()
        ));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void getMyPage() throws Exception {

        //given
        String target = "testerUUID";

        //when
        ResultActions result = mockMvc.perform(get("/user/{uuid}/mypage", target).header(
                "_csrf",
                "dummyCsrfToken"
            )
            .cookie(new Cookie("JSESSIONID", "example-session-id"))
            .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk()).andDo(print());

        //docs
        result.andDo(write().document(
            pathParameters(parameterWithName("uuid").description("The UUID of the target to search.")),
            responseFields(
                fieldWithPath("signature").description("The signature emoji of the target user."),
                fieldWithPath("name").description("The nickname of the target user."),
                fieldWithPath("uuid").description("The UUID of the target user."),
                fieldWithPath("introduce").description("The target user's self-introduction."),
                fieldWithPath("joinedOn").description("The date the user joined.")
            ),
            requestCookies(cookieWithName("JSESSIONID").description(
                "Authenticated user session ID cookie")),
            requestHeaders(headerWithName("_csrf").description("CSRF Token")),
            responseBody(),
            requestBody(),
            httpRequest(),
            httpResponse(),
            curlRequest()
        ));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void modifyMyPage() throws Exception {
        //given
        UserDTOs.UpdateInfo dto = new UpdateInfo("", "modified", "");

        //when
        ResultActions result = mockMvc.perform(patch("/user/myinfo").header(
                "_csrf",
                "dummyCsrfToken"
            )
            .cookie(new Cookie("JSESSIONID", "example-session-id"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)));

        //then
        result.andExpect(status().isOk()).andDo(print());

        //docs
        result.andDo(write().document(
            requestFields(
                fieldWithPath("name").description(
                    "Nickname to update").optional(),
                fieldWithPath("introduce").description("Self-introduce to modify").optional(),
                fieldWithPath("signature").description("An emoji to modify.").optional()
            ),
            requestCookies(cookieWithName("JSESSIONID").description(
                "Authenticated user session ID cookie")),
            requestHeaders(headerWithName("_csrf").description("CSRF Token")),
            responseBody(),
            requestBody(),
            httpRequest(),
            httpResponse(),
            curlRequest()
        ));
    }
}