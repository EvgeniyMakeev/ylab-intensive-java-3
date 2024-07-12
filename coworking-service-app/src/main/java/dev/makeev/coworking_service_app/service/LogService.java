package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.model.LogOfUserAction;

import java.util.List;

public interface LogService {
    void addLog(String login, String message);
    List<LogOfUserAction> getLogs();
}
