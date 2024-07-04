package dev.makeev.coworking_service_app.dao;

import dev.makeev.coworking_service_app.model.LogOfUserAction;

import java.util.List;

/**
 * The {@code LogDAO} interface provides methods for managing the persistence
 * of Log entities. It allows adding, retrieving, and querying logs of users action.
 */
public interface LogDAO {

    void add(LogOfUserAction logOfUserAction);

    List<LogOfUserAction> getAll();

    List<LogOfUserAction> getAllByLogin(String login);
}
