package dev.makeev.coworking_service_app.dao.impl;

import dev.makeev.coworking_service_app.dao.UserDAO;
import dev.makeev.coworking_service_app.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * An in-memory implementation of the {@link UserDAO} interface.
 */
public class UserDAOInMemory implements UserDAO {

    private final Map<String, User> mapOfUsers = new HashMap<>();

    /**
     * {@inheritdoc}
     */
    @Override
    public void add(User newUser) {
        mapOfUsers.put(newUser.login(), newUser);
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public Optional<User> getByLogin(String login) {
        return Optional.ofNullable(mapOfUsers.get(login));
    }
}
