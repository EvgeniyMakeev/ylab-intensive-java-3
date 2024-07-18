package dev.makeev.coworking_service_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.makeev.coworking_service_app.advice.ExceptionControllerAdvice;
import dev.makeev.coworking_service_app.dto.BookingAddDTO;
import dev.makeev.coworking_service_app.dto.BookingDTO;
import dev.makeev.coworking_service_app.exceptions.BookingNotFoundException;
import dev.makeev.coworking_service_app.service.BookingService;
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

import java.util.List;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("BookingController Test")
@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    private static final String LOGIN = "TestUser";
    private static final String SPACE_NAME = "Test Space";

    private static ObjectMapper objectMapper;

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

    @BeforeAll
    static void beforeAll() {
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    @DisplayName("Should get bookings for a user")
    void testGetBookings_ValidRequest() throws Exception {
        List<BookingDTO> bookings = List.of(mockBookingDTO);
        when(mockBookingDTO.nameOfBookingSpace()).thenReturn(SPACE_NAME);
        when(userService.isAdmin(LOGIN)).thenReturn(false);
        when(bookingService.getAllBookingsForUser(LOGIN)).thenReturn(bookings);

        mockMvc.perform(get("/api/v1/bookings")
                        .requestAttr("login", LOGIN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(SPACE_NAME)));

        verify(userService, times(1)).isAdmin(LOGIN);
        verify(bookingService, times(1)).getAllBookingsForUser(LOGIN);
    }

    @Test
    @DisplayName("Should add booking if parameters are valid")
    void testAddBooking_ValidRequest() throws Exception {
        doNothing().when(bookingService).addBooking(anyString(), any(BookingAddDTO.class));

        String jsonRequest = objectMapper
                .writeValueAsString(new BookingAddDTO("NewSpace2",
                        "2024-07-13",14,
                        "2024-07-14", 12));

        mockMvc.perform(post("/api/v1/bookings")
                        .requestAttr("login", LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Booking added successfully")));

        verify(bookingService, times(1)).addBooking(anyString(), any(BookingAddDTO.class));
    }



    @Test
    @DisplayName("Should delete booking if user is admin")
    void testDeleteBooking_ValidRequest_Admin() throws Exception {
        when(userService.isAdmin("TestAdmin")).thenReturn(true);

        mockMvc.perform(delete("/api/v1/bookings/1")
                        .requestAttr("login", "TestAdmin"))
                .andExpect(status().isNoContent());

        verify(bookingService, times(1)).deleteBookingByIdByAdmin("TestAdmin", 1L);
    }

    @Test
    @DisplayName("Should return not found if booking does not exist for user")
    void testDeleteBooking_NotFound() throws Exception {
        when(userService.isAdmin(LOGIN)).thenReturn(false);
        doThrow(new BookingNotFoundException()).when(bookingService).deleteBookingById(LOGIN,999L);

        mockMvc.perform(delete("/api/v1/bookings/999")
                        .requestAttr("login", LOGIN))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(new BookingNotFoundException().getMessage())));

        verify(bookingService, times(1)).deleteBookingById(LOGIN, 999L);
    }
}