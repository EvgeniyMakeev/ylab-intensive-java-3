package dev.makeev.coworking_service_app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.makeev.coworking_service_app.dto.ApiResponse;
import dev.makeev.coworking_service_app.dto.ErrorDetails;
import dev.makeev.coworking_service_app.dto.TokenResponse;
import dev.makeev.coworking_service_app.dto.UserRequestDTO;
import dev.makeev.coworking_service_app.exceptions.LoginAlreadyExistsException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User Controller Integration Test")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerIntegrationTest {

    private static final String LOGIN = "TestUser";
    private static final String PASSWORD = "TestPassword";
    private static final UserRequestDTO userRequestDTO = new UserRequestDTO(LOGIN, PASSWORD);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:16.2");

    @Autowired
    TestRestTemplate restTemplate;

    private String token;

    @BeforeAll
    static void setUpAll() {
        postgresContainer.start();
    }

    @AfterAll
    static void afterAll() {
        postgresContainer.stop();
    }

    @Order(1)
    @Test
    @DisplayName("Should add user")
    void testAddUser_ShouldAddNewUser() throws JsonProcessingException {
        String jsonRequest = objectMapper.writeValueAsString(userRequestDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(jsonRequest, headers);

        ResponseEntity<TokenResponse> response = restTemplate.postForEntity("/api/v1/user/registration", request, TokenResponse.class);
        token = Objects.requireNonNull(response.getBody()).token();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody().message()).contains("User added successfully");
        assertThat(response.getBody().token()).contains(token);
    }

    @Order(2)
    @Test
    @DisplayName("Should return conflict if login already exists")
    void testAddUserLoginAlreadyExists() throws JsonProcessingException {
        String jsonRequest = objectMapper.writeValueAsString(userRequestDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(jsonRequest, headers);

        ResponseEntity<ErrorDetails> response = restTemplate.postForEntity("/api/v1/user/registration", request, ErrorDetails.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(Objects.requireNonNull(response.getBody()).massage()).contains(new LoginAlreadyExistsException().getMessage());
    }

    @Order(3)
    @Test
    @DisplayName("Should log in user")
    void testLogIn_ShouldLogInUser() throws JsonProcessingException {
        String jsonRequest = objectMapper.writeValueAsString(userRequestDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(jsonRequest, headers);

        ResponseEntity<TokenResponse> response = restTemplate.exchange("/api/v1/user/login", HttpMethod.PUT, request, TokenResponse.class);
        token = Objects.requireNonNull(response.getBody()).token();


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody().message()).contains("Login success");
        assertThat(response.getBody().token()).contains(token);
    }

    @Order(4)
    @Test
    @DisplayName("Should log out user")
    void testLogOut_ShouldLogOutUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);

        HttpEntity<String> request = new HttpEntity<>(null, headers);

        ResponseEntity<ApiResponse> response = restTemplate.exchange("/api/v1/user/logout", HttpMethod.PUT, request, ApiResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(Objects.requireNonNull(Objects.requireNonNull(response.getBody()).message())).contains("Successfully logged out.");
    }


}
