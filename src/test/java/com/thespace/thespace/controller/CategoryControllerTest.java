package com.thespace.thespace.controller;

import static com.thespace.thespace.config.RestDocsConfig.restDocsConfig;
import static com.thespace.thespace.config.RestDocsConfig.write;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
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
class CategoryControllerTest {

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

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentation) {
        mockMvc = restDocsConfig(context, restDocumentation);
        dataBaseCleaner.clear();
    }

    @Test
    void categoryList() throws Exception {
        //given
        Community community = communityRepository.save(new Community(
            "test",
            "test",
            new ArrayList<>()
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
        );

        //docs
        result.andDo(write().document(queryParameters(parameterWithName("path").description(
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
    void categoryCreate() throws Exception {
        //given
        Community community = communityRepository.save(new Community(
            "test",
            "test",
            new ArrayList<>()
        ));

        CategoryCreateDTO categoryCreateDTO = new CategoryCreateDTO(
            "test",
            "test",
            community.getCommunityName(),
            community.getCommunityId()
        );

        User admin = userRepository.save(new User(
            "tester",
            "aaaa",
            "aaaa",
            "1234@asdf.zxc",
            "1234",
            new ArrayList<>()
        ));
        UserRole userRole = userRoleRepository.save(new UserRole(
            "ADMIN_" + community.getCommunityName(),
            new ArrayList<>()
        ));
        userService.setRole(admin.getId(), userRole.getRole());

        //when
        ResultActions result = mockMvc.perform(post("/category/admin").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(categoryCreateDTO))
            .queryParam("userId", admin.getId()));

        //then
        result.andExpect(status().isOk());
        Category category = categoryRepository.findById(1L).orElseThrow();
        if (!(category.getCategoryName().equals(categoryCreateDTO.categoryName())
            && category.getCategoryType().equals(categoryCreateDTO.categoryType())
            && category.getCommunity().getCommunityId().equals(categoryCreateDTO.communityId()))) {
            throw new Exception();
        }

        //docs
        result.andDo(write().document(
            requestFields(
                fieldWithPath("categoryName").description(
                    "Category name"),
                fieldWithPath("categoryType").description("Category Type"),
                fieldWithPath("path").description(
                    "The community name to which that category will belongs."),
                fieldWithPath("communityId").description(
                    "The community ID to which that category will belongs.")
            ),
            queryParameters(parameterWithName("userId").description("ID of the delete performer."))
        ));
    }

    @Test
    void categoryDelete() throws Exception {
        //given
        Community community = communityRepository.save(new Community(
            "test",
            "test",
            new ArrayList<>()
        ));

        Category category = categoryRepository.save(new Category(
            community.getCommunityName(),
            "test",
            "test",
            community,
            new ArrayList<>()
        ));

        User admin = userRepository.save(new User(
            "tester",
            "aaaa",
            "aaaa",
            "1234@asdf.zxc",
            "1234",
            new ArrayList<>()
        ));
        UserRole userRole = userRoleRepository.save(new UserRole(
            "ADMIN_" + community.getCommunityName(),
            new ArrayList<>()
        ));
        userService.setRole(admin.getId(), userRole.getRole());

        //when
        ResultActions result = mockMvc.perform(delete(
            "/category/{categoryId}/admin", category.getCategoryId())
            .queryParam("userId", admin.getId())
            .queryParam("communityName", community.getCommunityName()));

        //then
        result.andExpect(status().isOk());
        assertThrows(
            Exception.class,
            () -> categoryRepository.findById(category.getCategoryId()).orElseThrow(Exception::new)
        );

        //docs
        result.andDo(write().document(
            pathParameters(parameterWithName("categoryId").description("Category ID what to delete")),
            queryParameters(
                parameterWithName("userId").description("ID of the delete performer."),
                parameterWithName("communityName").description(
                    "The community name to which that category belongs.")
            )
        ));
    }
}