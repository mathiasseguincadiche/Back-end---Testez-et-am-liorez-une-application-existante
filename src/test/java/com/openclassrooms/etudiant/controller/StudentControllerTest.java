package com.openclassrooms.etudiant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.etudiant.dto.CreateStudentDTO;
import com.openclassrooms.etudiant.dto.LoginRequestDTO;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.entities.User;
import com.openclassrooms.etudiant.repository.StudentRepository;
import com.openclassrooms.etudiant.repository.UserRepository;
import com.openclassrooms.etudiant.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class StudentControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private StudentRepository studentRepository;

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        User user = new User();
        user.setFirstName("Test"); user.setLastName("User");
        user.setLogin("testuser"); user.setPassword("testpass");
        userService.register(user);

        LoginRequestDTO loginDTO = new LoginRequestDTO();
        loginDTO.setLogin("testuser"); loginDTO.setPassword("testpass");

        MvcResult result = mockMvc.perform(post("/api/login")
                .content(objectMapper.writeValueAsString(loginDTO))
                .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
        jwtToken = result.getResponse().getContentAsString();
    }

    @AfterEach
    void tearDown() {
        studentRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void createStudentSuccessfully() throws Exception {
        CreateStudentDTO dto = new CreateStudentDTO();
        dto.setFirstName("Alice"); dto.setLastName("Martin");
        dto.setEmail("alice@test.com");

        mockMvc.perform(post("/api/students")
                .header("Authorization", "Bearer " + jwtToken)
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    public void listStudentsSuccessfully() throws Exception {
        Student s = new Student();
        s.setFirstName("Alice"); s.setLastName("M"); s.setEmail("a@t.com");
        studentRepository.save(s);

        mockMvc.perform(get("/api/students")
                .header("Authorization", "Bearer " + jwtToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].firstName").value("Alice"));
    }

    @Test
    public void accessDeniedWithoutToken() throws Exception {
        mockMvc.perform(get("/api/students"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getStudentByIdSuccessfully() throws Exception {
        Student s = new Student();
        s.setFirstName("Bob"); s.setLastName("D"); s.setEmail("b@t.com");
        s = studentRepository.save(s);

        mockMvc.perform(get("/api/students/" + s.getId())
                .header("Authorization", "Bearer " + jwtToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Bob"));
    }

    @Test
    public void updateStudentSuccessfully() throws Exception {
        Student s = new Student();
        s.setFirstName("Alice"); s.setLastName("M"); s.setEmail("a@t.com");
        s = studentRepository.save(s);

        CreateStudentDTO dto = new CreateStudentDTO();
        dto.setFirstName("Updated"); dto.setLastName("M"); dto.setEmail("a@t.com");

        mockMvc.perform(put("/api/students/" + s.getId())
                .header("Authorization", "Bearer " + jwtToken)
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Updated"));
    }

    @Test
    public void deleteStudentSuccessfully() throws Exception {
        Student s = new Student();
        s.setFirstName("Del"); s.setLastName("S"); s.setEmail("d@t.com");
        s = studentRepository.save(s);

        mockMvc.perform(delete("/api/students/" + s.getId())
                .header("Authorization", "Bearer " + jwtToken))
            .andExpect(status().isNoContent());
    }
}
