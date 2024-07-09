package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.dao.LogDAO;
import dev.makeev.coworking_service_app.model.LogOfUserAction;

import java.time.LocalDateTime;
import java.util.List;


public final class LogService {

    private final LogDAO logDAO;

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
