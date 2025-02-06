package com.example.security.controller;

import com.example.security.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import com.example.security.TestDataInitializer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestDataInitializer.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private String userToken;
    private String adminToken;

    @BeforeEach
    public void setup() {
        Authentication userAuth = new UsernamePasswordAuthenticationToken("user", null);
        Authentication adminAuth = new UsernamePasswordAuthenticationToken("admin", null);

        userToken = tokenProvider.generateToken(userAuth);
        adminToken = tokenProvider.generateToken(adminAuth);
    }

    @Test
    public void testPublicEndpoint() throws Exception {
        mockMvc.perform(get("/api/test/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("公共内容"));
    }

    @Test
    public void testUserEndpoint() throws Exception {
        mockMvc.perform(get("/api/test/user")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("用户内容"));
    }

    @Test
    public void testAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/test/admin")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("管理员内容"));
    }

    @Test
    public void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/test/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testForbiddenAccess() throws Exception {
        mockMvc.perform(get("/api/test/admin")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }
}