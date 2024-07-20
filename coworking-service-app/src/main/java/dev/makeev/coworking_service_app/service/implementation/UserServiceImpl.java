package dev.makeev.coworking_service_app.service.implementation;

import dev.makeev.coworking_service_app.dao.UserDAO;
import dev.makeev.coworking_service_app.exceptions.LoginAlreadyExistsException;
import dev.makeev.coworking_service_app.exceptions.VerificationException;
import dev.makeev.coworking_service_app.model.User;
import dev.makeev.coworking_service_app.service.UserService;
import dev.makeev.coworking_service_app.util.TokenUtil;
import dev.makeev.logging_time_starter.advice.annotations.LoggingTime;
import dev.makeev.logging_to_db_starter.advice.annotations.LoggingToDb;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * The {@code UserServiceImpl} class implements the {@link UserService} interface.
 * It provides methods to manage Users.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    /**
     * The UserDAO instance for managing user data.
     */
    private final UserDAO userDAO;

    /**
     * {@inheritdoc}
     */
    @LoggingTime
    @LoggingToDb
    @Override
    public String addUser(String login, String password) throws LoginAlreadyExistsException {
        if (userDAO.getByLogin(login).isPresent()){
            throw new LoginAlreadyExistsException();
        }
        userDAO.add(new User(login, password));
        return TokenUtil.generateToken(login);
    }


    /**
     * {@inheritdoc}
     */
    @LoggingTime
    @LoggingToDb
    @Override
    public String checkCredentials(String login, String password) throws VerificationException {
        Optional<User> user = userDAO.getByLogin(login);
        if (user.isPresent() && user.get().password().equals(password)) {
            return TokenUtil.generateToken(login);
        } else {
            throw new VerificationException();
        }
    }

    /**
     * {@inheritdoc}
     */
    @LoggingTime
    @Override
    public String validateToken(String token) throws VerificationException {
        return TokenUtil.validateToken(token).orElseThrow(VerificationException::new);
    }

    /**
     * {@inheritdoc}
     */
    @LoggingTime
    @LoggingToDb
    @Override
    public void logOut(String login) {
        String token = TokenUtil.validateToken(login).orElseThrow(VerificationException::new);
        TokenUtil.invalidateToken(token);
    }

    /**
     * {@inheritdoc}
     */
    @LoggingTime
    @Override
    public boolean isAdmin(String login){
        return userDAO.getByLogin(login).map(User::isAdmin).orElse(false);
    }
}
