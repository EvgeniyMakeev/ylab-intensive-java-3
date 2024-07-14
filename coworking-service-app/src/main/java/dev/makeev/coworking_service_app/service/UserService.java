package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.advice.annotations.LoggingTime;
import dev.makeev.coworking_service_app.advice.annotations.LoggingToDb;
import dev.makeev.coworking_service_app.exceptions.LoginAlreadyExistsException;
import dev.makeev.coworking_service_app.exceptions.VerificationException;

/**
 * UserService interface for managing users.
 */
public interface UserService {
    @LoggingTime
    @LoggingToDb
    void addUser(String login, String password) throws LoginAlreadyExistsException;

    /**
     * Verifies the credentials of a user.
     *
     * @param login    The login to verify.
     * @param password The password to verify.
     */
    @LoggingTime
    void checkCredentials(String login, String password) throws VerificationException;

    /**
     * Checks if a user is an admin.
     *
     * @param login The login of the user.
     * @return {@code true} if the user is an admin, {@code false} otherwise.
     */
    @LoggingTime
    boolean isAdmin(String login);
}
