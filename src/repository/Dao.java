/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import java.util.List;

/**
 *
 * @author MKN
 */
public interface Dao<Tipe> {
    boolean simpan(Tipe data) throws Exception;
    boolean update(Tipe data) throws Exception;
    
    boolean hapus(int id) throws Exception;
    
    Tipe cariById(int id) throws Exception;
    
    List<Tipe> cariSemua() throws Exception;
}
