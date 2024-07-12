package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.dao.LogDAO;
import dev.makeev.coworking_service_app.model.LogOfUserAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public final class LogService {

    private final LogDAO logDAO;

    @Autowired
    public LogService(LogDAO logDAO) {
        this.logDAO = logDAO;
    }

    public void addLog(String login, String message) {
        logDAO.add(new LogOfUserAction(LocalDateTime.now(), login, message));
    }

    public List<LogOfUserAction> getLogs() {
        return logDAO.getAll();
    }
}
