package dev.makeev.coworking_service_app.service.implementation;

import dev.makeev.coworking_service_app.dao.LogDAO;
import dev.makeev.coworking_service_app.dto.LogOfUserActionDTO;
import dev.makeev.coworking_service_app.mappers.LogOfUserActionMapper;
import dev.makeev.coworking_service_app.model.LogOfUserAction;
import dev.makeev.coworking_service_app.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogServiceImpl implements LogService {

    private final LogDAO logDAO;
    private final LogOfUserActionMapper logOfUserActionMapper;


    @Autowired
    public LogServiceImpl(LogDAO logDAO, LogOfUserActionMapper logOfUserActionMapper) {
        this.logDAO = logDAO;
        this.logOfUserActionMapper = logOfUserActionMapper;
    }

    @Override
    public void addLog(String login, String message) {
        logDAO.add(new LogOfUserAction(LocalDateTime.now(), login, message));
    }

    @Override
    public List<LogOfUserActionDTO> getLogs() {
        return logDAO.getAll()
                .stream()
                .map(logOfUserActionMapper::toLogOfUserActionDTO)
                .toList();
    }
}
