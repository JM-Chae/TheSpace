package com.thespace.common.notification;

import static com.thespace.config.RestDocsConfig.write;
import static org.springframework.restdocs.cli.CliDocumentation.curlRequest;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.http.HttpDocumentation.httpRequest;
import static org.springframework.restdocs.http.HttpDocumentation.httpResponse;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.thespace.config.DataBaseCleaner;
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
class NotificationControllerTest {

    static User user = new User();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DataBaseCleaner dataBaseCleaner;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    @BeforeEach
    void setup() {
        dataBaseCleaner.clear();
        user = userRepository.save(new User(
            "testerUser",
            "testerUUID",
            "tester",
            "test@test.test",
            "password",
            new ArrayList<>(),
            "nice to meet you",
            "😊"
        ));
        Long rid = userRoleRepository.save(new UserRole("ROLE_USER", new ArrayList<>())).getId();
        userRoleService.setRole(user.getId(), rid);
    }

    @Test
    @WithUserDetails(value = "testerUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void getNotifications() throws Exception {
        //given
        for (int i = 1; i <= 10; i++) {
            Notification notification = new Notification(
                user,
                "No." + i,
                "Content: " + i,
                "URL",
                DataPayload.forFriendShipRequest(null, "23123#23123"),
                NotificationType.NOTICE
            );

            notificationRepository.save(notification);
        }

        //given
        //isRead = false;
        //page = 1;
        //size = 10;

        //when
        ResultActions result = mockMvc.perform(get("/notification")
            .queryParam("isRead", "false")
            .queryParam("page", "1")
            .queryParam("size", "10")
            .cookie(new Cookie("JSESSIONID", "example-session-id"))
        );

        //then
        result.andExpect(status().isOk()).andDo(print());

        //docs
        result.andDo(write().document(requestHeaders(),
            responseBody(),
            requestBody(),
            curlRequest(),
            httpRequest(),
            httpResponse(),
            queryParameters(parameterWithName("isRead")
                .description("If true, only unread notifications are displayed. If false, all notifications are displayed."),
                parameterWithName("page").description("Page number to display in the full query results."),
                parameterWithName("size").description("Number of rows to display search results.")
                ),
            requestCookies(cookieWithName("JSESSIONID").description(
                "Authenticated user session ID cookie"))
        ));
    }
}