/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import config.DatabaseConfig;
import model.Barang;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author MKN
 */
public class BarangRepository implements Dao<Barang> {
    @Override
    public boolean simpan(Barang data) throws Exception {
        String sql = "INSERT INTO barang (barcode, nama_barang, kategori, harga_beli, " +
                     "harga_jual, stok, satuan) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, data.getBarcode());
            stmt.setString(2, data.getNamaBarang());
            stmt.setString(3, data.getKategori());
            stmt.setDouble(4, data.getHargaBeli());
            stmt.setDouble(5, data.getHargaJual());
            stmt.setInt(6, data.getStok());
            stmt.setString(7, data.getSatuan());
            
            int affected = stmt.executeUpdate();
            return affected > 0;
            
        } catch (SQLException e) {
            throw new Exception("Gagal menyimpan barang: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean update(Barang data) throws Exception {
        String sql = "UPDATE barang SET barcode=?, nama_barang=?, kategori=?, " +
                     "harga_beli=?, harga_jual=?, stok=?, satuan=? WHERE id_barang=?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, data.getBarcode());
            stmt.setString(2, data.getNamaBarang());
            stmt.setString(3, data.getKategori());
            stmt.setDouble(4, data.getHargaBeli());
            stmt.setDouble(5, data.getHargaJual());
            stmt.setInt(6, data.getStok());
            stmt.setString(7, data.getSatuan());
            stmt.setInt(8, data.getIdBarang());
            
            int affected = stmt.executeUpdate();
            return affected > 0;
            
        } catch (SQLException e) {
            throw new Exception("Gagal mengupdate barang: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean hapus(int id) throws Exception {
        String sql = "DELETE FROM barang WHERE id_barang=?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affected = stmt.executeUpdate();
            return affected > 0;
            
        } catch (SQLException e) {
            throw new Exception("Gagal menghapus barang: " + e.getMessage(), e);
        }
    }
   
    @Override
    public Barang cariById(int id) throws Exception {
        String sql = "SELECT * FROM barang WHERE id_barang=?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buatBarangDariRow(rs);
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            throw new Exception("Gagal mencari barang: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Barang> cariSemua() throws Exception {
        List<Barang> list = new ArrayList<>();
        String sql = "SELECT * FROM barang ORDER BY nama_barang";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(buatBarangDariRow(rs));
            }
            
            return list;
            
        } catch (SQLException e) {
            throw new Exception("Gagal mengambil daftar barang: " + e.getMessage(), e);
        }
    }
    
    public Barang cariByBarcode(String barcode) throws Exception {
        String sql = "SELECT * FROM barang WHERE barcode=?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, barcode);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buatBarangDariRow(rs);
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            throw new Exception("Gagal mencari barang: " + e.getMessage(), e);
        }
    }
    
    public boolean barcodeSudahDipakai(String barcode, int idBarangDiabaikan) throws Exception {
        String sql = "SELECT COUNT(*) FROM barang WHERE barcode=? AND id_barang<>?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, barcode);
            stmt.setInt(2, idBarangDiabaikan);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
            return false;
            
        } catch (SQLException e) {
            throw new Exception("Gagal cek barcode: " + e.getMessage(), e);
        }
    }
    
    public List<Barang> cariByKeyword(String keyword) throws Exception {
        List<Barang> list = new ArrayList<>();
        String sql = "SELECT * FROM barang WHERE nama_barang LIKE ? OR barcode LIKE ? " +
                     "ORDER BY nama_barang";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String likePattern = "%" + keyword + "%";
            stmt.setString(1, likePattern);
            stmt.setString(2, likePattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(buatBarangDariRow(rs));
                }
            }
            
            return list;
            
        } catch (SQLException e) {
            throw new Exception("Gagal mencari barang: " + e.getMessage(), e);
        }
    }
    
    public void kurangiStok(Connection conn, int idBarang, int qty) throws Exception {
        // Cek stok dulu
        String sqlCek = "SELECT stok FROM barang WHERE id_barang=?";
        try (PreparedStatement stmtCek = conn.prepareStatement(sqlCek)) {
            stmtCek.setInt(1, idBarang);
            
            try (ResultSet rs = stmtCek.executeQuery()) {
                if (rs.next()) {
                    int stokSaatIni = rs.getInt("stok");
                    if (stokSaatIni < qty) {
                        throw new Exception("Stok tidak cukup untuk ID barang: " + idBarang);
                    }
                } else {
                    throw new Exception("Barang tidak ditemukan: ID " + idBarang);
                }
            }
        }
        
        // Kurangi stok
        String sqlUpdate = "UPDATE barang SET stok = stok - ? WHERE id_barang=? AND stok >= ?";
        try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
            stmtUpdate.setInt(1, qty);
            stmtUpdate.setInt(2, idBarang);
            stmtUpdate.setInt(3, qty);
            
            int affected = stmtUpdate.executeUpdate();
            if (affected == 0) {
                throw new Exception("Gagal mengurangi stok barang ID: " + idBarang);
            }
        }
    }
    private Barang buatBarangDariRow(ResultSet rs) throws SQLException {
        return new Barang(
            rs.getInt("id_barang"),
            rs.getString("barcode"),
            rs.getString("nama_barang"),
            rs.getString("kategori"),
            rs.getDouble("harga_beli"),
            rs.getDouble("harga_jual"),
            rs.getInt("stok"),
            rs.getString("satuan")
        );
    }
}
