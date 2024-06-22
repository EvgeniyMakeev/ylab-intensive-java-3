package dev.makeev.coworking_service_app.dao;

import dev.makeev.coworking_service_app.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserDAOInMemory Test")
class UserDAOInMemoryTest {

    private static final String LOGIN = "TestLogin";
    private static final String PASSWORD = "TestPassword";

    private UserDAO userDAO;
    private User testUser;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAOInMemory();
        testUser = new User(LOGIN, PASSWORD, false);
    }

    @Test
    @DisplayName("UserDAOInMemory test: Add User - Should add new user")
    void add_shouldAddUser() {
        userDAO.add(testUser);

        assertThat(userDAO.getByLogin(LOGIN)).isPresent();
        assertThat(userDAO.getByLogin(LOGIN).get().login()).isEqualTo(LOGIN);
        assertThat(userDAO.getByLogin(LOGIN).get().password()).isEqualTo(PASSWORD);
    }

    @Test
    @DisplayName("UserDAOInMemory test: Get User by login - Success")
    void getBy_shouldGetUser_whenExists() {
        userDAO.add(testUser);
        Optional<User> user = userDAO.getByLogin(LOGIN);

        assertThat(user).isPresent();
        assertThat(user.get().password()).isEqualTo(PASSWORD);
    }

    @Test
    @DisplayName("UserDAOInMemory test: Get By Login - Should return empty optional if user does not exist")
    void getBy_shouldReturnEmptyOptionalIfUserDoesNotExist() {
        userDAO.add(testUser);
        String WRONG_LOGIN = "WrongTestLogin";
        Optional<User> result = userDAO.getByLogin(WRONG_LOGIN);

        assertThat(result).isEmpty();
    }
}