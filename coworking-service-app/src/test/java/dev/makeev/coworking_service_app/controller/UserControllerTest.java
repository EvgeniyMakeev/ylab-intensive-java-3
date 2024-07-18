package dev.makeev.coworking_service_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.makeev.coworking_service_app.advice.ExceptionControllerAdvice;
import dev.makeev.coworking_service_app.dto.UserRequestDTO;
import dev.makeev.coworking_service_app.exceptions.LoginAlreadyExistsException;
import dev.makeev.coworking_service_app.service.UserService;
import org.junit.jupiter.api.BeforeAll;
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

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("UserController Test")
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private static final String LOGIN = "TestUser";
    private static final String PASSWORD = "TestPassword";
    private static final String TOKEN = "sample_token";
    private static final UserRequestDTO userRequestDTO = new UserRequestDTO(LOGIN, PASSWORD);

    private static ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeAll
    static void beforeAll() {
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    @DisplayName("Should add user")
    void testAddUser_ShouldAddNewUser() throws Exception {
        when(userService.addUser(LOGIN, PASSWORD)).thenReturn(TOKEN);
        String jsonRequest = objectMapper.writeValueAsString(userRequestDTO);

        mockMvc.perform(post("/api/user/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Space added successfully")))
                .andExpect(content().string(containsString(TOKEN)));

        verify(userService, times(1)).addUser(LOGIN, PASSWORD);
    }

    @Test
    @DisplayName("Should return conflict if login already exists")
    void testAddUserLoginAlreadyExists() throws Exception {
        doThrow(new LoginAlreadyExistsException()).when(userService).addUser(LOGIN, PASSWORD);
        String jsonRequest = objectMapper.writeValueAsString(userRequestDTO);

        mockMvc.perform(post("/api/user/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(new LoginAlreadyExistsException().getMessage())));

        verify(userService, times(1)).addUser(LOGIN, PASSWORD);
    }

    @Test
    @DisplayName("Should log in user")
    void testLogIn_ShouldLogInUser() throws Exception {
        when(userService.checkCredentials(LOGIN, PASSWORD)).thenReturn(TOKEN);
        String jsonRequest = objectMapper.writeValueAsString(userRequestDTO);

        mockMvc.perform(put("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Login success")))
                .andExpect(content().string(containsString(TOKEN)));

        verify(userService, times(1)).checkCredentials(LOGIN, PASSWORD);
    }

    @Test
    @DisplayName("Should log out user")
    void testLogOut_ShouldLogOutUser() throws Exception {
        mockMvc.perform(put("/api/user/logout")
                        .requestAttr("login", LOGIN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Successfully logged out.")));

        verify(userService, times(1)).logOut(LOGIN);
    }
}
