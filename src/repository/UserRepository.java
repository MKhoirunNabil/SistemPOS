/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import config.DatabaseConfig;
import model.Admin;
import model.Kasir;
import model.Pengguna;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author MKN
 */
public class UserRepository implements Dao<Pengguna> {
    @Override
    public boolean simpan(Pengguna data) throws Exception {
        String sql = "INSERT INTO user (username, password, nama, role) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, data.getUsername());
            stmt.setString(2, data.getPassword());
            stmt.setString(3, data.getNama());
            stmt.setString(4, data.getRole());
            
            int affected = stmt.executeUpdate();
            return affected > 0;
            
        } catch (SQLException e) {
            throw new Exception("Gagal menyimpan user: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean update(Pengguna data) throws Exception {
        String sql = "UPDATE user SET username=?, password=?, nama=?, role=? WHERE id_user=?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, data.getUsername());
            stmt.setString(2, data.getPassword());
            stmt.setString(3, data.getNama());
            stmt.setString(4, data.getRole());
            stmt.setInt(5, data.getIdUser());
            
            int affected = stmt.executeUpdate();
            return affected > 0;
            
        } catch (SQLException e) {
            throw new Exception("Gagal mengupdate user: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean hapus(int id) throws Exception {
        String sql = "DELETE FROM user WHERE id_user=?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affected = stmt.executeUpdate();
            return affected > 0;
            
        } catch (SQLException e) {
            throw new Exception("Gagal menghapus user: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Pengguna cariById(int id) throws Exception {
        String sql = "SELECT * FROM user WHERE id_user=?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buatPenggunaDariRow(rs);
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            throw new Exception("Gagal mencari user: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Pengguna> cariSemua() throws Exception {
        List<Pengguna> list = new ArrayList<>();
        String sql = "SELECT * FROM user ORDER BY id_user";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(buatPenggunaDariRow(rs));
            }
            
            return list;
            
        } catch (SQLException e) {
            throw new Exception("Gagal mengambil daftar user: " + e.getMessage(), e);
        }
    }
    
    public Pengguna cariByUsername(String username) throws Exception {
        String sql = "SELECT * FROM user WHERE username=?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buatPenggunaDariRow(rs);
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            throw new Exception("Gagal mencari user: " + e.getMessage(), e);
        }
    }
    
    private Pengguna buatPenggunaDariRow(ResultSet rs) throws SQLException {
        int idUser = rs.getInt("id_user");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String nama = rs.getString("nama");
        String role = rs.getString("role");
        
        // POLYMORPHISM: return type berbeda tergantung role
        if ("admin".equalsIgnoreCase(role)) {
            return new Admin(idUser, username, password, nama, role);
        } else {
            return new Kasir(idUser, username, password, nama, role);
        }
    }
}