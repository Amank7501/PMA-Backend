package com.rasp.app;

import com.rasp.app.helper.*;
import com.rasp.app.resource.*;
import com.rasp.app.service.IamService;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.Cookie;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import platform.resource.BaseResource;
import platform.resource.session;
import platform.webservice.ServletContext;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
//@Transactional
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@Import(TestConfig.class)
public class ProjectCollabirationTest {
    private String rashmiToken;
    private String adiToken;

    private String projectId;
    private String  ownerRefreshToken ;
    private String teamMemberRefreshToken ;

    private static Map<String, Object> ownerLoginTokens = new HashMap<>();
    private static Map<String, Object> teamMemberLoginTokens = new HashMap<>();
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private session httpSession;


    private static ServletContext context;
    private List<String> taskIds = new ArrayList<>();

    static {
        Dotenv dotenv = Dotenv.configure().load();
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );
    }



    @Autowired
    IamService iamService;
//    @BeforeAll
//   static void setup() throws Exception {
//
//    }
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        context = new ServletContext(httpSession);
        // Login Rashmi
        ownerLoginTokens = loginUser("aman", "aman");
        rashmiToken= ownerLoginTokens.get("access_token").toString();
        ownerRefreshToken= ownerLoginTokens.get("refresh_token").toString();

        teamMemberLoginTokens = loginUser("shashank", "aman");
        adiToken= teamMemberLoginTokens.get("access_token").toString();
        teamMemberRefreshToken= teamMemberLoginTokens.get("refresh_token").toString();
        Registry.register();
    }

    void logout( String refreshToken) throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                .cookie(new Cookie("refresh_token", refreshToken)))
                .andExpect(status().isOk());
    }
    void createProject(String accessToken, String refreshToken) throws Exception {
        BaseResource[] baseResources1= UsersHelper.getInstance().getAll();
        Users users=null;
        for(BaseResource br:baseResources1) {
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
                .cookie(new Cookie("refresh_token", refreshToken)));
    }

    void createTask(String accessToken, String refreshToken) throws Exception {
        BaseResource[] baseResources=  ProjectHelper.getInstance().getAll();
        BaseResource[] users1= UsersHelper.getInstance().getAll();
        BaseResource[] lists=  ListResHelper.getInstance().getAll();
        Project project=null;
        Users user=null;
        ListRes listRes=null;
        for (BaseResource br:users1) {
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



        String name1="label1"+ UUID.randomUUID().toString().substring(0, 2);
        String project_id=project.getId();
        String owner_id="11111111111111111111";
        String list_id=listRes.getId();
        String projectRequestBody1 = String.format(
                "{\"owner_id\":\"%s\",\"project_id\":\"%s\",\"issue_title\":\"first_issue\"," +
                        "\"description\":\"first_issue\",\"attachment\":\"fewrew\"," +
                        "\"priority\":\"high\",\"status\":\"pending\",\"list_id\":\"%s\"}",
                owner_id,project_id,list_id
        );
        String encodedResource1 = Base64.getEncoder().encodeToString(projectRequestBody1.getBytes());
        mockMvc.perform(post("/api/issue")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("resource=" + URLEncoder.encode(encodedResource1, StandardCharsets.UTF_8))
                .header("Authorization", "Bearer " + accessToken)
                .cookie(new Cookie("refresh_token", refreshToken)));
        //).andExpect(status().isOk())
    }

    void getAllTask(String accessTocken,String refreshTocken) throws Exception {
        mockMvc.perform(get("/api/issue?queryId=GET_ALL")

                        .header("Authorization", "Bearer " + accessTocken)
                        .cookie(new Cookie("refresh_token", refreshTocken))
                ).andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("\"message\":\"Success\"")))
                .andExpect(content().string(Matchers.containsString("\"errCode\":0")));
    }

    void updateTaskStatus(String accessTocken,String refreshTocken) throws Exception {
        BaseResource[] issues=  IssueHelper.getInstance().getAll();

        Issue issue=null;
        ListRes listRes1=null;
        for (BaseResource br:issues) {
            issue = (Issue) br;
            break;
        }





        String project_id1=issue.getProject_id();
        String owner_id1=issue.getOwner_id();
        String list_id1=issue.getList_id();
        String id=issue.getId();
        String projectRequestBody11 = String.format(
                "{\"id\":\"%s\",\"owner_id\":\"%s\",\"project_id\":\"%s\",\"issue_title\":\"first_issue\"," +
                        "\"description\":\"first_issue\",\"attachment\":\"fewrew\"," +
                        "\"priority\":\"high\",\"status\":\"pending\",\"list_id\":\"%s\"}",
                id, owner_id1,project_id1,list_id1
        );

        String encodedResource11 = Base64.getEncoder().encodeToString(projectRequestBody11.getBytes());
        mockMvc.perform(post("/api/issue?action=MODIFY")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("resource=" + URLEncoder.encode(encodedResource11, StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + accessTocken)
                        .cookie(new Cookie("refresh_token", refreshTocken))
                ).andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(1)
    void testProjectCreationAndCollaborationFlow() throws Exception {
        // 1. Rashmi creates a project
        createProject(rashmiToken, ownerRefreshToken);

        // 2. Rashmi creates tasks
       createTask(rashmiToken,ownerRefreshToken);

        // 3. Rashmi adds Adi as collaborator
        Map<String, Object> userRoleMapping = new HashMap<>();
        userRoleMapping.put("username", "shashank");
        userRoleMapping.put("role", "team_member");
        iamService.addUserRoleMapping(userRoleMapping);
         RoleResourcePermission roleResourcePermission = new RoleResourcePermission();
        roleResourcePermission.setRole("team_member");
        roleResourcePermission.setResource("project");
        roleResourcePermission.setUser_name("shashank");
        RoleResourcePermissionHelper.getInstance().add(roleResourcePermission);


        // 4. Rashmi assigns  tasks to Adi
        Map<String, Object> userRoleMapping1 = new HashMap<>();
        userRoleMapping.put("username", "shashank");
        userRoleMapping.put("role", "OWNER");
        iamService.addUserRoleMapping(userRoleMapping);
        RoleResourcePermission roleResourcePermission1 = new RoleResourcePermission();
        roleResourcePermission1.setRole("team_member");
        roleResourcePermission1.setResource("issue");
        roleResourcePermission1.setUser_name("shashank");
        RoleResourcePermissionHelper.getInstance().add(roleResourcePermission1);

        // 5. Rashmi logs out (handled by token expiration in real scenario)
        logout(ownerRefreshToken);
        // 6. Adi logs in and gets tasks
        getAllTask(adiToken,teamMemberRefreshToken);


        // 7. Adi updates task status
        updateTaskStatus(adiToken,teamMemberRefreshToken);

        // 8. Adi logs out
        logout(teamMemberRefreshToken);
        // 9. Rashmi logs back in and verifies updates
        // Login Rashmi
        ownerLoginTokens = loginUser("aman", "aman");
        rashmiToken= ownerLoginTokens.get("access_token").toString();
        ownerRefreshToken= ownerLoginTokens.get("refresh_token").toString();

        getAllTask(rashmiToken,ownerRefreshToken);


    }


    private Map<String, Object> loginUser(String username, String password) throws Exception {

        String loginRequestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, "aman");
        MvcResult mvcResult = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(loginRequestBody))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().exists("refresh_token"))
                .andReturn();

       String accessToken = mvcResult.getResponse().getCookie("access_token").getValue();
       String refreshToken = mvcResult.getResponse().getCookie("refresh_token").getValue();
       Map<String, Object> tokens = new HashMap<>();

         tokens.put("access_token", accessToken);
         tokens.put("refresh_token", refreshToken);
       return tokens;
    }

}
