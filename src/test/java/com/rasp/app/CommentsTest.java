package com.rasp.app;
import com.rasp.app.helper.*;
import com.rasp.app.resource.*;
import jakarta.servlet.http.Cookie;
import org.hamcrest.Matchers;
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
import platform.resource.BaseResource;
import platform.resource.session;
import platform.util.ApplicationException;
import platform.webservice.ServletContext;

import javax.xml.stream.events.Comment;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CommentsTest {


    @Autowired
    private MockMvc mockMvc;

    @Mock
    private session httpSession;

    private ServletContext context;

    @BeforeEach
    void setUp() throws ApplicationException {
        MockitoAnnotations.openMocks(this);
        context = new ServletContext(httpSession);
        Registry.register();
    }

    @Test
    void createComments() throws Exception {
        String loginRequestBody = "{\"username\":\"aman\",\"password\":\"aman\"}";

        MvcResult mvcResult= mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody)
                ).andExpect(status().isOk())
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().exists("refresh_token"))
                .andDo(print())
                .andReturn();

        String accessToken = mvcResult.getResponse().getCookie("access_token").getValue();
        String refreshToken = mvcResult.getResponse().getCookie("refresh_token").getValue();

        BaseResource[] baseResources= IssueHelper.getInstance().getAll();
        Issue issue=null;
        for (BaseResource br:baseResources) {
            issue = (Issue) br;
            break;
        }
        BaseResource[] users= UsersHelper.getInstance().getAll();
        Users user1=null;
        for (BaseResource br:users) {
            user1 = (Users) br;
            break;
        }

        String issue_id=issue.getId();
        String user_id=user1.getId();
        String requestBody = String.format(
                "{\"issue_id\":\"%s\",\"user_id\":\"%s\",\"content\":\"somthething\"}",
                issue_id, user_id
        );
        String encodedResource = Base64.getEncoder().encodeToString(requestBody.getBytes());
        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("resource=" + URLEncoder.encode(encodedResource, StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + accessToken)
                        .cookie(new Cookie("refresh_token", refreshToken))
                ).andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("\"message\":\"Success\"")))
                .andExpect(content().string(Matchers.containsString("\"errCode\":0")));

    }

    @Test
    void fetchComments() throws Exception {
        String loginRequestBody = "{\"username\":\"aman\",\"password\":\"aman\"}";

        MvcResult mvcResult= mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody)
                ).andExpect(status().isOk())
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().exists("refresh_token"))
                .andDo(print())
                .andReturn();

        String accessToken = mvcResult.getResponse().getCookie("access_token").getValue();
        String refreshToken = mvcResult.getResponse().getCookie("refresh_token").getValue();
        ;


        mockMvc.perform(get("/api/comments?queryId=GET_ALL")

                        .header("Authorization", "Bearer " + accessToken)
                        .cookie(new Cookie("refresh_token", refreshToken))
                ).andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("\"message\":\"Success\"")))
                .andExpect(content().string(Matchers.containsString("\"errCode\":0")));
    }

    @Test
    void updateComments() throws Exception {
        String loginRequestBody = "{\"username\":\"aman\",\"password\":\"aman\"}";

        MvcResult mvcResult= mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody)
                ).andExpect(status().isOk())
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().exists("refresh_token"))
                .andDo(print())
                .andReturn();

        String accessToken = mvcResult.getResponse().getCookie("access_token").getValue();
        String refreshToken = mvcResult.getResponse().getCookie("refresh_token").getValue();

        BaseResource[] baseResources= CommentsHelper.getInstance().getAll();
        Comments comments=null;
        for(BaseResource br:baseResources) {
            comments = (Comments) br;
            break;
        }

        String issue_id=comments.getIssue_id();
        String id=comments.getId();
        String user_id=comments.getUser_id();

        String requestBody = String.format(
                "{\"id\":\"%s\",\"issue_id\":\"red\",\"user_id\":\"%s\",\"content\":\"somthething\"}",
               id, issue_id, user_id
        );
        String encodedResource = Base64.getEncoder().encodeToString(requestBody.getBytes());
        mockMvc.perform(post("/api/comments?action=MODIFY")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("resource=" + URLEncoder.encode(encodedResource, StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + accessToken)
                        .cookie(new Cookie("refresh_token", refreshToken))
                ).andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    void deleteComment() throws Exception {
        String loginRequestBody = "{\"username\":\"aman\",\"password\":\"aman\"}";

        MvcResult mvcResult= mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody)
                ).andExpect(status().isOk())
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().exists("refresh_token"))
                .andDo(print())
                .andReturn();

        String accessToken = mvcResult.getResponse().getCookie("access_token").getValue();
        String refreshToken = mvcResult.getResponse().getCookie("refresh_token").getValue();

        BaseResource[] baseResources= CommentsHelper.getInstance().getAll();
        Comments comment=null;
        for(BaseResource br:baseResources) {
            comment = (Comments) br;
            break;
        }

        String id=comment.getId();
        String requestBody = String.format(
                "{\"id\":\"%s\"}",
                id
        );
        String encodedResource = Base64.getEncoder().encodeToString(requestBody.getBytes());
        mockMvc.perform(post("/api/comments?action=DELETE")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("resource=" + URLEncoder.encode(encodedResource, StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + accessToken)
                        .cookie(new Cookie("refresh_token", refreshToken))
                ).andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("\"message\":\"Success\"")))
                .andExpect(content().string(Matchers.containsString("\"errCode\":0")));
    }



}

