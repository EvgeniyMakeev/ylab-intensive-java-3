package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.dao.UserDAO;
import dev.makeev.coworking_service_app.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("UserService Test")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String LOGIN = "TestUser";
    private static final String PASSWORD = "TestPassword";

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Add User - Should add new user to DAO")
    void addUser_shouldAddUserToDAO() {
        userService.addUser(LOGIN, PASSWORD);

        verify(userDAO, times(1)).add(eq(new User(LOGIN, PASSWORD, false)));
    }

    @Test
    @DisplayName("Exist By Login - Should check if user exists in DAO")
    void existByLogin_shouldCheckIfUserExistsInDAO() {
        when(userDAO.getByLogin(LOGIN)).thenReturn(Optional.of(new User(LOGIN, PASSWORD,false)));

        boolean exists = userService.existByLogin(LOGIN);

        assertTrue(exists);
        verify(userDAO, times(1)).getByLogin(eq(LOGIN));
    }

    @Test
    @DisplayName("Is Admin - Should return true if user is admin")
    void isAdmin_shouldReturnTrueIfUserIsAdmin() {
        when(userDAO.getByLogin(LOGIN)).thenReturn(Optional.of(new User(LOGIN, PASSWORD, true)));

        boolean result = userService.isAdmin(LOGIN);

        assertTrue(result);
    }

    @Test
    @DisplayName("Is Admin - Should return false if user is not admin")
    void isAdmin_shouldReturnFalseIfUserIsNotAdmin() {
        when(userDAO.getByLogin(LOGIN)).thenReturn(Optional.of(new User(LOGIN, PASSWORD, false)));

        boolean result = userService.isAdmin(LOGIN);

        assertFalse(result);
    }

}