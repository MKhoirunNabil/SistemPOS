/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author MKN
 */
public class Admin extends Pengguna {
    public Admin(int idUser, String username, String password, String nama, String role) {
        super(idUser, username, password, nama, role);
    }
    
    @Override
    public String[] getMenuAkses() {
        return new String[]{
            "DATA_USER",
            "DATA_BARANG",
            "KASIR",
            "LAPORAN",
            "BACKUP",
            "RESTORE"
        };
    }
    
    @Override
    public String getLabelHakAkses() {
        return "Administrator";
    }
}
