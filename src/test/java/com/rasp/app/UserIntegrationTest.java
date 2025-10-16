package com.rasp.app;

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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import platform.resource.session;
import platform.util.ApplicationException;
import platform.webservice.ServletContext;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")

@Import(TestConfig.class)
class UserIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private session httpSession;

    private ServletContext context;

    @BeforeEach
    void setUp() throws ApplicationException {
        MockitoAnnotations.openMocks(this);
        context = new ServletContext(httpSession);


    }

    /**
     * Tests the creation of a new user by sending a POST request to the users endpoint.
     * The test verifies that the creation of a new user is successful.
     */

    @Test
    void testUserCreation_Success() throws Exception {
        // Given
        String randomString = UUID.randomUUID().toString().substring(0, 8);
        String userName = "raspTest_" + randomString;
        String email = "raspTest_" + randomString + "@gmail.com";
        String userJson = String.format("""
            {
                "resourceName": "Users",
                "authMap": {
                    "userName": "%s",
                    "email": "%s",
                    "firstName": "raspTest",
                    "lastName": "K",
                    "password": "raspTest"
                },
                "resourceMap": {
                    "user_name": "%s",
                    "user_email": "%s"
                }
            }""", userName, email, userName, email);

        // When & Then
        mockMvc.perform(post("/api/auth/add_user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk());

    }
}
