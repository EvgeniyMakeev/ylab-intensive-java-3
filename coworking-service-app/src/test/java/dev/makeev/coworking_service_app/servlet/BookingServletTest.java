package dev.makeev.coworking_service_app.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.makeev.coworking_service_app.dto.BookingAddDTO;
import dev.makeev.coworking_service_app.dto.BookingRequestDTO;
import dev.makeev.coworking_service_app.dto.ApiResponse;
import dev.makeev.coworking_service_app.mappers.BookingMapper;
import dev.makeev.coworking_service_app.model.Booking;
import dev.makeev.coworking_service_app.model.BookingRange;
import dev.makeev.coworking_service_app.service.BookingService;
import dev.makeev.coworking_service_app.service.UserService;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookingServletTest {

    private static final String LOGIN = "TestUser";
    private static final String PASSWORD = "TestPassword";

    private static final BookingAddDTO BOOKING_ADD_DTO =
            new BookingAddDTO(LOGIN, PASSWORD,
                    "Test Space",
                    "2024-07-11", 9,
                    "2024-07-12",17);

    private static final Booking booking = new Booking(LOGIN, "Test Space",
            new BookingRange(LocalDate.of(2024, 7, 11), 9,
                    LocalDate.of(2024, 7, 12), 17));

    @Mock
    private BookingService bookingService;

    @Mock
    private UserService userService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServlet bookingServlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("BookingServlet test: POST - Should add booking if parameters are valid")
    void testDoPost_ValidRequest() throws Exception {
        String jsonRequest = """
                        {
                            "login": "TestUser",
                            "password": "TestPassword",
                            "nameOfBookingSpace": "Test Space",
                            "beginningBookingDate": "2024-07-11",
                            "beginningBookingHour": "9",
                            "endingBookingDate": "2024-07-12",
                            "endingBookingHour": "17"
                        }
                        """;
        InputStream inputStream = new ByteArrayInputStream(jsonRequest.getBytes());
        ServletInputStream servletInputStream = mock(ServletInputStream.class);
        when(request.getInputStream()).thenReturn(servletInputStream);
        when(servletInputStream.read(any(byte[].class))).thenAnswer(invocation -> inputStream.read((byte[]) invocation.getArguments()[0]));

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        when(objectMapper.readValue(any(InputStream.class), eq(BookingAddDTO.class))).thenReturn(BOOKING_ADD_DTO);
        when(bookingMapper.toBooking(BOOKING_ADD_DTO)).thenReturn(booking);

        doNothing().when(userService).checkCredentials(LOGIN, PASSWORD);
        doNothing().when(bookingService).addBooking(LOGIN, booking);

        bookingServlet.doPost(request, response);

        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(objectMapper).writeValue(any(Writer.class), eq(new ApiResponse("Booking added successfully")));
    }

    @Test
    @DisplayName("BookingServlet test: DELETE - Should delete booking if user is admin")
    void testDoDelete_ValidRequest_Admin() throws Exception {
        String jsonRequest = """
                        {
                            "id": "1",
                            "login": "TestAdmin",
                            "password": "AdminPass"
                        }
                        """;
        InputStream inputStream = new ByteArrayInputStream(jsonRequest.getBytes());
        ServletInputStream servletInputStream = mock(ServletInputStream.class);
        when(request.getInputStream()).thenReturn(servletInputStream);
        when(servletInputStream.read(any(byte[].class))).thenAnswer(invocation -> inputStream.read((byte[]) invocation.getArguments()[0]));

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        BookingRequestDTO bookingRequestDTO = new BookingRequestDTO(1L,"TestAdmin", "AdminPass");
        when(objectMapper.readValue(any(InputStream.class), eq(BookingRequestDTO.class))).thenReturn(bookingRequestDTO);

        doNothing().when(userService).checkCredentials("TestAdmin", "AdminPass");
        when(userService.isAdmin("TestAdmin")).thenReturn(true);
        doNothing().when(bookingService).deleteBookingById("TestAdmin", 1);

        bookingServlet.doDelete(request, response);

        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(objectMapper).writeValue(any(Writer.class), eq(new ApiResponse("Booking deleted successfully")));
    }
}
