package com.openclassrooms.etudiant.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret",
            "MonSuperSecretJWTQuiDoitFairePlusDe256BitsDeSecurite1234567890ABCDEF");
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L);
    }

    @Test
    public void test_generate_token() {
        UserDetails user = new User("john", "pass", Collections.emptyList());
        String token = jwtService.generateToken(user);
        assertThat(token).isNotNull().startsWith("eyJ");
    }

    @Test
    public void test_extract_username() {
        UserDetails user = new User("john", "pass", Collections.emptyList());
        String token = jwtService.generateToken(user);
        assertThat(jwtService.extractUsername(token)).isEqualTo("john");
    }

    @Test
    public void test_token_is_valid() {
        UserDetails user = new User("john", "pass", Collections.emptyList());
        String token = jwtService.generateToken(user);
        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }

    @Test
    public void test_token_invalid_for_wrong_user() {
        UserDetails john = new User("john", "pass", Collections.emptyList());
        UserDetails jane = new User("jane", "pass", Collections.emptyList());
        String token = jwtService.generateToken(john);
        assertThat(jwtService.isTokenValid(token, jane)).isFalse();
    }
}
