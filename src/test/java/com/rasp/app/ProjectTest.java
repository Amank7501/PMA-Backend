package com.rasp.app;

import com.rasp.app.helper.ProjectHelper;
import com.rasp.app.helper.UsersHelper;
import com.rasp.app.resource.Project;
import com.rasp.app.resource.Users;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.Cookie;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
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
public class ProjectTest {


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

    @BeforeAll
    static void beforeAll() throws ApplicationException {
        Registry.register();
        System.out.println("Registry completed");
    }


    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        context = new ServletContext(httpSession);

//        Registry.register();
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
    void createProject() throws Exception {
        BaseResource[] baseResources= UsersHelper.getInstance().getAll();
        Users users=null;
        for(BaseResource br:baseResources) {
            users = (Users) br;
            break;
        }
        String userId= users.getId();
        String name="project"+ UUID.randomUUID().toString().substring(0, 2);
        String projectRequestBody = String.format(
                "{\"name\":\"%s\",\"description\":\"Project Description\",\"user_id\":\"%s\"}",name,userId

        );
        String encodedResource = Base64.getEncoder().encodeToString(projectRequestBody.getBytes());
        mockMvc.perform(post("/api/project")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("resource=" + URLEncoder.encode(encodedResource, StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + accessToken)
                        .cookie(new Cookie("refresh_token", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("\"message\":\"Success\"")))
                .andExpect(content().string(Matchers.containsString("\"errCode\":0")))
                .andDo(print());
    }
    @Test
    void createProjectNegative() throws Exception {
        BaseResource[] baseResources= UsersHelper.getInstance().getAll();
        Users users=null;
        for(BaseResource br:baseResources) {
            users = (Users) br;
            break;
        }
        String userId= users.getId();
       Project project=new Project();
       project.setName("name");
       project.setDescription("Project Description");
       project.setUser_id(userId);
       ProjectHelper.getInstance().add(project);
        String projectRequestBody = String.format(
                "{\"name\":\"name\",\"description\":\"Project Description\",\"user_id\":\"%s\"}",userId

        );

        String encodedResource = Base64.getEncoder().encodeToString(projectRequestBody.getBytes());
        mockMvc.perform(post("/api/project")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("resource=" + URLEncoder.encode(encodedResource, StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + accessToken)
                        .cookie(new Cookie("refresh_token", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.matchesPattern("(?s).*\"errCode\":-1.*\"message\"\s*:\s*\"Duplicate Name\\s+[a-f0-9-]+\".*")))
                .andDo(print());
    }

    @Test
    void fetchProject() throws Exception {
        mockMvc.perform(get("/api/project?queryId=GET_ALL")
                        .header("Authorization", "Bearer " + accessToken)
                        .cookie(new Cookie("refresh_token", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("\"message\":\"Success\"")))
                .andExpect(content().string(Matchers.containsString("\"errCode\":0")))
                .andDo(print());
    }
    @Test
    void fetchProjectNegative() throws Exception {
        mockMvc.perform(get("/api/project?queryId=GET_AL")
                        .header("Authorization", "Bearer " + accessToken)
                        .cookie(new Cookie("refresh_token", refreshToken)))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    void updateProject() throws Exception {

       BaseResource[] baseResources= ProjectHelper.getInstance().getAll();
       Project project=null;
       for(BaseResource br:baseResources) {
            project = (Project) br;
            break;
       }
        String userId= project.getUser_id();

        String id=project.getId();
        String name=project.getName();
        String description=project.getDescription();
        String projectRequestBody = String.format(
                "{\"id\":\"%s\", \"name\":\"new name\",\"description\":\"%s\"}",
               id,name,description,userId
        );
        String encodedResource = Base64.getEncoder().encodeToString(projectRequestBody.getBytes());
        mockMvc.perform(post("/api/project?action=MODIFY")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("resource=" + URLEncoder.encode(encodedResource, StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + accessToken)
                        .cookie(new Cookie("refresh_token", refreshToken))
                ).andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("\"message\":\"Success\"")))
                .andExpect(content().string(Matchers.containsString("\"errCode\":0")));

    }

    @Test
    void updateProjectNegative() throws Exception {

        BaseResource[] baseResources= ProjectHelper.getInstance().getAll();
        Project project=null;
        for(BaseResource br:baseResources) {
            project = (Project) br;
            break;
        }


        String id=project.getId();
        String name=project.getName();

        String description=project.getDescription();
        String projectRequestBody = String.format(
                "{\"id\":\"1111111111111111111\", \"name\":\"new name\",\"description\":\"%s\"}",
                id,name,description
        );
        String encodedResource = Base64.getEncoder().encodeToString(projectRequestBody.getBytes());
        mockMvc.perform(post("/api/project?action=MODIFY")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("resource=" + URLEncoder.encode(encodedResource, StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + accessToken)
                        .cookie(new Cookie("refresh_token", refreshToken))
                ).andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("\"message\":\"Entity doesn't exist\"")))
                .andExpect(content().string(Matchers.containsString("\"errCode\":-1")));

    }




    @Test
    void deleteProject() throws Exception {

        BaseResource[] baseResources= ProjectHelper.getInstance().getAll();
        Project project=null;
        for(BaseResource br:baseResources) {
            project = (Project) br;
            break;
        }

        String id=project.getId();
        String projectRequestBody = String.format(
                "{\"id\":\"%s\"}",
                id
        );
        String encodedResource = Base64.getEncoder().encodeToString(projectRequestBody.getBytes());
        mockMvc.perform(post("/api/project?action=DELETE")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("resource=" + URLEncoder.encode(encodedResource, StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + accessToken)
                        .cookie(new Cookie("refresh_token", refreshToken))
                ).andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("\"message\":\"Success\"")))
                .andExpect(content().string(Matchers.containsString("\"errCode\":0")));

    }

    @Test
    void deleteProjectNegative() throws Exception {

        BaseResource[] baseResources= ProjectHelper.getInstance().getAll();

        String id="11111111111111111111";
        String projectRequestBody = String.format(
                "{\"id\":\"%s\"}",
                id
        );
        String encodedResource = Base64.getEncoder().encodeToString(projectRequestBody.getBytes());
        mockMvc.perform(post("/api/project?action=DELETE")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("resource=" + URLEncoder.encode(encodedResource, StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + accessToken)
                        .cookie(new Cookie("refresh_token", refreshToken))
                ).andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("\"message\":\"Entity doesn't exists\"")))
                .andExpect(content().string(Matchers.containsString("\"errCode\":-1")));

    }



}
