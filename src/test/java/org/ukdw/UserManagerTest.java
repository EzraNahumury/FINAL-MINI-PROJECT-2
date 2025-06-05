/**
 * Author: dendy
 * Date:20/02/2025
 * Time:12:01
 * Description:
 */

package org.ukdw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ukdw.data.User;
import org.ukdw.managers.UserManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserManagerTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private UserManager userManager;

    @BeforeEach
    void setUp() throws SQLException {
        userManager = new UserManager();
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    void testAuthenticateUser_Success() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        boolean result = userManager.authenticateUser("testuser", "password123");

        assertTrue(result, "User authentication should succeed");
        verify(preparedStatement, times(1)).setString(1, "testuser");
        verify(preparedStatement, times(1)).setString(2, "password123");
    }

    @Test
    void testAuthenticateUser_Failure() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        boolean result = userManager.authenticateUser("testuser", "wrongpassword");

        assertFalse(result, "User authentication should fail");
    }

    @Test
    void testRegisterUser_Success() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = userManager.registerUser("test@example.com", "testuser", "password123");

        assertTrue(result, "User registration should succeed");
        verify(preparedStatement, times(1)).setString(1, "test@example.com");
        verify(preparedStatement, times(1)).setString(2, "testuser");
        verify(preparedStatement, times(1)).setString(3, "password123");
    }

    @Test
    void testRegisterUser_Failure() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(0);

        boolean result = userManager.registerUser("test@example.com", "testuser", "password123");

        assertFalse(result, "User registration should fail");
    }

    @Test
    void testUpdateProfile_Success() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = userManager.updateProfile("test@example.com", "updatedUser", "newPassword");

        assertTrue(result, "User profile update should succeed");
        verify(preparedStatement, times(1)).setString(1, "updatedUser");
        verify(preparedStatement, times(1)).setString(2, "newPassword");
        verify(preparedStatement, times(1)).setString(3, "test@example.com");
    }

    @Test
    void testUpdateProfile_Failure() throws SQLException {

        when(preparedStatement.executeUpdate()).thenReturn(0);

        boolean result = userManager.updateProfile("test@example.com", "updatedUser", "newPassword");

        assertFalse(result, "User profile update should fail");
    }
}

