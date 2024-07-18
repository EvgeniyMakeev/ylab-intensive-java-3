package dev.makeev.coworking_service_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.makeev.coworking_service_app.advice.ExceptionControllerAdvice;
import dev.makeev.coworking_service_app.dto.SpaceAddDTO;
import dev.makeev.coworking_service_app.dto.SpaceDTO;
import dev.makeev.coworking_service_app.dto.SpaceDeleteDTO;
import dev.makeev.coworking_service_app.exceptions.NoAdminException;
import dev.makeev.coworking_service_app.service.SpaceService;
import dev.makeev.coworking_service_app.service.UserService;
import org.hamcrest.CoreMatchers;
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

import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("SpaceController Test")
@ExtendWith(MockitoExtension.class)
class SpaceControllerTest {

    private static final String ADMIN_LOGIN = "TestAdmin";
    private static final String SPACE_NAME = "Test Space";
    private static final SpaceAddDTO SPACE_ADD_DTO = new SpaceAddDTO(SPACE_NAME, 8, 18, 10);

    private static ObjectMapper objectMapper;

    @Mock
    private SpaceService spaceService;

    @Mock
    private UserService userService;

    @InjectMocks
    private SpaceController spaceController;

    @Mock
    private MockMvc mockMvc;

    @Mock
    private SpaceDTO mockSpaceDTO;

    @BeforeAll
    static void beforeAll() {
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(spaceController)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    @DisplayName("Should return list of spaces")
    void testGetSpaces() throws Exception {
        List<SpaceDTO> spaces = Collections.singletonList(mockSpaceDTO);
        when(spaceService.getSpaces()).thenReturn(spaces);
        when(mockSpaceDTO.name()).thenReturn(SPACE_NAME);

        mockMvc.perform(get("/api/v1/spaces"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(SPACE_NAME)));

        verify(spaceService, times(1)).getSpaces();
    }

    @Test
    @DisplayName("Should add space if parameters are valid and user is admin")
    void testAddSpace_ValidRequest_Admin() throws Exception {
        when(userService.isAdmin(ADMIN_LOGIN)).thenReturn(true);
        doNothing().when(spaceService).addSpace(SPACE_ADD_DTO);

        String jsonRequest = objectMapper.writeValueAsString(SPACE_ADD_DTO);

        mockMvc.perform(post("/api/v1/spaces")
                        .requestAttr("login", ADMIN_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Space added successfully")));

        verify(userService, times(1)).isAdmin(ADMIN_LOGIN);
        verify(spaceService, times(1)).addSpace(SPACE_ADD_DTO);
    }

    @Test
    @DisplayName("Should throw NoAdminException if user is not admin")
    void testAddSpace_NoAdmin() throws Exception {
        when(userService.isAdmin("NotAdmin")).thenReturn(false);

        String jsonRequest = objectMapper.writeValueAsString(SPACE_ADD_DTO);

        mockMvc.perform(post("/api/v1/spaces")
                        .requestAttr("login", "NotAdmin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(new NoAdminException().getMessage())));

        verify(userService, times(1)).isAdmin("NotAdmin");
        verify(spaceService, never()).addSpace(SPACE_ADD_DTO);
    }

    @Test
    @DisplayName("Should delete space if user is admin")
    void testDeleteSpace_ValidRequest_Admin() throws Exception {
        when(userService.isAdmin(ADMIN_LOGIN)).thenReturn(true);
        doNothing().when(spaceService).deleteSpace("Test Space");

        String jsonRequest = objectMapper.writeValueAsString(new SpaceDeleteDTO("Test Space"));

        mockMvc.perform(delete("/api/v1/spaces")
                        .requestAttr("login", ADMIN_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNoContent())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Space deleted successfully")));

        verify(userService, times(1)).isAdmin(ADMIN_LOGIN);
        verify(spaceService, times(1)).deleteSpace(SPACE_NAME);
    }

    @Test
    @DisplayName("Should throw NoAdminException if user is not admin")
    void testDeleteSpace_NoAdmin() throws Exception {
        when(userService.isAdmin("NotAdmin")).thenReturn(false);

        String jsonRequest = objectMapper.writeValueAsString(new SpaceDeleteDTO("Test Space"));

        mockMvc.perform(delete("/api/v1/spaces")
                        .requestAttr("login", "NotAdmin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(CoreMatchers.containsString(new NoAdminException().getMessage())));

        verify(userService, times(1)).isAdmin("NotAdmin");
        verify(spaceService, never()).deleteSpace(SPACE_NAME);
    }
}
