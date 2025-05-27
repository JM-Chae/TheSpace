package com.thespace.thespace.controller;

import static com.thespace.thespace.config.RestDocsConfig.CsrfRestDocumentationRequestBuilders.delete;
import static com.thespace.thespace.config.RestDocsConfig.CsrfRestDocumentationRequestBuilders.patch;
import static com.thespace.thespace.config.RestDocsConfig.CsrfRestDocumentationRequestBuilders.post;
import static com.thespace.thespace.config.RestDocsConfig.write;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.restdocs.cli.CliDocumentation.curlRequest;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thespace.thespace.config.DataBaseCleaner;
import com.thespace.thespace.domain.Community;
import com.thespace.thespace.domain.User;
import com.thespace.thespace.domain.UserRole;
import com.thespace.thespace.dto.CommunityDTOs.Create;
import com.thespace.thespace.dto.CommunityDTOs.Modify;
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
class CommunityControllerTest {

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
    private CommunityRepository communityRepository;

    @BeforeEach
    void setup() {
        dataBaseCleaner.clear();
        User user = userRepository.save(new User("testerUser", "testerUUID", "tester", "test@test.test", "password", new ArrayList<>()));
        userRoleRepository.save(new UserRole("ROLE_USER", new ArrayList<>()));
        userService.setRole(user.getId(), "ROLE_USER");
    }

    @Test
    void communityGet() throws Exception {
        //given
        Community community = communityRepository.save(new Community("test", "test"));

        //when
        ResultActions result = mockMvc.perform(get(
            "/community/{communityName}",
            community.getCommunityName()
        ));

        //then
        result.andExpect(status().isOk())
            .andExpectAll(
                jsonPath("$.communityName").value(community.getCommunityName()),
                jsonPath("$.description").value(community.getDescription())
            ).andDo(print());

        //docs
        result.andDo(write().document(requestHeaders(), responseBody(), requestBody(), curlRequest(), httpRequest(), httpResponse(),
            pathParameters(parameterWithName("communityName").description("Community name to search")),
            relaxedResponseFields(
                fieldWithPath("communityId").description("Community ID"),
                fieldWithPath("communityName").description("Community name"),
                fieldWithPath("createDate").description("Created date"),
                fieldWithPath("modDate").description("Modified date"),
                fieldWithPath("description").description("Community description"),
                fieldWithPath("category").description(
                    "List of categories that are open in that community.")
            )
        ));
    }

    @Test
    void communityList() throws Exception {
        //given
        for (int i = 1; i <= 25; i++) {
            communityRepository.save(new Community("test" + i, "test" + i));
        }

        //when
        ResultActions result = mockMvc.perform(get("/community/list")
                .queryParam("page", "0")
                .queryParam("size", "0")
            .queryParam("type", "n")
            .queryParam("keyword", "3"));

        //then
        result.andExpect(status().isOk()).andExpectAll(
            jsonPath("$.total").value("3"),
            jsonPath("$.page").value("1"),
            jsonPath("$.size").value("1000000"),
            jsonPath("$.dtoList[0].communityId").value("3")
        ).andDo(print());

        //docs
        result.andDo(write().document(requestHeaders(), responseBody(), requestBody(), curlRequest(), httpRequest(), httpResponse(),
            queryParameters(
            parameterWithName("page").description("0"),
            parameterWithName("size").description("0"),
            parameterWithName("type").description(
                "n / Use n to distinguish it from other list lookup APIs."),
            parameterWithName("keyword").description("Keywords to search for")
        ), relaxedResponseFields(
            fieldWithPath("page").description(
                "1 / This is fixed value because it will show the list all on one page."),
            fieldWithPath("size").description(
                "1000000 / This is fixed value because it will show the list all on one page. It may be different if we introduce a community cap or group-specific categorising in the future."),
            fieldWithPath("total").description("Total number of search hits."),
            fieldWithPath("start").description(
                "1 / This is fixed value because it will show the list all on one page."),
            fieldWithPath("end").description(
                "1 / This is fixed value because it will show the list all on one page."),
            fieldWithPath("prev").description(
                "false / This is fixed value because it will show the list all on one page."),
            fieldWithPath("next").description(
                "false / This is fixed value because it will show the list all on one page."),
            fieldWithPath("dtoList").description("List of rows of search results.")
        )));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void communityCreate() throws Exception {
        //given
        Create createDTO = new Create("test", "test");

        //when
        ResultActions result = mockMvc.perform(post("/community").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createDTO))
            .queryParam("nameCheck", "true"));

        //then
        result.andExpect(status().isOk()).andDo(print());
        Community community = communityRepository.findById(1L).orElseThrow();
        if (!(community.getCommunityName().equals(createDTO.communityName())
            && community.getDescription().equals(createDTO.description()))) {
            throw new Exception();
        }

        //docs
        result.andDo(write().document(requestHeaders(), responseBody(), requestBody(), curlRequest(), httpRequest(), httpResponse(),
            requestFields(
            fieldWithPath("communityName").description(
                "Name of community"),
            fieldWithPath("description").description("Description of community")
        ), queryParameters(
            parameterWithName("nameCheck").description("result of name check")
        )));
    }

    @Test
    void communityCheck() throws Exception {
        //given
        String test = "Test Community 1";

        //when
        ResultActions result = mockMvc.perform(get("/community/nameCheck").queryParam(
            "communityName",
            test
        ));

        //then
        result.andExpect(status().isOk()).andExpect(jsonPath("$").value(true)).andDo(print());

        //docs
        result.andDo(write().document(requestHeaders(), responseBody(), requestBody(), curlRequest(), httpRequest(), httpResponse(),
            queryParameters(parameterWithName("communityName").description(
            "Name of community to check"))));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void communityDelete() throws Exception {
        //given
        Community community = communityRepository.save(new Community("test", "test"));

        UserRole userRole = userRoleRepository.save(new UserRole(
            "ADMIN_" + community.getCommunityName(),
            new ArrayList<>()
        ));
        userService.setRole("testerUser", userRole.getRole());

        //when
        ResultActions result = mockMvc.perform(delete(
            "/community/{communityId}",
            community.getCommunityId()
        ).queryParam("communityName", community.getCommunityName()));

        //then
        result.andExpect(status().isOk()).andDo(print());
        assertThrows(
            Exception.class,
            () -> communityRepository.findById(community.getCommunityId())
                .orElseThrow(Exception::new)
        );

        //docs
        result.andDo(write().document(requestHeaders(), responseBody(), requestBody(), curlRequest(), httpRequest(), httpResponse(),
            pathParameters(parameterWithName("communityId").description("CommunityID to delete")),
            queryParameters(
                parameterWithName("communityName").description("Name of community to delete")
            )
        ));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void communityHasAdminList() throws Exception {
        //given
        for (int i = 1; i <= 4; i++) {
            Community community = communityRepository.save(new Community("test" + i, "test" + i));

            UserRole userRole = userRoleRepository.save(new UserRole(
                "ADMIN_" + community.getCommunityName(),
                new ArrayList<>()
            ));
            userService.setRole("testerUser", userRole.getRole());
        }

        //when
        ResultActions result = mockMvc.perform(get("/community/list/admin"));

        //then
        result.andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(4)).andDo(print());

        //docs
        result.andDo(write().document(requestHeaders(), responseBody(), requestBody(), curlRequest(), httpRequest(), httpResponse(),
            responseFields(fieldWithPath("[]").description(
                "List of CommunityID what performer has admin role."))
        ));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void communityModify() throws Exception {
        //given
        Community community = communityRepository.save(new Community(
            "test",
            "test"
        ));

        Modify modifyDTO = new Modify(
            community.getCommunityId(),
            community.getCommunityName(),
            "modify"
        );

        UserRole userRole = userRoleRepository.save(new UserRole(
            "ADMIN_" + community.getCommunityName(),
            new ArrayList<>()
        ));
        userService.setRole("testerUser", userRole.getRole());

        //when
        ResultActions result = mockMvc.perform(patch("/community/modify")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(modifyDTO)));

        //then
        result.andExpect(status().isOk()).andDo(print());
        community = communityRepository.findById(community.getCommunityId()).orElseThrow();
        if (!community.getDescription().equals(modifyDTO.description())) {
            throw new Exception();
        }

        result.andDo(write().document(requestHeaders(), responseBody(), requestBody(), curlRequest(), httpRequest(), httpResponse(),
            requestFields(
            fieldWithPath("communityId").description("CommunityID to update"),
            fieldWithPath("communityName").description("Community name to update"),
            fieldWithPath("description").description("Description what will change"))));
    }
}