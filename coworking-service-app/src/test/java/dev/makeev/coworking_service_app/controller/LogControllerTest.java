package dev.makeev.coworking_service_app.controller;

import dev.makeev.coworking_service_app.advice.ExceptionControllerAdvice;
import dev.makeev.coworking_service_app.dto.LogOfUserActionDTO;
import dev.makeev.coworking_service_app.exceptions.NoAdminException;
import dev.makeev.coworking_service_app.mappers.LogOfUserActionMapper;
import dev.makeev.coworking_service_app.service.LogService;
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

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("LogController Test")
@ExtendWith(MockitoExtension.class)
class LogControllerTest {

    private static final String ADMIN_LOGIN = "TestAdmin";
    private static final String LOGIN = "TestUser";
    private static final String ACTION = "TestAction";
    private static final String DATE = "2024-07-14 18:00:00";
    private static final LogOfUserActionDTO LOG_OF_USER_ACTION_DTO = new LogOfUserActionDTO(DATE, LOGIN, ACTION);

    @Mock
    private LogService logService;

    @Mock
    private UserService userService;

    @Mock
    private LogOfUserActionMapper logOfUserActionMapper;

    @InjectMocks
    private LogController logController;

    @Mock
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(logController)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    @DisplayName("Should get log if user is admin")
    void testGetLog_AdminUser() throws Exception {
        when(userService.isAdmin(ADMIN_LOGIN)).thenReturn(true);
        when(logService.getLogs()).thenReturn(List.of(LOG_OF_USER_ACTION_DTO));

        mockMvc.perform(get("/api/v1/log")
                        .requestAttr("login", ADMIN_LOGIN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(DATE)))
                .andExpect(content().string(containsString(LOGIN)))
                .andExpect(content().string(containsString(ACTION)));

        verify(userService, times(1)).isAdmin(ADMIN_LOGIN);
        verify(logService, times(1)).getLogs();
    }

    @Test
    @DisplayName("Should throw NoAdminException if user is not admin")
    void testGetLog_NonAdminUser() throws Exception {
        when(userService.isAdmin(LOGIN)).thenReturn(false);

        mockMvc.perform(get("/api/v1/log")
                        .requestAttr("login", LOGIN))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(new NoAdminException().getMessage())));

        verify(userService, times(1)).isAdmin(LOGIN);
        verify(logService, never()).getLogs();
        verify(logOfUserActionMapper, never()).toLogOfUserActionDTO(any());
    }

}
