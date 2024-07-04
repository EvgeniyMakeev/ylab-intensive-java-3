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
import static org.mockito.Mockito.when;

@DisplayName("UserService Test")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String LOGIN = "TestUser";
    private static final String PASSWORD = "TestPassword";
    private static final User testUser = new User(LOGIN, PASSWORD, false);
    private static final User testAmin = new User(LOGIN, PASSWORD, true);

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserService userService;

//    @Test
//    @DisplayName("UserService test: Add User - Should add new user to DAO")
//    void addUser_shouldAddUserToDAO() {
//        userService.addUser(LOGIN, PASSWORD);
//
//        verify(userDAO, times(1)).add(eq(testUser));
//    }

//    @Test
//    @DisplayName("UserService test: Exist By Login - Should check if user exists in DAO")
//    void existByLogin_shouldCheckIfUserExistsInDAO() {
//        when(userDAO.getByLogin(LOGIN)).thenReturn(Optional.of(testUser));
//
//        assertTrue(userService.existByLogin(LOGIN));
//        verify(userDAO, times(1)).getByLogin(eq(LOGIN));
//    }
//
//    @Test
//    @DisplayName("UserService test: Check user credentials - Should return the credentials of user is correct")
//    void checkCredentials_shouldCheckCredentialsOfUserIsCorrect() {
//        when(userDAO.getByLogin(LOGIN)).thenReturn(Optional.of(testUser));
//
//        assertTrue(userService.checkCredentials(LOGIN,PASSWORD));
//        verify(userDAO, times(1)).getByLogin(eq(LOGIN));
//    }
//
//    @Test
//    @DisplayName("UserService test: Check user credentials - Should return the credentials of user is incorrect")
//    void checkCredentials_shouldReturnCredentialsOfUserIsIncorrect() {
//        when(userDAO.getByLogin(LOGIN)).thenReturn(Optional.of(testUser));
//
//        assertFalse(userService.checkCredentials(LOGIN,"Wrong Password"));
//        verify(userDAO, times(1)).getByLogin(eq(LOGIN));
//    }

    @Test
    @DisplayName("UserService test: Is Admin - Should return true if user is admin")
    void isAdmin_shouldReturnTrueIfUserIsAdmin() {
        when(userDAO.getByLogin(LOGIN)).thenReturn(Optional.of(testAmin));

        assertTrue(userService.isAdmin(LOGIN));
    }

    @Test
    @DisplayName("UserService test: Is Admin - Should return false if user is not admin")
    void isAdmin_shouldReturnFalseIfUserIsNotAdmin() {
        when(userDAO.getByLogin(LOGIN)).thenReturn(Optional.of(testUser));

        assertFalse(userService.isAdmin(LOGIN));
    }

}