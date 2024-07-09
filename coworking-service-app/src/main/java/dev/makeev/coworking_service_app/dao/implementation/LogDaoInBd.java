package dev.makeev.coworking_service_app.dao.implementation;

import dev.makeev.coworking_service_app.dao.LogDAO;
import dev.makeev.coworking_service_app.enums.SQLRequest;
import dev.makeev.coworking_service_app.exceptions.DaoException;
import dev.makeev.coworking_service_app.model.LogOfUserAction;
import dev.makeev.coworking_service_app.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LogDaoInBd implements LogDAO {

    private final ConnectionManager connectionManager;

    public LogDaoInBd(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public void add(LogOfUserAction logOfUserAction) {
        try (Connection connection = connectionManager.open();
             PreparedStatement statement = connection.prepareStatement(SQLRequest.ADD_LOG_SQL.getQuery())) {
            statement.setTimestamp(1, Timestamp.valueOf(logOfUserAction.localDateTime()));
            statement.setString(2, logOfUserAction.login());
            statement.setString(3, logOfUserAction.messageAboutAction());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public List<LogOfUserAction> getAll() {
        try (Connection connection = connectionManager.open();
             PreparedStatement statement = connection.prepareStatement(SQLRequest.GET_ALL_LOGS_SQL.getQuery())) {

            return getLogOfUserActions(statement);

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public List<LogOfUserAction> getAllByLogin(String login) {
        try (Connection connection = connectionManager.open();
             PreparedStatement statement = connection.prepareStatement(SQLRequest.GET_ALL_LOGS_FOR_USER_SQL.getQuery())) {

            statement.setString(1, login);
            return getLogOfUserActions(statement);

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private List<LogOfUserAction> getLogOfUserActions(PreparedStatement statement) throws SQLException {
        List<LogOfUserAction> userActions = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            userActions.add(
                    new LogOfUserAction(
                            resultSet.getTimestamp("timestamp").toLocalDateTime(),
                            resultSet.getString("login"),
                            resultSet.getString("action"))
            );
        }
        resultSet.close();
        return userActions;
    }
}
