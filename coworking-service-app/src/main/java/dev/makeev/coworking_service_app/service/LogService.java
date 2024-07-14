package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.dto.LogOfUserActionDTO;

import java.util.List;

public interface LogService {
    void addLog(String login, String message);
    List<LogOfUserActionDTO> getLogs();
}
