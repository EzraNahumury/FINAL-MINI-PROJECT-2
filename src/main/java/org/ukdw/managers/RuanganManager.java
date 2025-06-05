package org.ukdw.managers;

import org.ukdw.data.Gedung;
import org.ukdw.data.Ruangan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RuanganManager {
    private Connection connection;

    public RuanganManager(Connection connection) {
        this.connection = connection;
    }

    public boolean adaDiGedung(String namaRuangan, int idGedung) throws SQLException {
        String query = "Select Count(*) FROM ruangan where nama = ? AND gedung_id = ?";
        try (PreparedStatement psmt = connection.prepareStatement(query)){
            psmt.setString(1,namaRuangan);
            psmt.setInt(2,idGedung);
            ResultSet rs = psmt.executeQuery();
            if (rs.next()){
                return rs.getInt(1)>0;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    // Add methods for course management (create, edit, delete)
    public boolean addRuangan(String nama, int idGedung) {
        String query = "INSERT INTO ruangan (nama, gedung_id) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, nama);
            preparedStatement.setInt(2, idGedung);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // Course added successfully if rows were inserted
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database query error
            return false;
        }
    }

    public boolean editRuangan(String newNama, int idGedung, int idRuangan) {
        String query = "UPDATE ruangan SET nama = ?, gedung_id = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, newNama);
            preparedStatement.setInt(2, idGedung);
            preparedStatement.setInt(3, idRuangan);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // Course edited successfully if rows were affected
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database query error
            return false;
        }
    }

    public boolean deleteRuangan(int idRuangan) {
        String query = "DELETE FROM ruangan WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, idRuangan);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // Course deleted successfully if rows were affected
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database query error
            return false;
        }
    }

    public List<Ruangan> getAllRuangan() {
        List<Ruangan> ruangans = new ArrayList<>();
        String query = "SELECT * FROM ruangan";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nama = resultSet.getString("nama");
                int idGedung = resultSet.getInt("gedung_id");
                Ruangan ruangan = new Ruangan(id, nama, idGedung);
                ruangans.add(ruangan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database query error
        }
        return ruangans;
    }

    public List<Ruangan> searchRuanganByKeyword(String keyword) {
        List<Ruangan> gedungs = new ArrayList<>();
        String query = "SELECT * FROM ruangan WHERE nama LIKE ? OR gedung_id LIKE ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            String likeKeyword = "%" + keyword + "%";
            preparedStatement.setString(1, likeKeyword);
            preparedStatement.setString(2, likeKeyword);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nama = resultSet.getString("nama");
                int alamat = resultSet.getInt("gedung_id");
                gedungs.add(new Ruangan(id, nama, alamat));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gedungs;
    }
}