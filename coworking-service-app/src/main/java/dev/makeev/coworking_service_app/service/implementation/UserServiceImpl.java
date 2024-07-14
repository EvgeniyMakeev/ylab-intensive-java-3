package dev.makeev.coworking_service_app.service.implementation;

import dev.makeev.coworking_service_app.advice.annotations.LoggingTime;
import dev.makeev.coworking_service_app.advice.annotations.LoggingToDb;
import dev.makeev.coworking_service_app.dao.UserDAO;
import dev.makeev.coworking_service_app.exceptions.LoginAlreadyExistsException;
import dev.makeev.coworking_service_app.exceptions.VerificationException;
import dev.makeev.coworking_service_app.model.User;
import dev.makeev.coworking_service_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * The {@code UserServiceImpl} class implements the {@link UserService} interface.
 * It provides methods to manage Users.
 */
@Service
public class UserServiceImpl implements UserService {

    /**
     * The UserDAO instance for managing user data.
     */
    private final UserDAO userDAO;

    /**
     * Constructs a {@code UserService} with the specified UserDAO instance.
     *
     * @param userDAO The UserDAO instance to use for data access.
     */
    @Autowired
    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * {@inheritdoc}
     */
    @LoggingTime
    @LoggingToDb
    @Override
    public void addUser(String login, String password) throws LoginAlreadyExistsException {
        if (userDAO.getByLogin(login).isPresent()){
            throw new LoginAlreadyExistsException();
        }
        userDAO.add(new User(login, password));
    }


    /**
     * {@inheritdoc}
     */
    @LoggingTime
    @Override
    public void checkCredentials(String login, String password) throws VerificationException {
        Optional<User> user = userDAO.getByLogin(login);
        if (user.isEmpty() || !user.get().password().equals(password)) {
            throw new VerificationException();
        }
    }

    /**
     * {@inheritdoc}
     */
    @LoggingTime
    @Override
    public boolean isAdmin(String login){
        return userDAO.getByLogin(login).isPresent() ? userDAO.getByLogin(login).get().isAdmin() : false;
    }
}
