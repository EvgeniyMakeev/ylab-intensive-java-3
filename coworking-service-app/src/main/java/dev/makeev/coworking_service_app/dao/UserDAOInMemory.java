package dev.makeev.coworking_service_app.dao;

import dev.makeev.coworking_service_app.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserDAOInMemory implements UserDAO {

    private final Map<String, User> mapOfUsers = new HashMap<>();

    @Override
    public void add(User newUser) {
        mapOfUsers.put(newUser.login(), newUser);
    }

    @Override
    public Optional<User> getByLogin(String login) {
        return Optional.ofNullable(mapOfUsers.get(login));
    }
}
