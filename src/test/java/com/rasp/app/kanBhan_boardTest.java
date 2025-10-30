
package com.rasp.app;

import com.rasp.app.helper.KanbanBoardHelper;
import com.rasp.app.helper.ProjectHelper;
import com.rasp.app.resource.KanbanBoard;
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
import org.springframework.web.context.WebApplicationContext;
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
public class kanBhan_boardTest {


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
    void createkanBhan_board() throws Exception {

        BaseResource[] baseResources= ProjectHelper.getInstance().getAll();
        Project project=null;
        for(BaseResource br:baseResources) {
            project = (Project) br;

        }
        String project_id= project.getId();
        String projectRequestBody = String.format(
                "{\"project_id\":\"%s\"}",project_id

        );
        String encodedResource = Base64.getEncoder().encodeToString(projectRequestBody.getBytes());
        mockMvc.perform(post("/api/kanban_board")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("resource=" + URLEncoder.encode(encodedResource, StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + accessToken)
                        .cookie(new Cookie("refresh_token", refreshToken))
                ).andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void fetchkanban_board() throws Exception {




        mockMvc.perform(get("/api/kanban_board?queryId=GET_ALL")

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

        BaseResource[] baseResources= KanbanBoardHelper.getInstance().getAll();
        KanbanBoard kanbanBoard=null;
        for(BaseResource br:baseResources) {
            kanbanBoard = (KanbanBoard) br;
            break;
        }


        String id=kanbanBoard.getId();
        String project_id=kanbanBoard.getProject_id();

        String projectRequestBody = String.format(
                "{\"id\":\"%s\", \"project_id\":\"%s\"}",id,project_id

        );
        String encodedResource = Base64.getEncoder().encodeToString(projectRequestBody.getBytes());
        mockMvc.perform(post("/api/kanban_board?action=MODIFY")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("resource=" + URLEncoder.encode(encodedResource, StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + accessToken)
                        .cookie(new Cookie("refresh_token", refreshToken))
                ).andExpect(status().isOk())
                .andDo(print());

    }


    @Test
    void deleteProject() throws Exception {

        BaseResource[] baseResources= KanbanBoardHelper.getInstance().getAll();
        KanbanBoard kanbanBoard=null;
        for(BaseResource br:baseResources) {
            kanbanBoard = (KanbanBoard) br;
            break;
        }


        String id=kanbanBoard.getId();
        String projectRequestBody = String.format(
                "{\"id\":\"%s\"}",
                id
        );
        String encodedResource = Base64.getEncoder().encodeToString(projectRequestBody.getBytes());
        mockMvc.perform(post("/api/kanban_board?action=DELETE")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("resource=" + URLEncoder.encode(encodedResource, StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + accessToken)
                        .cookie(new Cookie("refresh_token", refreshToken))
                ).andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("\"message\":\"Success\"")))
                .andExpect(content().string(Matchers.containsString("\"errCode\":0")));

    }



}
