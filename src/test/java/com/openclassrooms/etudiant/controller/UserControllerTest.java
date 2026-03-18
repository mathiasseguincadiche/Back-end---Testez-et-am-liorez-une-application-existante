package com.openclassrooms.etudiant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.etudiant.dto.LoginRequestDTO;
import com.openclassrooms.etudiant.dto.RegisterDTO;
import com.openclassrooms.etudiant.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void registerSuccessfully() throws Exception {
        RegisterDTO dto = new RegisterDTO();
        dto.setFirstName("John"); dto.setLastName("Doe");
        dto.setLogin("john"); dto.setPassword("secret123");

        mockMvc.perform(post("/api/register")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
    }

    @Test
    public void loginSuccessfully() throws Exception {
        RegisterDTO reg = new RegisterDTO();
        reg.setFirstName("John"); reg.setLastName("Doe");
        reg.setLogin("john2"); reg.setPassword("secret123");

        mockMvc.perform(post("/api/register")
                .content(objectMapper.writeValueAsString(reg))
                .contentType(MediaType.APPLICATION_JSON));

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setLogin("john2"); dto.setPassword("secret123");

        mockMvc.perform(post("/api/login")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void loginWithWrongPasswordFails() throws Exception {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setLogin("nobody"); dto.setPassword("wrong");

        mockMvc.perform(post("/api/login")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());;
    }
}
