/**
 * Author: dendy
 * Date:20/02/2025
 * Time:12:13
 * Description:
 */

package org.ukdw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ukdw.managers.PemesananManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PemesananManagerTest {
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;

    @InjectMocks
    private PemesananManager pemesananManager;

    @BeforeEach
    void setUp() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    void testAddPemesanan_Success() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = pemesananManager.addPemesanan("user@example.com", 1, "2024-06-01", "2024-06-02", "08:00", "17:00");
        assertTrue(result);
    }

    @Test
    void testDeletePemesanan_Success() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = pemesananManager.deletePemesanan(1);
        assertTrue(result);
    }
}
