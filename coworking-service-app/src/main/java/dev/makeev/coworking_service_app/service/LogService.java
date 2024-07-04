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

    public void addLog(String loginOfUser, String message) {
        logDAO.add(new LogOfUserAction(LocalDateTime.now(), loginOfUser, message));
    }

    public List<LogOfUserAction> getLogs() {
        return logDAO.getAll();
    }

    public List<LogOfUserAction> getLogsOfUser(String loginOfUser) {
        return logDAO.getAllByLogin(loginOfUser);
    }
}
