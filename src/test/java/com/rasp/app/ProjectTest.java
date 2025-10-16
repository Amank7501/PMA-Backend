package com.rasp.app;

import com.rasp.app.helper.ProjectHelper;
import com.rasp.app.resource.Project;
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
import platform.resource.BaseResource;
import platform.resource.session;
import platform.util.ApplicationException;
import platform.webservice.ServletContext;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

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
    void createProject() throws Exception {
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
        String userId="01e6d944-ee20-440e-9d04-d1b0a00fdad5-18";
         String projectRequestBody = String.format(
                 "{\"name\":\"New Project1\",\"description\":\"Project Description\",\"user_id\":\"%s\"}",
                 userId
         );
        String encodedResource = Base64.getEncoder().encodeToString(projectRequestBody.getBytes());
       mockMvc.perform(post("/api/project")
                       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                       .content("resource=" + URLEncoder.encode(encodedResource, StandardCharsets.UTF_8))
               .header("Authorization", "Bearer " + accessToken)
                       .cookie(new Cookie("refresh_token", refreshToken))
       ).andExpect(status().isOk())
               .andDo(print());
    }

    @Test
    void fetchProject() throws Exception {
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


        mockMvc.perform(get("/api/project?queryId=GET_ALL")

                        .header("Authorization", "Bearer " + accessToken)
                        .cookie(new Cookie("refresh_token", refreshToken))
                ).andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("\"message\":\"Success\"")))
                .andExpect(content().string(Matchers.containsString("\"errCode\":0")));

    }

    @Test
    void updateProject() throws Exception {
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
    void deleteProject() throws Exception {
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



}
