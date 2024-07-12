package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.advice.annotations.LoggingTime;
import dev.makeev.coworking_service_app.advice.annotations.LoggingToDb;
import dev.makeev.coworking_service_app.dao.UserDAO;
import dev.makeev.coworking_service_app.exceptions.LoginAlreadyExistsException;
import dev.makeev.coworking_service_app.exceptions.VerificationException;
import dev.makeev.coworking_service_app.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * The {@code UserService} class provides methods to manage user-related operations.
 * It encapsulates the business logic related to user management, such as adding users,
 * verifying credentials, and checking user roles.
 */
@Service
public final class UserService {

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
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }


    @LoggingTime
    @LoggingToDb
    public void addUser(String login, String password) throws LoginAlreadyExistsException {
        if (userDAO.getByLogin(login).isPresent()){
            throw new LoginAlreadyExistsException();
        }
        userDAO.add(new User(login, password));
    }


    /**
     * Verifies the credentials of a user.
     *
     * @param login    The login to verify.
     * @param password The password to verify.
     */
    @LoggingTime
    public void checkCredentials(String login, String password) throws VerificationException {
        Optional<User> user = userDAO.getByLogin(login);
        if (user.isEmpty() || !user.get().password().equals(password)) {
            throw new VerificationException();
        }
    }

    /**
     * Checks if a user is an admin.
     *
     * @param login The login of the user.
     * @return {@code true} if the user is an admin, {@code false} otherwise.
     */
    @LoggingTime
    public boolean isAdmin(String login){
        return userDAO.getByLogin(login).isPresent() ? userDAO.getByLogin(login).get().isAdmin() : false;
    }
}
