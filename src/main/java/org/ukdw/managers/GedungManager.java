package org.ukdw.managers;

import org.ukdw.data.Gedung;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GedungManager {
    private Connection connection;

    public GedungManager(Connection connection) {
        this.connection = connection;
    }

    public boolean addGedung(String nama, String alamat)  {
        String query = "INSERT INTO gedung (nama, alamat) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, nama);
            preparedStatement.setString(2, alamat);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean editGedung(int gedungId, String newNama, String newAlamat) {
        String query = "UPDATE gedung SET nama = ?, alamat = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, newNama);
            preparedStatement.setString(2, newAlamat);
            preparedStatement.setInt(3, gedungId);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteGedung(int gedungId) {
        String query = "DELETE FROM gedung WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, gedungId);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Gedung> getAllGedung() {
        List<Gedung> gedungs = new ArrayList<>();
        String query = "SELECT * FROM gedung";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nama = resultSet.getString("nama");
                String alamat = resultSet.getString("alamat");
                Gedung gedung = new Gedung(id, nama, alamat);
                gedungs.add(gedung);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gedungs;
    }

    public List<Gedung> searchGedungByKeyword(String keyword) {
        List<Gedung> gedungs = new ArrayList<>();
        String query = "SELECT * FROM gedung WHERE nama LIKE ? OR alamat LIKE ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            String likeKeyword = "%" + keyword + "%";
            preparedStatement.setString(1, likeKeyword);
            preparedStatement.setString(2, likeKeyword);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nama = resultSet.getString("nama");
                String alamat = resultSet.getString("alamat");
                gedungs.add(new Gedung(id, nama, alamat));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gedungs;
    }

    // ✅ Tambahan: Cek apakah nama gedung sudah ada
    public boolean isNamaGedungExist(String namaGedung) {
        String sql = "SELECT COUNT(*) FROM gedung WHERE LOWER(nama) = LOWER(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, namaGedung);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Tambahan: Ambil gedung berdasarkan nama
    public Gedung findByNama(String namaGedung) {
        String sql = "SELECT * FROM gedung WHERE LOWER(nama) = LOWER(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, namaGedung);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Gedung(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("alamat")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}