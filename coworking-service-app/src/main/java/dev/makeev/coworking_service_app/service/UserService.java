package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.dao.UserDAO;
import dev.makeev.coworking_service_app.model.User;

import java.util.Optional;

/**
 * The {@code UserService} class provides methods to manage user-related operations.
 * It encapsulates the business logic related to user management, such as adding users,
 * verifying credentials, and checking user roles.
 */
public class UserService {

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

    public void init() {
        userDAO.add(new User("admin", "1234", true));
        userDAO.add(new User("User1", "pass1", false));
        userDAO.add(new User("User2", "pass2", false));
    }

    /**
     * Adds a new user with the specified login and password.
     *
     * @param login    The login of the user.
     * @param password The password of the user.
     */
    public void addUser(String login, String password) {
        userDAO.add(new User(login, password, false));
    }

    /**
     * Checks if a user with the specified login exists.
     *
     * @param login The login to check.
     * @return {@code true} if the user exists, {@code false} otherwise.
     */
    public boolean existByLogin(String login) {
        return userDAO.getByLogin(login).isPresent();
    }

    /**
     * Verifies the credentials of a user.
     *
     * @param login    The login to verify.
     * @param password The password to verify.
     */
    public boolean checkCredentials(String login, String password) {
        Optional<User> user = userDAO.getByLogin(login);
        return user.isPresent() && user.get().password().equals(password);
    }

    /**
     * Checks if a user is an admin.
     *
     * @param login The login of the user.
     * @return {@code true} if the user is an admin, {@code false} otherwise.
     */
    public boolean isAdmin(String login){
        return userDAO.getByLogin(login).get().isAdmin();
    }
}
