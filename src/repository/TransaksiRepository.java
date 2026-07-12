/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import config.DatabaseConfig;
import model.DetailTransaksi;
import model.Transaksi;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 *
 * @author MKN
 */
public class TransaksiRepository {
    
    private final BarangRepository barangRepository;
    public TransaksiRepository() {
        this.barangRepository = new BarangRepository();
    }
    
    public int simpanTransaksi(Transaksi transaksi) throws Exception {
        Connection conn = null;
        PreparedStatement stmtTransaksi = null;
        PreparedStatement stmtDetail = null;
        ResultSet generatedKeys = null;
        
        try {
            // Buka koneksi dan mulai transaksi
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // Mulai database transaction
            
            // 1. Insert header transaksi
            String sqlTransaksi = "INSERT INTO transaksi (tanggal, id_user, total, bayar, kembalian) " +
                                  "VALUES (?, ?, ?, ?, ?)";
            
            stmtTransaksi = conn.prepareStatement(sqlTransaksi, Statement.RETURN_GENERATED_KEYS);
            stmtTransaksi.setTimestamp(1, transaksi.getTanggal());
            stmtTransaksi.setInt(2, transaksi.getIdUser());
            stmtTransaksi.setDouble(3, transaksi.getTotal());
            stmtTransaksi.setDouble(4, transaksi.getBayar());
            stmtTransaksi.setDouble(5, transaksi.getKembalian());
            
            stmtTransaksi.executeUpdate();
            
            // Ambil ID transaksi yang baru dibuat
            generatedKeys = stmtTransaksi.getGeneratedKeys();
            int idTransaksi = 0;
            if (generatedKeys.next()) {
                idTransaksi = generatedKeys.getInt(1);
            } else {
                throw new Exception("Gagal mendapatkan ID transaksi");
            }
            
            // 2. Insert detail transaksi (batch untuk performa)
            String sqlDetail = "INSERT INTO detail_transaksi (id_transaksi, id_barang, harga, qty, subtotal) " +
                               "VALUES (?, ?, ?, ?, ?)";
            
            stmtDetail = conn.prepareStatement(sqlDetail);
            
            for (DetailTransaksi detail : transaksi.getDetailList()) {
                stmtDetail.setInt(1, idTransaksi);
                stmtDetail.setInt(2, detail.getIdBarang());
                stmtDetail.setDouble(3, detail.getHarga());
                stmtDetail.setInt(4, detail.getQty());
                stmtDetail.setDouble(5, detail.getSubtotal());
                stmtDetail.addBatch();
            }
            
            stmtDetail.executeBatch();
            
            // 3. Kurangi stok barang untuk setiap item
            for (DetailTransaksi detail : transaksi.getDetailList()) {
                barangRepository.kurangiStok(conn, detail.getIdBarang(), detail.getQty());
            }
            
            // Commit transaksi jika semua berhasil
            conn.commit();
            
            return idTransaksi;
            
        } catch (Exception e) {
            // Rollback jika ada error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // Log error rollback
                }
            }
            throw new Exception("Gagal menyimpan transaksi: " + e.getMessage(), e);
            
        } finally {
            // Tutup semua resource
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (stmtDetail != null) stmtDetail.close();
                if (stmtTransaksi != null) stmtTransaksi.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Kembalikan auto commit
                    conn.close();
                }
            } catch (SQLException e) {
                // Log error
            }
        }
    }
    
    public double[] getStatistikHariIni() throws Exception {
        double[] hasil = new double[3]; // [jumlah_transaksi, total_penjualan, jumlah_barang]
        
        String sql = "SELECT " +
                     "  COUNT(DISTINCT t.id_transaksi) AS jumlah_transaksi, " +
                     "  COALESCE(SUM(t.total), 0) AS total_penjualan, " +
                     "  COALESCE(SUM(d.qty), 0) AS jumlah_barang " +
                     "FROM transaksi t " +
                     "LEFT JOIN detail_transaksi d ON t.id_transaksi = d.id_transaksi " +
                     "WHERE DATE(t.tanggal) = CURDATE()";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                hasil[0] = rs.getDouble("jumlah_transaksi");
                hasil[1] = rs.getDouble("total_penjualan");
                hasil[2] = rs.getDouble("jumlah_barang");
            }
            
            return hasil;
            
        } catch (SQLException e) {
            throw new Exception("Gagal mengambil statistik: " + e.getMessage(), e);
        }
    }

    public String buatNomorTransaksiBaru() throws Exception {
        String tanggalStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String prefix = "TRX" + tanggalStr;
        
        // Cari nomor urut transaksi hari ini
        String sql = "SELECT COUNT(*) AS jumlah FROM transaksi WHERE DATE(tanggal) = CURDATE()";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            int urutan = 1;
            if (rs.next()) {
                urutan = rs.getInt("jumlah") + 1;
            }
            
            // Format: TRX + tanggal + 4 digit urutan
            return String.format("%s%04d", prefix, urutan);
            
        } catch (SQLException e) {
            throw new Exception("Gagal membuat nomor transaksi: " + e.getMessage(), e);
        }
    }
}
