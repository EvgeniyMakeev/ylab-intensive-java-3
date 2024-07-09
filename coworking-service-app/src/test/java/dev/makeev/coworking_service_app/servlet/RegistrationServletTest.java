package dev.makeev.coworking_service_app.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.makeev.coworking_service_app.dto.UserRequestDTO;
import dev.makeev.coworking_service_app.exceptions.DaoException;
import dev.makeev.coworking_service_app.exceptions.LoginAlreadyExistsException;
import dev.makeev.coworking_service_app.dto.ApiResponse;
import dev.makeev.coworking_service_app.service.UserService;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RegistrationServletTest {

    private static final String LOGIN = "TestUser";
    private static final String PASSWORD = "TestPassword";


    @Mock
    private UserService userService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RegistrationServlet servlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDoPost_UserAddedSuccessfully() throws IOException, LoginAlreadyExistsException, DaoException {
        String jsonRequest = """
                        {
                            "login": "TestUser",
                            "password":"TestPassword"
                        }
                        """;
        InputStream inputStream = new ByteArrayInputStream(jsonRequest.getBytes());
        ServletInputStream servletInputStream = mock(ServletInputStream.class);
        when(request.getInputStream()).thenReturn(servletInputStream);
        when(servletInputStream.read(any(byte[].class))).thenAnswer(invocation -> inputStream.read((byte[]) invocation.getArguments()[0]));

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        UserRequestDTO userRequestDTO = new UserRequestDTO(LOGIN, PASSWORD);
        when(objectMapper.readValue(any(InputStream.class), eq(UserRequestDTO.class))).thenReturn(userRequestDTO);
        doNothing().when(userService).addUser(LOGIN, PASSWORD);

        servlet.doPost(request, response);

        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(objectMapper).writeValue(any(Writer.class), eq(new ApiResponse("User added successfully")));
    }
}
