

package com.rasp.app;
import com.rasp.app.decorator.IssueUserDecorator;
import com.rasp.app.decorator.LabelIssueDecorator;
import com.rasp.app.decorator.UserProjectDecorator;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import platform.decorator.DecoratorManager;
import platform.resource.session;
import platform.util.ApplicationException;
import platform.webservice.ServletContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserProjectDecTest {


    @Autowired
    private MockMvc mockMvc;

    @Mock
    private session httpSession;

    private ServletContext context;

    @BeforeEach
    void setUp() throws ApplicationException {
        MockitoAnnotations.openMocks(this);
        context = new ServletContext(httpSession);
        DecoratorManager.getInstance().register(new UserProjectDecorator());
    }

    @Test
    void test_GET_PROJECT_BY_USER_ID() throws Exception {
        // First, perform login to get tokens
        String loginRequestBody = "{\"username\":\"aman\",\"password\":\"aman\"}";

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().exists("refresh_token"))
                .andReturn();

        // Get tokens from response cookies
        String accessToken = loginResult.getResponse().getCookie("access_token").getValue();
        String refreshToken = loginResult.getResponse().getCookie("refresh_token").getValue();


        // Perform the GET request with query parameters
        mockMvc.perform(get("/api/user_project_map")
                        .param("queryId", "GET_PROJECT_BY_USER_ID")
                        .param("args", "id:admin11122")
                        .cookie(new Cookie("refresh_token", refreshToken))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.errCode").value(0));

    }

    @Test
    void test_GET_USER_BY_PROJECT_ID() throws Exception {
        // First, perform login to get tokens
        String loginRequestBody = "{\"username\":\"aman\",\"password\":\"aman\"}";

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().exists("refresh_token"))
                .andReturn();

        // Get tokens from response cookies
        String accessToken = loginResult.getResponse().getCookie("access_token").getValue();
        String refreshToken = loginResult.getResponse().getCookie("refresh_token").getValue();


        // Perform the GET request with query parameters
        mockMvc.perform(get("/api/user_project_map")
                        .param("queryId", "GET_USER_BY_PROJECT_ID")
                        .param("args", "id:admin11122")
                        .cookie(new Cookie("refresh_token", refreshToken))
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.errCode").value(0));

    }


}
