/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author MKN
 */
public abstract class Pengguna implements Validatable {
    private int idUser;
    private String username;
    private String password;
    private String nama;
    private String role;
    
    public Pengguna(int idUser, String username, String password, String nama, String role) {
        this.idUser = idUser;
        this.username = username;
        this.password = password;
        this.nama = nama;
        this.role = role;
    }
    
    public abstract String[] getMenuAkses();
    
    public abstract String getLabelHakAkses();
    
    public boolean bolehAkses(String kodeMenu) {
        String[] menuAkses = getMenuAkses();
        for (String menu : menuAkses) {
            if (menu.equals(kodeMenu)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isValid() {
        return getValidationError().isEmpty();
    }
    
    @Override
    public String getValidationError() {
        if (username == null || username.trim().isEmpty()) {
            return "Username tidak boleh kosong";
        }
        if (password == null || password.trim().isEmpty()) {
            return "Password tidak boleh kosong";
        }
        if (nama == null || nama.trim().isEmpty()) {
            return "Nama tidak boleh kosong";
        }
        return ""; // Valid
    }
    
    @Override
    public String toString() {
        return nama + " (" + getLabelHakAkses() + ")";
    }
    
    public int getIdUser() {
        return idUser;
    }
    
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getNama() {
        return nama;
    }
    
    public void setNama(String nama) {
        this.nama = nama;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
}