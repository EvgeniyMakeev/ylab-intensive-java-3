package dev.makeev.coworking_service_app.dao;

import dev.makeev.coworking_service_app.model.User;

import java.util.Optional;

/**
 * The {@code UserDAO} interface provides methods for managing the persistence
 * of User entities. It allows adding, retrieving, and querying User entities by their login.
 */
public interface UserDAO {

    void add(User newUser);

    /**
     * Retrieves a User entity by its login.
     *
     * @param login The login of the User entity to retrieve.
     * @return An {@code Optional} containing the User entity if found,
     *         or empty if not found.
     */
    Optional<User> getByLogin(String login);
}
