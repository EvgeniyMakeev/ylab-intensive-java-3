package dev.makeev.coworking_service_app.model;

/**
 * Represents a user with login credentials and administrative status.
 *
 * @param login     the login name of the user
 * @param password  the password of the user
 * @param isAdmin   whether the user has administrative privileges
 */
public record User(String login,
                   String password,
                   Boolean isAdmin) {
    public User(String login, String password) {
        this(login, password, false);
    }
}
