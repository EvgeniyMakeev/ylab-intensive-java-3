package dev.makeev.coworking_service_app.dao.implementation;

import dev.makeev.coworking_service_app.dao.LogDAO;
import dev.makeev.coworking_service_app.enums.SQLRequest;
import dev.makeev.coworking_service_app.exceptions.DaoException;
import dev.makeev.coworking_service_app.model.LogOfUserAction;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code LogDaoInBd} class implements the {@link LogDAO} interface.
 * It provides methods to interact with the database to manage Log entities.
 */
@Component
public class LogDaoInBd implements LogDAO {

    private final BasicDataSource dataSource;

    @Autowired
    public LogDaoInBd(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * {@inheritdoc}
     */
    @Override
    public void add(LogOfUserAction logOfUserAction) {
        try (Connection connection = dataSource.getConnection();
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
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQLRequest.GET_ALL_LOGS_SQL.getQuery())) {

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
