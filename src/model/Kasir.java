/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author MKN
 */
public class Kasir extends Pengguna {
    
    public Kasir(int idUser, String username, String password, String nama, String role) {
        super(idUser, username, password, nama, role);
    }
    
    @Override
    public String[] getMenuAkses() {
        return new String[]{"KASIR"};
    }
    
    @Override
    public String getLabelHakAkses() {
        return "Kasir";
    }
}
