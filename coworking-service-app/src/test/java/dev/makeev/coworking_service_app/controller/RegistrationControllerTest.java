package dev.makeev.coworking_service_app.controller;

import dev.makeev.coworking_service_app.advice.ExceptionControllerAdvice;
import dev.makeev.coworking_service_app.exceptions.LoginAlreadyExistsException;
import dev.makeev.coworking_service_app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("RegistrationController Test")
@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {

    private static final String LOGIN = "TestUser";
    private static final String PASSWORD = "TestPassword";


    @Mock
    private UserService userService;

    @InjectMocks
    private RegistrationController registrationController;

    @Mock
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(registrationController)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/registration - Should add user")
    void testAddUser_ShodAddNewUser() throws Exception {
        doNothing().when(userService).addUser(LOGIN, PASSWORD);
        String jsonRequest = """
                        {
                            "login": "TestUser",
                            "password":"TestPassword"
                        }
                        """;

        mockMvc.perform(post("/api/v1/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Space added successfully")));

        verify(userService, times(1)).addUser(LOGIN, PASSWORD);
    }

    @Test
    @DisplayName("POST /api/v1/registration - Should return conflict if login already exists")
    void testAddUserLoginAlreadyExists() throws Exception {
        doThrow(new LoginAlreadyExistsException()).when(userService).addUser(LOGIN, PASSWORD);

        String jsonRequest = """
                        {
                            "login": "TestUser",
                            "password":"TestPassword"
                        }
                        """;

        mockMvc.perform(post("/api/v1/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(new LoginAlreadyExistsException().getMessage())));

        verify(userService, times(1)).addUser(LOGIN, PASSWORD);
    }
}
