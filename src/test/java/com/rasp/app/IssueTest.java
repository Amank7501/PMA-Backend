
package com.rasp.app;

import com.rasp.app.helper.IssueHelper;
import com.rasp.app.helper.ListResHelper;
import com.rasp.app.helper.ProjectHelper;
import com.rasp.app.helper.UsersHelper;
import com.rasp.app.resource.Issue;
import com.rasp.app.resource.ListRes;
import com.rasp.app.resource.Project;
import com.rasp.app.resource.Users;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import platform.resource.BaseResource;
import platform.resource.session;
import platform.util.ApplicationException;
import platform.webservice.ServletContext;

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
public class IssueTest {


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
    void createIssue() throws Exception {


        BaseResource[] baseResources=  ProjectHelper.getInstance().getAll();
        BaseResource[] users= UsersHelper.getInstance().getAll();
        BaseResource[] lists=  ListResHelper.getInstance().getAll();
        Project project=null;
        Users user=null;
        ListRes listRes=null;
        for (BaseResource br:users) {
            user = (Users) br;
            break;
        }
        for (BaseResource br:lists) {
            listRes = (ListRes) br;
            break;
        }
        for (BaseResource br:baseResources) {
            project = (Project) br;
            break;
        }



        String name="label1"+ UUID.randomUUID().toString().substring(0, 2);
        String project_id=project.getId();
        String owner_id=user.getId();
        String list_id=listRes.getId();
        String projectRequestBody = String.format(
                "{\"owner_id\":\"%s\",\"project_id\":\"%s\",\"issue_title\":\"first_issue\"," +
                        "\"description\":\"first_issue\",\"attachment\":\"fewrew\"," +
                        "\"priority\":\"high\",\"status\":\"pending\",\"list_id\":\"%s\"}",
                owner_id,project_id,list_id
        );
        String encodedResource = Base64.getEncoder().encodeToString(projectRequestBody.getBytes());
        mockMvc.perform(post("/api/issue")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("resource=" + URLEncoder.encode(encodedResource, StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + accessToken)
                        .cookie(new Cookie("refresh_token", refreshToken))
                ).andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("\"message\":\"Success\"")))
                .andExpect(content().string(Matchers.containsString("\"errCode\":0")));

    }

    @Test
    void createIssue_neg() throws Exception {


        BaseResource[] baseResources=  ProjectHelper.getInstance().getAll();
        BaseResource[] users= UsersHelper.getInstance().getAll();
        BaseResource[] lists=  ListResHelper.getInstance().getAll();
        Project project=null;
        Users user=null;
        ListRes listRes=null;
        for (BaseResource br:users) {
            user = (Users) br;
            break;
        }
        for (BaseResource br:lists) {
            listRes = (ListRes) br;
            break;
        }
        for (BaseResource br:baseResources) {
            project = (Project) br;
            break;
        }



        String name="label1"+ UUID.randomUUID().toString().substring(0, 2);
        String project_id=project.getId();
        String owner_id="11111111111111111111";
        String list_id=listRes.getId();
        String projectRequestBody = String.format(
                "{\"owner_id\":\"%s\",\"project_id\":\"%s\",\"issue_title\":\"first_issue\"," +
                        "\"description\":\"first_issue\",\"attachment\":\"fewrew\"," +
                        "\"priority\":\"high\",\"status\":\"pending\",\"list_id\":\"%s\"}",
                owner_id,project_id,list_id
        );
        String encodedResource = Base64.getEncoder().encodeToString(projectRequestBody.getBytes());
        mockMvc.perform(post("/api/issue")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("resource=" + URLEncoder.encode(encodedResource, StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + accessToken)
                        .cookie(new Cookie("refresh_token", refreshToken))
                ).andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("\"message\":\"Foreign Key violation : owner_id\"")))
                .andExpect(content().string(Matchers.containsString("\"errCode\":-1")));

    }

    @Test
    void fetchProject() throws Exception {

        mockMvc.perform(get("/api/issue?queryId=GET_ALL")

                        .header("Authorization", "Bearer " + accessToken)
                        .cookie(new Cookie("refresh_token", refreshToken))
                ).andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("\"message\":\"Success\"")))
                .andExpect(content().string(Matchers.containsString("\"errCode\":0")));

    }

    @Test
    void updateIssue() throws Exception {

        BaseResource[] issues=  IssueHelper.getInstance().getAll();

        Issue issue=null;
        ListRes listRes=null;
        for (BaseResource br:issues) {
            issue = (Issue) br;
            break;
        }





        String project_id=issue.getProject_id();
        String owner_id=issue.getOwner_id();
        String list_id=issue.getList_id();
        String id=issue.getId();
        String projectRequestBody = String.format(
                "{\"id\":\"%s\",\"owner_id\":\"%s\",\"project_id\":\"%s\",\"issue_title\":\"first_issue\"," +
                        "\"description\":\"first_issue\",\"attachment\":\"fewrew\"," +
                        "\"priority\":\"high\",\"status\":\"pending\",\"list_id\":\"%s\"}",
               id, owner_id,project_id,list_id
        );
        String encodedResource = Base64.getEncoder().encodeToString(projectRequestBody.getBytes());
        mockMvc.perform(post("/api/issue?action=MODIFY")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("resource=" + URLEncoder.encode(encodedResource, StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + accessToken)
                        .cookie(new Cookie("refresh_token", refreshToken))
                ).andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    void updateIssue_neg() throws Exception {

        BaseResource[] issues=  IssueHelper.getInstance().getAll();

        Issue issue=null;
        ListRes listRes=null;
        for (BaseResource br:issues) {
            issue = (Issue) br;
            break;
        }





        String project_id="11111111111111111";
        String owner_id="111111111111111111";
        String list_id=issue.getList_id();
        String id=issue.getId();
        String projectRequestBody = String.format(
                "{\"id\":\"%s\",\"owner_id\":\"%s\",\"project_id\":\"%s\",\"issue_title\":\"first_issue\"," +
                        "\"description\":\"first_issue\",\"attachment\":\"fewrew\"," +
                        "\"priority\":\"high\",\"status\":\"pending\",\"list_id\":\"%s\"}",
                id, owner_id,project_id,list_id
        );
        String encodedResource = Base64.getEncoder().encodeToString(projectRequestBody.getBytes());
        mockMvc.perform(post("/api/issue?action=MODIFY")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("resource=" + URLEncoder.encode(encodedResource, StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + accessToken)
                        .cookie(new Cookie("refresh_token", refreshToken))
                ). andExpect(content().string(Matchers.containsString("\"message\":\"Foreign Key violation : owner_id\"")))
                .andExpect(content().string(Matchers.containsString("\"errCode\":-1")));
    }


//    @Test
//    void deleteIssue() throws Exception {
//        String loginRequestBody = "{\"username\":\"aman\",\"password\":\"aman\"}";
//
//        MvcResult mvcResult= mockMvc.perform(post("/api/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(loginRequestBody)
//                ).andExpect(status().isOk())
//                .andExpect(cookie().exists("access_token"))
//                .andExpect(cookie().exists("refresh_token"))
//                .andDo(print())
//                .andReturn();
//
//        String accessToken = mvcResult.getResponse().getCookie("access_token").getValue();
//        String refreshToken = mvcResult.getResponse().getCookie("refresh_token").getValue();
//
//        BaseResource[] baseResources= IssueHelper.getInstance().getAll();
//        Issue issue=null;
//        for(BaseResource br:baseResources) {
//            issue = (Issue) br;
//            break;
//        }
//
//        String id=issue.getId();
//        String requestBody = String.format(
//                "{\"id\":\"%s\"}",
//                id
//        );
//        String encodedResource = Base64.getEncoder().encodeToString(requestBody.getBytes());
//        mockMvc.perform(post("/api/issue?action=DELETE")
//                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                        .content("resource=" + URLEncoder.encode(encodedResource, StandardCharsets.UTF_8))
//                        .header("Authorization", "Bearer " + accessToken)
//                        .cookie(new Cookie("refresh_token", refreshToken))
//                ).andExpect(status().isOk())
//                .andExpect(content().string(Matchers.containsString("\"message\":\"Success\"")))
//                .andExpect(content().string(Matchers.containsString("\"errCode\":0")));
//
//    }



}
