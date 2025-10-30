package com.rasp.app;

import com.rasp.app.helper.*;
import com.rasp.app.resource.*;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.Cookie;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import platform.resource.BaseResource;
import platform.resource.session;
import platform.webservice.ServletContext;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CommentsConcurrentTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private session httpSession;

    private static ServletContext context;

    private static Map<String, String> userTokens = new HashMap<>();
    private static Map<String, String> refreshTokens = new HashMap<>();

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

        Registry.register();

        if (userTokens.isEmpty()) {
            String[] users = {"aman", "aman","shashank","rasptest","rasptest1"};
            for (String user : users) {
                String loginRequestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", user, "aman");
                MvcResult mvcResult = mockMvc.perform(post("/api/auth/login")
                                .contentType("application/json")
                                .content(loginRequestBody))
                        .andExpect(status().isOk())
                        .andExpect(cookie().exists("access_token"))
                        .andExpect(cookie().exists("refresh_token"))
                        .andReturn();

                String token = mvcResult.getResponse().getCookie("access_token").getValue();
                String refreshToken = mvcResult.getResponse().getCookie("refresh_token").getValue();

                userTokens.put(user, token);
                refreshTokens.put(user, refreshToken);
            }
            System.out.println("✅ Logged in 5 users once for all tests");
        }
    }

    private Cookie[] getAuthCookies(String username) {
        return new Cookie[]{
                new Cookie("access_token", userTokens.get(username)),
                new Cookie("refresh_token", refreshTokens.get(username))
        };
    }

    @Test
    void concurrentCRUDTest() throws Exception {
        int numberOfUsers = userTokens.size();
        ExecutorService executor = Executors.newFixedThreadPool(numberOfUsers);
        CountDownLatch latch = new CountDownLatch(numberOfUsers);

        BaseResource[] baseIssues = IssueHelper.getInstance().getAll();
        Issue issue = (Issue) baseIssues[0];

        BaseResource[] baseUsers = UsersHelper.getInstance().getAll();
        Users[] usersArray = new Users[numberOfUsers];
        for (int i = 0; i < numberOfUsers; i++) {
            usersArray[i] = (Users) baseUsers[i % baseUsers.length];
        }

        int index = 0;
        for (String username : userTokens.keySet()) {
            int finalIndex = index;
            executor.submit(() -> {
                try {
                    Users userObj = usersArray[finalIndex];
                    String issueId = issue.getId();
                    String userId = userObj.getId();
                    String uniqueContent = "Comment_" + System.currentTimeMillis() + "_" + finalIndex;

                    // CREATE - Each thread creates its own comment
                    String createRequest = String.format("{\"issue_id\":\"%s\",\"user_id\":\"%s\",\"content\":\"%s\"}",
                            issueId, userId, uniqueContent);
                    String encodedCreate = Base64.getEncoder().encodeToString(createRequest.getBytes());
                    MvcResult createResult = mockMvc.perform(post("/api/comments")
                                    .contentType("application/x-www-form-urlencoded")
                                    .content("resource=" + URLEncoder.encode(encodedCreate, StandardCharsets.UTF_8))
                                    .header("Authorization", "Bearer " + userTokens.get(username))
                                    .cookie(getAuthCookies(username)))
                            .andExpect(status().isOk())
                            .andExpect(content().string(Matchers.containsString("\"message\":\"Success\"")))
                            .andExpect(content().string(Matchers.containsString("\"errCode\":0")))
                            .andReturn();
                    // READ
                    mockMvc.perform(get("/api/comments?queryId=GET_ALL")
                                    .header("Authorization", "Bearer " + userTokens.get(username))
                                    .cookie(getAuthCookies(username)))
                            .andExpect(status().isOk())
                            .andExpect(content().string(Matchers.containsString("\"message\":\"Success\"")))
                            .andExpect(content().string(Matchers.containsString("\"errCode\":0")));

                    // UPDATE
                    BaseResource[] baseComments = CommentsHelper.getInstance().getAll();

                    Comments comment = (Comments) baseComments[0];

                    BaseResource[] users = UsersHelper.getInstance().getAll();
                    Users user = (Users) users[0];
                    String updateRequest = String.format("{\"id\":\"%s\",\"issue_id\":\"%s\",\"user_id\":\"%s\",\"content\":\"Updated Comment #%d\"}",
                            comment.getId(), comment.getIssue_id(), user.getId(), finalIndex);
                    String encodedUpdate = Base64.getEncoder().encodeToString(updateRequest.getBytes());
                    mockMvc.perform(post("/api/comments?action=MODIFY")
                                    .contentType("application/x-www-form-urlencoded")
                                    .content("resource=" + URLEncoder.encode(encodedUpdate, StandardCharsets.UTF_8))
                                    .header("Authorization", "Bearer " + userTokens.get(username))
                                    .cookie(getAuthCookies(username)))
                            .andExpect(status().isOk());

//                    // DELETE
//                    String deleteRequest = String.format("{\"id\":\"%s\"}", comment.getId());
//                    String encodedDelete = Base64.getEncoder().encodeToString(deleteRequest.getBytes());
//                    mockMvc.perform(post("/api/comments?action=DELETE")
//                                    .contentType("application/x-www-form-urlencoded")
//                                    .content("resource=" + URLEncoder.encode(encodedDelete, StandardCharsets.UTF_8))
//                                    .header("Authorization", "Bearer " + userTokens.get(username))
//                                    .cookie(getAuthCookies(username)))
//                            .andExpect(status().isOk())
//                            .andExpect(content().string(Matchers.containsString("\"message\":\"Success\"")))
//                            .andExpect(content().string(Matchers.containsString("\"errCode\":0")));

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
            index++;
        }

        latch.await();
        executor.shutdown();
        System.out.println("✅ Completed concurrent CRUD for 5 users");
    }

}
