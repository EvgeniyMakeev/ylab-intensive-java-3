package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.aop.annotations.LoggingTime;
import dev.makeev.coworking_service_app.aop.annotations.LoggingToDb;
import dev.makeev.coworking_service_app.dao.UserDAO;
import dev.makeev.coworking_service_app.dto.UserRequestDTO;
import dev.makeev.coworking_service_app.exceptions.LoginAlreadyExistsException;
import dev.makeev.coworking_service_app.exceptions.VerificationException;
import dev.makeev.coworking_service_app.model.User;

import java.util.Optional;

/**
 * The {@code UserService} class provides methods to manage user-related operations.
 * It encapsulates the business logic related to user management, such as adding users,
 * verifying credentials, and checking user roles.
 */
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
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }


    @LoggingTime
    @LoggingToDb
    public void addUser(UserRequestDTO userRequestDTO) {
        userDAO.add(new User(userRequestDTO.login(), userRequestDTO.password(), false));
    }

    /**
     * Checks if a user with the specified login exists.
     *
     * @param login The login to check.
     */
    public void existByLogin(String login) throws LoginAlreadyExistsException {
        if (userDAO.getByLogin(login).isPresent()){
            throw new LoginAlreadyExistsException();
        }
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
