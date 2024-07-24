package dev.makeev.coworking_service_app.service.implementation;

import dev.makeev.coworking_service_app.dao.LogDAO;
import dev.makeev.coworking_service_app.dto.LogOfUserActionDTO;
import dev.makeev.coworking_service_app.mappers.LogOfUserActionMapper;
import dev.makeev.coworking_service_app.model.LogOfUserAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("LogServiceImpl Test")
@ExtendWith(MockitoExtension.class)
public class LogServiceImplTest {

    private static final String LOGIN = "testUser";
    private static final String MESSAGE = "Test log message";

    @Mock
    private LogOfUserAction mockLogOfUserAction;

    @Mock
    private LogDAO logDAO;

    @InjectMocks
    private LogServiceImpl logService;

    @BeforeEach
    void setUp() {
        logService = new LogServiceImpl(logDAO, Mappers.getMapper(LogOfUserActionMapper.class));
    }

    @Test
    @DisplayName("Test adding a log")
    void testAddLog() {
        doNothing().when(logDAO).add(any(LogOfUserAction.class));

        logService.addLog(LOGIN, MESSAGE);

        verify(logDAO, times(1)).add(any(LogOfUserAction.class));
    }

    @Test
    @DisplayName("Test getting all logs")
    void testGetLogs() {
        List<LogOfUserAction> logList = new ArrayList<>();
        logList.add(mockLogOfUserAction);
        when(mockLogOfUserAction.login()).thenReturn(LOGIN);
        when(mockLogOfUserAction.messageAboutAction()).thenReturn(MESSAGE);

        when(logDAO.getAll()).thenReturn(logList);

        List<LogOfUserActionDTO> result = logService.getLogs();

        assertEquals(1, result.size());
        assertEquals(LOGIN, result.get(0).login());
        assertEquals(MESSAGE, result.get(0).messageAboutAction());
    }
}
