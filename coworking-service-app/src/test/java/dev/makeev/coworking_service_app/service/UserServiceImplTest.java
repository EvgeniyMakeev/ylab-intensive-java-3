package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.dao.UserDAO;
import dev.makeev.coworking_service_app.exceptions.VerificationException;
import dev.makeev.coworking_service_app.model.User;
import dev.makeev.coworking_service_app.service.implementation.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("UserService Test")
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final String LOGIN = "TestUser";
    private static final String PASSWORD = "TestPassword";
    private static final User testUser = new User(LOGIN, PASSWORD);

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    @DisplayName("UserService test: Add User - Should add new user to DAO")
    void addUser_shouldAddUserToDAO() {
        userServiceImpl.addUser(LOGIN, PASSWORD);

        verify(userDAO, times(1)).add(new User(LOGIN, PASSWORD));
    }

    @Test
    @DisplayName("UserService test: Check user credentials - Should return the credentials of user is correct")
    void checkCredentials_shouldCheckCredentialsOfUserIsCorrect() {
        when(userDAO.getByLogin(LOGIN)).thenReturn(Optional.of(testUser));

        assertDoesNotThrow(() -> userServiceImpl.checkCredentials(LOGIN,PASSWORD));
        verify(userDAO, times(1)).getByLogin(eq(LOGIN));
    }

    @Test
    @DisplayName("UserService test: Check user credentials - Should return the credentials of user is incorrect")
    void checkCredentials_shouldReturnCredentialsOfUserIsIncorrect() {
        when(userDAO.getByLogin(LOGIN)).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() ->
                userServiceImpl.checkCredentials(LOGIN,"Wrong Password"))
                .isInstanceOf(VerificationException.class);

        verify(userDAO, times(1)).getByLogin(eq(LOGIN));
    }

    @Test
    @DisplayName("UserService test: Is Admin - Should return true if user is admin")
    void isAdmin_shouldReturnTrueIfUserIsAdmin() {
        when(userDAO.getByLogin(LOGIN)).thenReturn(Optional.of(new User(LOGIN, PASSWORD, true)));

        assertTrue(userServiceImpl.isAdmin(LOGIN));
    }

    @Test
    @DisplayName("UserService test: Is Admin - Should return false if user is not admin")
    void isAdmin_shouldReturnFalseIfUserIsNotAdmin() {
        when(userDAO.getByLogin(LOGIN)).thenReturn(Optional.of(testUser));

        assertFalse(userServiceImpl.isAdmin(LOGIN));
    }

}