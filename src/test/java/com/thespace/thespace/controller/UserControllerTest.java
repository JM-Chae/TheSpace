package com.thespace.thespace.controller;

import static com.thespace.thespace.config.RestDocsConfig.write;
import static org.springframework.restdocs.cli.CliDocumentation.curlRequest;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.http.HttpDocumentation.httpRequest;
import static org.springframework.restdocs.http.HttpDocumentation.httpResponse;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thespace.thespace.config.DataBaseCleaner;
import com.thespace.thespace.domain.User;
import com.thespace.thespace.domain.UserRole;
import com.thespace.thespace.dto.user.UserRegisterDTO;
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
    private UserService userService;

    @Autowired
    private UserRoleRepository userRoleRepository;


    @BeforeEach
    void setup() {
        dataBaseCleaner.clear();
        User user = userRepository.save(new User("testerUser", "testerUUID", "tester", "test@test.test", "password", new ArrayList<>()));
        userRoleRepository.save(new UserRole("ROLE_USER", new ArrayList<>()));
        userService.setRole(user.getId(), "ROLE_USER");
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
        result.andDo(write().document(queryParameters(
            parameterWithName("id").description("The ID to check.")
        ), requestHeaders(), responseBody(), requestBody(),httpRequest(), httpResponse(), curlRequest()));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void userGetUserInfo() throws Exception {
        //given user "testerUser"

        //when
        ResultActions result = mockMvc.perform(get("/user/info"));

        //then
        result.andExpect(status().isOk()).andExpect(jsonPath("$.id").value("testerUser")).andDo(print());

        //docs
        result.andDo(write().document(responseFields(
                fieldWithPath("uuid").description("UUID of login user"),
            fieldWithPath("id").description("ID of login user"),
            fieldWithPath("name").description("Nickname of login user"),
            fieldWithPath("email").description("Email of login user"),
            fieldWithPath("roles").description("Roles(in community) of login user")
            ), requestHeaders(), responseBody(), requestBody(),httpRequest(), httpResponse(), curlRequest()));
    }

    @Test
    void userRegister() throws Exception {
        //given
        UserRegisterDTO dto = new UserRegisterDTO("AAAAAA", "AAAAAA", "AAAA@AAAA.AAA", "password");

        //when
        ResultActions result = mockMvc.perform(post("/user").queryParam("checkid", "true").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)));

        //then
        result.andExpect(status().isOk()).andDo(print());

        //docs
        result.andDo(write().document(requestFields(
            fieldWithPath("id").description("ID to register"),
            fieldWithPath("name").description("Nickname to register"),
            fieldWithPath("email").description("Email to register"),
            fieldWithPath("password").description("password to register")
        ), requestHeaders(), responseBody(), requestBody(),httpRequest(), httpResponse(), curlRequest()));
    }
}