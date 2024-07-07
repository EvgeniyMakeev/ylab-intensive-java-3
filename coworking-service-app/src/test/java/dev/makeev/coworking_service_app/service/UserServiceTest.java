package dev.makeev.coworking_service_app.service;

import dev.makeev.coworking_service_app.dao.UserDAO;
import dev.makeev.coworking_service_app.dto.UserRequestDTO;
import dev.makeev.coworking_service_app.exceptions.VerificationException;
import dev.makeev.coworking_service_app.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("UserService Test")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String LOGIN = "TestUser";
    private static final String PASSWORD = "TestPassword";

    @Mock
    private UserRequestDTO testUserDTO;

    @Mock
    private User testUser;

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("UserService test: Add User - Should add new user to DAO")
    void addUser_shouldAddUserToDAO() {
        userService.addUser(LOGIN, PASSWORD);
        when(testUser.login()).thenReturn(LOGIN);
        when(testUser.password()).thenReturn(PASSWORD);

        verify(userDAO, times(1)).add(testUser);
    }

//    @Test
//    @DisplayName("UserService test: Exist By Login - Should check if user exists in DAO")
//    void existByLogin_shouldCheckIfUserExistsInDAO() {
//        when(userDAO.getByLogin(LOGIN)).thenReturn(Optional.of(testUser));
//
//        assertThatThrownBy(() ->
//                userService.existByLogin(LOGIN))
//                .isInstanceOf(LoginAlreadyExistsException.class);
//
//        verify(userDAO, times(1)).getByLogin(eq(LOGIN));
//    }

    @Test
    @DisplayName("UserService test: Check user credentials - Should return the credentials of user is correct")
    void checkCredentials_shouldCheckCredentialsOfUserIsCorrect() {
        when(userDAO.getByLogin(LOGIN)).thenReturn(Optional.of(testUser));
        when(testUser.password()).thenReturn(PASSWORD);

        assertDoesNotThrow(() -> userService.checkCredentials(LOGIN,PASSWORD));
        verify(userDAO, times(1)).getByLogin(eq(LOGIN));
    }

    @Test
    @DisplayName("UserService test: Check user credentials - Should return the credentials of user is incorrect")
    void checkCredentials_shouldReturnCredentialsOfUserIsIncorrect() {
        when(userDAO.getByLogin(LOGIN)).thenReturn(Optional.of(testUser));
        when(testUser.password()).thenReturn(PASSWORD);

        assertThatThrownBy(() ->
                userService.checkCredentials(LOGIN,"Wrong Password"))
                .isInstanceOf(VerificationException.class);

        verify(userDAO, times(1)).getByLogin(eq(LOGIN));
    }

    @Test
    @DisplayName("UserService test: Is Admin - Should return true if user is admin")
    void isAdmin_shouldReturnTrueIfUserIsAdmin() {
        when(userDAO.getByLogin(LOGIN)).thenReturn(Optional.of(testUser));
        when(testUser.isAdmin()).thenReturn(true);

        assertTrue(userService.isAdmin(LOGIN));
    }

    @Test
    @DisplayName("UserService test: Is Admin - Should return false if user is not admin")
    void isAdmin_shouldReturnFalseIfUserIsNotAdmin() {
        when(userDAO.getByLogin(LOGIN)).thenReturn(Optional.of(testUser));
        when(testUser.isAdmin()).thenReturn(false);

        assertFalse(userService.isAdmin(LOGIN));
    }

}