package dev.makeev.coworking_service_app.dao.implementation;

import dev.makeev.coworking_service_app.advice.annotations.LoggingTime;
import dev.makeev.coworking_service_app.dao.UserDAO;
import dev.makeev.coworking_service_app.enums.SQLRequest;
import dev.makeev.coworking_service_app.exceptions.DaoException;
import dev.makeev.coworking_service_app.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * The {@code UserDAOInBd} class implements the {@link UserDAO} interface.
 * It provides methods to interact with the database to manage User entities.
 */
@Component
@RequiredArgsConstructor
public class UserDAOInBd implements UserDAO {

    private final DataSource dataSource;

    /**
     * {@inheritdoc}
     */
    @LoggingTime
    @Override
    public void add(User newUser) {
        try (Connection connection = dataSource.getConnection();
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
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQLRequest.GET_USER_BY_LOGIN_SQL.getQuery())) {
            statement.setString(1, login);
            try (ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()) {
                    User user = new User(login,
                            resultSet.getString("password"),
                            resultSet.getBoolean("admin"));
                    return Optional.of(user);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}