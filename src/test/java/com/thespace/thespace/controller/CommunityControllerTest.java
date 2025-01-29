package com.thespace.thespace.controller;

import static com.thespace.thespace.config.RestDocsConfig.restDocsConfig;
import static com.thespace.thespace.config.RestDocsConfig.write;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedRequestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thespace.thespace.config.DataBaseCleaner;
import com.thespace.thespace.domain.Community;
import com.thespace.thespace.domain.User;
import com.thespace.thespace.dto.community.CommunityCreateDTO;
import com.thespace.thespace.dto.page.PageReqDTO;
import com.thespace.thespace.repository.CommunityRepository;
import com.thespace.thespace.repository.UserRepository;
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
class CommunityControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataBaseCleaner dataBaseCleaner;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentation) {
        mockMvc = restDocsConfig(context, restDocumentation);
        dataBaseCleaner.clear();
    }

    @Test
    void communityGet() throws Exception {
        //given
        Community community = communityRepository.save(new Community(
            "test",
            "test"
        ));

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
            );

        //docs
        result.andDo(write().document(
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

        PageReqDTO pageReqDTO = new PageReqDTO(0, 0, "n", "3", "", "");

        //when
        ResultActions result = mockMvc.perform(get("/community/list").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(pageReqDTO)));

        //then
        result.andExpect(status().isOk()).andExpectAll(
            jsonPath("$.total").value("3"),
            jsonPath("$.page").value("1"),
            jsonPath("$.size").value("1000000"),
            jsonPath("$.dtoList[0].communityId").value("23")

        );

        //docs
        result.andDo(write().document(relaxedRequestFields(
            fieldWithPath("type").description(
                "n / Use n to distinguish it from other list lookup APIs."),
            fieldWithPath("keyword").description("Keywords to search for")
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
    void communityCreate() throws Exception {
        //given
        CommunityCreateDTO communityCreateDTO = new CommunityCreateDTO("test", "test");

        User admin = userRepository.save(new User(
            "tester",
            "aaaa",
            "aaaa",
            "1234@asdf.zxc",
            "1234",
            new ArrayList<>()
        ));

        //when
        ResultActions result = mockMvc.perform(post("/community").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(communityCreateDTO))
            .queryParam("userId", admin.getId())
            .queryParam("nameCheck", "true"));

        //then
        result.andExpect(status().isOk());
        Community community = communityRepository.findById(1L).orElseThrow();
        if (!(community.getCommunityName().equals(communityCreateDTO.communityName())
            && community.getDescription().equals(communityCreateDTO.description()))) {
            throw new Exception();
        }

        //docs
        result.andDo(write().document(requestFields(
            fieldWithPath("communityName").description("Name of community"),
            fieldWithPath("description").description("Description of community")
        ), queryParameters(
            parameterWithName("nameCheck").description("result of name check"),
            parameterWithName("userId").description("ID of the create performer.")
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
        result.andExpect(status().isOk()).andExpect(jsonPath("$").value(true));

        //docs
        result.andDo(write().document(queryParameters(parameterWithName("communityName").description(
            "Name of community to check"))));
    }

    @Test
    void communityDelete() {
    }

    @Test
    void communityHasAdminList() {
    }

    @Test
    void communityModify() {
    }
}