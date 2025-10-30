

package com.rasp.app;
import com.rasp.app.decorator.IssueUserDecorator;
import com.rasp.app.decorator.LabelIssueDecorator;
import com.rasp.app.decorator.UserProjectDecorator;
import io.github.cdimascio.dotenv.Dotenv;
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
import org.springframework.web.context.WebApplicationContext;
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
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private session httpSession;


    private static ServletContext context;
    private static String accessToken;
    private static String refreshToken;

    static {
        Dotenv dotenv = Dotenv.configure().load();
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );
    }



    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        context = new ServletContext(httpSession);
        DecoratorManager.getInstance().register(new IssueUserDecorator());
        Registry.register();
        // Only login if we don't have tokens yet
        if (accessToken == null || refreshToken == null) {
            // Perform login
            String loginRequestBody = "{\"username\":\"aman\",\"password\":\"aman\"}";
            MvcResult mvcResult = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginRequestBody))
                    .andExpect(status().isOk())
                    .andExpect(cookie().exists("access_token"))
                    .andExpect(cookie().exists("refresh_token"))
                    .andReturn();

            accessToken = mvcResult.getResponse().getCookie("access_token").getValue();
            refreshToken = mvcResult.getResponse().getCookie("refresh_token").getValue();
        }
    }

    @Test
    void test_GET_PROJECT_BY_USER_ID() throws Exception {



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
