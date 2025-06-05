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
import org.ukdw.managers.GedungManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GedungManagerTest {
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;

    @InjectMocks
    private GedungManager gedungManager;

    @BeforeEach
    void setUp() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    void testAddGedung_Success() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = gedungManager.addGedung("Gedung A", "Jl. Kampus");
        assertTrue(result);
    }

    @Test
    void testDeleteGedung_Success() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = gedungManager.deleteGedung(1);
        assertTrue(result);
    }
}
