package com.thespace.thespace.controller;

import static com.thespace.thespace.config.RestDocsConfig.write;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thespace.thespace.config.DataBaseCleaner;
import com.thespace.thespace.domain.Category;
import com.thespace.thespace.domain.Community;
import com.thespace.thespace.domain.User;
import com.thespace.thespace.domain.UserRole;
import com.thespace.thespace.dto.category.CategoryCreateDTO;
import com.thespace.thespace.repository.CategoryRepository;
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
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataBaseCleaner dataBaseCleaner;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private CommunityRepository communityRepository;

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
    void categoryList() throws Exception {
        //given
        Community community = communityRepository.save(new Community(
            "test",
            "test"
        ));

        for (int i = 1; i <= 10; i++) {
            categoryRepository.save(new Category(
                community.getCommunityName(),
                "test " + (i),
                "test",
                community,
                new ArrayList<>()
            ));
        }

        //when
        ResultActions result = mockMvc.perform(get("/category/list").queryParam(
            "path",
            community.getCommunityName()
        ));

        //then
        result.andExpect(status().isOk()).andExpectAll(
            jsonPath("$.[0].categoryId").value("1"),
            jsonPath("$.[0].categoryName").value("test 1"),
            jsonPath("$.[9].categoryId").value("10"),
            jsonPath("$.[9].categoryName").value("test 10")
        ).andDo(print());

        //docs
        result.andDo(write().document(requestHeaders(), responseBody(), requestBody(), curlRequest(), httpRequest(), httpResponse(),
            queryParameters(parameterWithName("path").description(
            "The community name to which that category belongs.")), relaxedResponseFields(
            fieldWithPath("[].categoryId").description("Category ID"),
            fieldWithPath("[].categoryName").description("Category Name"),
            fieldWithPath("[].categoryType").description("Category Type"),
            //Will implement several types later.
            fieldWithPath("[].communityId").description(
                "The community ID to which that category belongs.")
        )));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void categoryCreate() throws Exception {
        //given
        Community community = communityRepository.save(new Community(
            "test",
            "test"
        ));

        CategoryCreateDTO categoryCreateDTO = new CategoryCreateDTO(
            "test",
            "test",
            community.getCommunityName(),
            community.getCommunityId()
        );

        UserRole userRole = userRoleRepository.save(new UserRole(
            "ADMIN_" + community.getCommunityName(),
            new ArrayList<>()
        ));
        userService.setRole("testerUser", userRole.getRole());

        //when
        ResultActions result = mockMvc.perform(post("/category/admin").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(categoryCreateDTO)));

        //then
        result.andExpect(status().isOk()).andDo(print());
        Category category = categoryRepository.findById(1L).orElseThrow();
        if (!(category.getCategoryName().equals(categoryCreateDTO.categoryName())
            && category.getCategoryType().equals(categoryCreateDTO.categoryType())
            && category.getCommunity().getCommunityId().equals(categoryCreateDTO.communityId()))) {
            throw new Exception();
        }

        //docs
        result.andDo(write().document(requestHeaders(), responseBody(), requestBody(), curlRequest(), httpRequest(), httpResponse(),
            requestFields(
                fieldWithPath("categoryName").description(
                    "Category name"),
                fieldWithPath("categoryType").description("Category Type"),
                fieldWithPath("path").description(
                    "The community name to which that category will belongs."),
                fieldWithPath("communityId").description(
                    "The community ID to which that category will belongs.")
            )));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void categoryDelete() throws Exception {
        //given
        Community community = communityRepository.save(new Community(
            "test",
            "test"
        ));

        Category category = categoryRepository.save(new Category(
            community.getCommunityName(),
            "test",
            "test",
            community,
            new ArrayList<>()
        ));

        UserRole userRole = userRoleRepository.save(new UserRole(
            "ADMIN_" + community.getCommunityName(),
            new ArrayList<>()
        ));
        userService.setRole("testerUser", userRole.getRole());

        //when
        ResultActions result = mockMvc.perform(delete(
            "/category/{categoryId}/admin", category.getCategoryId())
            .queryParam("communityName", community.getCommunityName())).andDo(print());

        //then
        result.andExpect(status().isOk());
        assertThrows(
            Exception.class,
            () -> categoryRepository.findById(category.getCategoryId()).orElseThrow(Exception::new)
        );

        //docs
        result.andDo(write().document(requestHeaders(), responseBody(), requestBody(), curlRequest(), httpRequest(), httpResponse(),
            pathParameters(parameterWithName("categoryId").description("Category ID what to delete")),
            queryParameters(
                parameterWithName("communityName").description(
                    "The community name to which that category belongs.")
            )
        ));
    }
}