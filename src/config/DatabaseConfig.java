/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author MKN
 */
public class DatabaseConfig {
    
    // Konfigurasi koneksi database
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DB_NAME = "db_pos";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Sesuaikan dengan password MySQL Anda
    
    // JDBC URL lengkap
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME 
            + "?useSSL=false&serverTimezone=Asia/Jakarta";
    
    private DatabaseConfig() {
    }
    
    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Buat dan kembalikan koneksi
            return DriverManager.getConnection(URL, USER, PASSWORD);
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver tidak ditemukan. "
                    + "Pastikan library MySQL Connector/J sudah ditambahkan ke project.", e);
        }
    }
    
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            // Jika berhasil membuat koneksi dan connection tidak null
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Test koneksi gagal: " + e.getMessage());
            return false;
        }
    }
    
    public static String getDatabaseName() {
        return DB_NAME;
    }
}