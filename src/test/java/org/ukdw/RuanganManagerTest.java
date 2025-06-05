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
import org.ukdw.managers.RuanganManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RuanganManagerTest {
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;

    @InjectMocks
    private RuanganManager ruanganManager;

    @BeforeEach
    void setUp() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    void testAddRuangan_Success() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = ruanganManager.addRuangan("Ruangan 101", 1);
        assertTrue(result);
    }

    @Test
    void testDeleteRuangan_Success() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = ruanganManager.deleteRuangan(1);
        assertTrue(result);
    }
}
