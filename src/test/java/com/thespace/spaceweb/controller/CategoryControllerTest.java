package com.thespace.spaceweb.controller;

import static com.thespace.config.RestDocsConfig.CsrfRestDocumentationRequestBuilders.delete;
import static com.thespace.config.RestDocsConfig.CsrfRestDocumentationRequestBuilders.post;
import static com.thespace.config.RestDocsConfig.write;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thespace.config.DataBaseCleaner;
import com.thespace.spaceweb.category.Category;
import com.thespace.spaceweb.category.CategoryDTOs.Create;
import com.thespace.spaceweb.category.CategoryRepository;
import com.thespace.spaceweb.community.Community;
import com.thespace.spaceweb.community.CommunityRepository;
import com.thespace.spaceweb.user.User;
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
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataBaseCleaner dataBaseCleaner;

    static Long rid = 0L;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private UserRoleService userRoleService;

    @BeforeEach
    void setup() {
        dataBaseCleaner.clear();
        User user = userRepository.save(new User("testerUser",
            "testerUUID",
            "tester",
            "test@test.test",
            "password",
            new ArrayList<>(),
            "nice to meet you",
            "😊"
        ));
        rid = userRoleRepository.save(new UserRole("ROLE_USER", new ArrayList<>())).getId();
        userRoleService.setRole(user.getId(), rid);
    }

    @Test
    void categoryList() throws Exception {
        //given
        Community community = communityRepository.save(new Community("test", "test"));

        for (int i = 1; i <= 10; i++) {
            categoryRepository.save(new Category("test " + (i),
                "test",
                community,
                new ArrayList<>()
            ));
        }

        //when
        ResultActions result = mockMvc.perform(get("/category/list").queryParam("communityId",
                community.getId().toString()
            )
            .header("_csrf", "dummyCsrfToken")
            .cookie(new Cookie("JSESSIONID", "example-session-id")));

        //then
        result.andExpect(status().isOk()).andExpectAll(jsonPath("$.[0].categoryId").value("1"),
            jsonPath("$.[0].categoryName").value("test 1"),
            jsonPath("$.[9].categoryId").value("10"),
            jsonPath("$.[9].categoryName").value("test 10")
        ).andDo(print());

        //docs
        result.andDo(write().document(requestHeaders(),
            responseBody(),
            requestBody(),
            curlRequest(),
            httpRequest(),
            httpResponse(),
            relaxedResponseFields(fieldWithPath("[].categoryId").description("Category ID"),
                fieldWithPath("[].categoryName").description("Category Name"),
                fieldWithPath("[].categoryType").description("Category Type"),
                //Will implement several types later.
                fieldWithPath("[].communityId").description(
                    "The community ID to which that category belongs.")
            ),
            requestCookies(cookieWithName("JSESSIONID").description(
                "Authenticated user session ID cookie")),
            requestHeaders(headerWithName("_csrf").description("CSRF Token"))
        ));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void categoryCreate() throws Exception {
        //given
        Community community = communityRepository.save(new Community("test", "test"));

        Create categoryCreateDTO = new Create("test", "test", community.getId());

        UserRole userRole = userRoleRepository.save(new UserRole("ADMIN_" + community.getName(),
            new ArrayList<>()
        ));
        userRoleService.setRole("testerUser", userRole.getId());

        //when
        ResultActions result = mockMvc.perform(post("/category/admin").contentType(MediaType.APPLICATION_JSON)
            .header("_csrf", "dummyCsrfToken")
            .cookie(new Cookie("JSESSIONID", "example-session-id"))
            .content(objectMapper.writeValueAsString(categoryCreateDTO)));

        //then
        result.andExpect(status().isOk()).andDo(print());
        Category category = categoryRepository.findById(1L).orElseThrow();
        if (!(category.getName().equals(categoryCreateDTO.name()) && category.getType()
            .equals(categoryCreateDTO.type()) && category.getCommunity()
            .getId()
            .equals(categoryCreateDTO.communityId()))) {
            throw new Exception();
        }

        //docs
        result.andDo(write().document(requestHeaders(),
            responseBody(),
            requestBody(),
            curlRequest(),
            httpRequest(),
            httpResponse(),
            requestFields(fieldWithPath("name").description("Category name"),
                fieldWithPath("type").description("Category Type"),
                fieldWithPath("communityId").description(
                    "The community ID to which that category will belongs.")
            ),
            requestCookies(cookieWithName("JSESSIONID").description(
                "Authenticated user session ID cookie")),
            requestHeaders(headerWithName("_csrf").description("CSRF Token"))
        ));
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void categoryDelete() throws Exception {
        //given
        Community community = communityRepository.save(new Community("test", "test"));

        Category category = categoryRepository.save(new Category("test",
            "test",
            community,
            new ArrayList<>()
        ));

        UserRole userRole = userRoleRepository.save(new UserRole("ADMIN_" + community.getName(),
            new ArrayList<>()
        ));
        userRoleService.setRole("testerUser", userRole.getId());

        //when
        ResultActions result = mockMvc.perform(delete("/category/{categoryId}/admin",
            category.getId()
        ).header("_csrf", "dummyCsrfToken")
            .cookie(new Cookie("JSESSIONID", "example-session-id"))
            .queryParam("communityName", community.getName())).andDo(print());

        //then
        result.andExpect(status().isOk());
        assertThrows(Exception.class,
            () -> categoryRepository.findById(category.getId()).orElseThrow(Exception::new)
        );

        //docs
        result.andDo(write().document(requestHeaders(),
            responseBody(),
            requestBody(),
            curlRequest(),
            httpRequest(),
            httpResponse(),
            pathParameters(parameterWithName("categoryId").description("Category ID what to delete")),
            queryParameters(parameterWithName("communityName").description(
                "The community name to which that category belongs.")),
            requestCookies(cookieWithName("JSESSIONID").description(
                "Authenticated user session ID cookie")),
            requestHeaders(headerWithName("_csrf").description("CSRF Token"))
        ));
    }
}