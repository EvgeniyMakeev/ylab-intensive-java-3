package dev.makeev.coworking_service_app.dao.implementation;

import dev.makeev.coworking_service_app.aop.annotations.LoggingTime;
import dev.makeev.coworking_service_app.dao.UserDAO;
import dev.makeev.coworking_service_app.enums.SQLRequest;
import dev.makeev.coworking_service_app.exceptions.DaoException;
import dev.makeev.coworking_service_app.model.User;
import dev.makeev.coworking_service_app.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * The {@code UserDAOInBd} class implements the {@link UserDAO} interface.
 * It provides methods to interact with the database to manage User entities.
 */
public final class UserDAOInBd implements UserDAO {

    private final ConnectionManager connectionManager;

    public UserDAOInBd(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * {@inheritdoc}
     */
    @LoggingTime
    @Override
    public void add(User newUser) {
        try (Connection connection = connectionManager.open();
             PreparedStatement statement = connection.prepareStatement(SQLRequest.ADD_USER_SQL.getQuery())) {
            statement.setString(1, newUser.login());
            statement.setString(2, newUser.password());
            statement.setBoolean(3, false);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    /**
     * {@inheritdoc}
     */
    @LoggingTime
    @Override
    public Optional<User> getByLogin(String login) {
        try (Connection connection = connectionManager.open();
             PreparedStatement statement = connection.prepareStatement(SQLRequest.GET_USER_BY_LOGIN_SQL.getQuery())) {
            statement.setString(1, login);
            User user = null;
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = new User(login,
                        resultSet.getString("password"),
                        resultSet.getBoolean("admin"));
            }
            return Optional.ofNullable(user);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}