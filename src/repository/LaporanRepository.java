/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import config.DatabaseConfig;
import model.ItemLaporan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author MKN
 */
public class LaporanRepository {
    
    public double[] getRingkasan(Date tanggalAwal, Date tanggalAkhir) throws Exception {
        double[] hasil = new double[2]; // [jumlah_transaksi, total_penjualan]
        
        // Query SQL dengan agregasi
        String sql = "SELECT " +
                     "  COUNT(DISTINCT id_transaksi) AS jumlah_transaksi, " +
                     "  COALESCE(SUM(total), 0) AS total_penjualan " +
                     "FROM transaksi " +
                     "WHERE DATE(tanggal) BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set parameter tanggal
            stmt.setDate(1, tanggalAwal);
            stmt.setDate(2, tanggalAkhir);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    hasil[0] = rs.getDouble("jumlah_transaksi");
                    hasil[1] = rs.getDouble("total_penjualan");
                }
            }
            
            return hasil;
            
        } catch (SQLException e) {
            throw new Exception("Gagal mengambil ringkasan laporan: " + e.getMessage(), e);
        }
    }
    
    public List<ItemLaporan> getBarangTerlaris(Date tanggalAwal, Date tanggalAkhir, int limit) 
            throws Exception {
        
        List<ItemLaporan> hasil = new ArrayList<>();
        
        // Query SQL dengan JOIN dan GROUP BY
        String sql = "SELECT " +
                     "  b.nama_barang, " +
                     "  SUM(d.qty) AS total_qty, " +
                     "  SUM(d.subtotal) AS total_jual " +
                     "FROM transaksi t " +
                     "INNER JOIN detail_transaksi d ON t.id_transaksi = d.id_transaksi " +
                     "INNER JOIN barang b ON d.id_barang = b.id_barang " +
                     "WHERE DATE(t.tanggal) BETWEEN ? AND ? " +
                     "GROUP BY b.id_barang, b.nama_barang " +
                     "ORDER BY total_qty DESC " +
                     "LIMIT ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, tanggalAwal);
            stmt.setDate(2, tanggalAkhir);
            stmt.setInt(3, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ItemLaporan item = new ItemLaporan(
                        rs.getString("nama_barang"),
                        rs.getInt("total_qty"),
                        rs.getDouble("total_jual")
                    );
                    hasil.add(item);
                }
            }
            
            return hasil;
            
        } catch (SQLException e) {
            throw new Exception("Gagal mengambil data barang terlaris: " + e.getMessage(), e);
        }
    }
    
    public List<ItemLaporan> getBarangPalingSedikitTerjual(Date tanggalAwal, Date tanggalAkhir, int limit) 
            throws Exception {
        
        List<ItemLaporan> hasil = new ArrayList<>();
        
        // Query SQL - mirip terlaris tapi ORDER BY ASC
        String sql = "SELECT " +
                     "  b.nama_barang, " +
                     "  SUM(d.qty) AS total_qty, " +
                     "  SUM(d.subtotal) AS total_jual " +
                     "FROM transaksi t " +
                     "INNER JOIN detail_transaksi d ON t.id_transaksi = d.id_transaksi " +
                     "INNER JOIN barang b ON d.id_barang = b.id_barang " +
                     "WHERE DATE(t.tanggal) BETWEEN ? AND ? " +
                     "GROUP BY b.id_barang, b.nama_barang " +
                     "HAVING total_qty > 0 " +  // Hanya yang pernah terjual
                     "ORDER BY total_qty ASC " +  // ASC = dari terkecil
                     "LIMIT ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, tanggalAwal);
            stmt.setDate(2, tanggalAkhir);
            stmt.setInt(3, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ItemLaporan item = new ItemLaporan(
                        rs.getString("nama_barang"),
                        rs.getInt("total_qty"),
                        rs.getDouble("total_jual")
                    );
                    hasil.add(item);
                }
            }
            
            return hasil;
            
        } catch (SQLException e) {
            throw new Exception("Gagal mengambil data barang paling sedikit terjual: " 
                    + e.getMessage(), e);
        }
    }
}