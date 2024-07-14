package dev.makeev.coworking_service_app.controller;

import dev.makeev.coworking_service_app.advice.ExceptionControllerAdvice;
import dev.makeev.coworking_service_app.dto.BookingAddDTO;
import dev.makeev.coworking_service_app.dto.BookingDTO;
import dev.makeev.coworking_service_app.exceptions.BookingNotFoundException;
import dev.makeev.coworking_service_app.service.BookingService;
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

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("BookingController Test")
@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    private static final String LOGIN = "TestUser";
    private static final String PASSWORD = "TestPassword";
    private static final String SPACE_NAME = "Test Space";

    @Mock
    private BookingService bookingService;

    @Mock
    private UserService userService;

    @InjectMocks
    private BookingController bookingController;

    @Mock
    private BookingDTO mockBookingDTO;

    @Mock
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    @DisplayName("PUT /api/v1/bookings - Should get bookings for a user")
    void testGetBookings_ValidRequest() throws Exception {
        List<BookingDTO> bookings = List.of(mockBookingDTO);
        when(mockBookingDTO.nameOfBookingSpace()).thenReturn(SPACE_NAME);
        doNothing().when(userService).checkCredentials(LOGIN, PASSWORD);
        when(userService.isAdmin(LOGIN)).thenReturn(false);
        when(bookingService.getAllBookingsForUser(LOGIN)).thenReturn(bookings);

        String jsonRequest = """
                        {
                            "login": "TestUser",
                            "password": "TestPassword"
                        }
                        """;

        mockMvc.perform(put("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(SPACE_NAME)));

        verify(userService, times(1)).checkCredentials(LOGIN, PASSWORD);
        verify(userService, times(1)).isAdmin(LOGIN);
        verify(bookingService, times(1)).getAllBookingsForUser(LOGIN);
    }

    @Test
    @DisplayName("POST /api/v1/bookings - Should add booking if parameters are valid")
    void testAddBooking_ValidRequest() throws Exception {
        doNothing().when(userService).checkCredentials(anyString(), anyString());
        doNothing().when(bookingService).addBooking(anyString(), any(BookingAddDTO.class));

        String jsonRequest = """
            {
                "login": "TestUser",
                "password": "TestPassword",
                "nameOfBookingSpace": "NewSpace2",
                "beginningBookingDate": "2024-07-13",
                "beginningBookingHour": "14",
                "endingBookingDate": "2024-07-14",
                "endingBookingHour": "12"
            }
            """;

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Booking added successfully")));

        verify(userService, times(1)).checkCredentials(anyString(), anyString());
        verify(bookingService, times(1)).addBooking(anyString(), any(BookingAddDTO.class));
    }



    @Test
    @DisplayName("DELETE /api/v1/bookings - Should delete booking if user is admin")
    void testDeleteBooking_ValidRequest_Admin() throws Exception {
        doNothing().when(userService).checkCredentials("TestAdmin", "AdminPass");
        when(userService.isAdmin("TestAdmin")).thenReturn(true);
        doNothing().when(bookingService).deleteBookingById("TestAdmin", 1L);

        String jsonRequest = """
            {
                "login": "TestAdmin",
                "password": "AdminPass",
                "id": 1
            }
            """;

        mockMvc.perform(delete("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).checkCredentials("TestAdmin", "AdminPass");
        verify(bookingService, times(1)).deleteBookingById("TestAdmin", 1L);
    }

    @Test
    @DisplayName("DELETE /api/v1/bookings - Should return not found if booking does not exist for user")
    void testDeleteBooking_NotFound() throws Exception {
        doNothing().when(userService).checkCredentials(LOGIN, PASSWORD);
        when(userService.isAdmin(LOGIN)).thenReturn(false);

        String jsonRequest = """
            {
                "login": "TestUser",
                "password": "TestPassword",
                "id": 999
            }
            """;

        mockMvc.perform(delete("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(new BookingNotFoundException().getMessage())));

        verify(userService, times(1)).checkCredentials(LOGIN, PASSWORD);
        verify(bookingService, never()).deleteBookingById(LOGIN, 999L);
    }
}