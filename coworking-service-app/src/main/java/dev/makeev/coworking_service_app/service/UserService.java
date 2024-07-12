package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.advice.annotations.LoggingTime;
import dev.makeev.coworking_service_app.advice.annotations.LoggingToDb;
import dev.makeev.coworking_service_app.exceptions.LoginAlreadyExistsException;
import dev.makeev.coworking_service_app.exceptions.VerificationException;

public interface UserService {
    @LoggingTime
    @LoggingToDb
    void addUser(String login, String password) throws LoginAlreadyExistsException;

    @LoggingTime
    void checkCredentials(String login, String password) throws VerificationException;

    @LoggingTime
    boolean isAdmin(String login);
}
