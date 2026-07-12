/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import model.Pengguna;
import repository.UserRepository;

/**
 *
 * @author MKN
 */
public class AuthService {
    
    private final UserRepository userRepository;
    
    public AuthService() {
        this.userRepository = new UserRepository();
    }
    
    public Pengguna login(String username, String password) throws Exception {
        // Validasi input kosong
        if (username == null || username.trim().isEmpty()) {
            throw new Exception("Username tidak boleh kosong.");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new Exception("Password tidak boleh kosong.");
        }
        
        // Cari user berdasarkan username
        Pengguna user = userRepository.cariByUsername(username.trim());
        
        if (user == null) {
            throw new Exception("Username tidak ditemukan.");
        }
        
        // Cek password (exact match)
        if (!user.getPassword().equals(password)) {
            throw new Exception("Password salah.");
        }
        
        // Login berhasil - return objek user (bisa Admin atau Kasir)
        return user;
    }
}