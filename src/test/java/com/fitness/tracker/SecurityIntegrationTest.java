package com.fitness.tracker;

import com.fitness.tracker.model.JwtRequest;
import com.fitness.tracker.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SecurityIntegrationTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void testLoginAndAccessProtectedEndpoint() {
        RestTemplate restTemplate = new RestTemplate();

        // Login request
        JwtRequest jwtRequest = new JwtRequest();
        jwtRequest.setUsername("testuser");
        jwtRequest.setPassword("password123");

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                "http://localhost:8080/api/auth/login",
                jwtRequest,
                String.class
        );

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        String jwtToken = loginResponse.getBody();

        // Access protected endpoint
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);

        ResponseEntity<String> protectedResponse = restTemplate.getForEntity(
                "http://localhost:8080/api/exercise-records/user/1",
                String.class,
                headers
        );

        assertThat(protectedResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
