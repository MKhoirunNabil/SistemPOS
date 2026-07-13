/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import model.Barang;
import repository.BarangRepository;

import java.util.List;

/**
 *
 * @author MKN
 */
public class BarangService {
    
    private final BarangRepository barangRepository;
    
    public BarangService() {
        this.barangRepository = new BarangRepository();
    }
    
    public List<Barang> ambilSemuaBarang() throws Exception {
        return barangRepository.cariSemua();
    }
    
    public List<Barang> cariBarang(String keyword) throws Exception {
        // Jika keyword kosong, return semua barang
        if (keyword == null || keyword.trim().isEmpty()) {
            return ambilSemuaBarang();
        }
        
        return barangRepository.cariByKeyword(keyword.trim());
    }
    
    public void tambahBarang(Barang barang) throws Exception {
        // Validasi 1: Cek data valid lewat interface Validatable
        if (!barang.isValid()) {
            throw new Exception(barang.getValidationError());
        }
        
        // Validasi 2: Cek barcode belum dipakai
        // Parameter kedua = 0 karena ini barang baru (belum punya ID)
        boolean barcodeSudahAda = barangRepository.barcodeSudahDipakai(
                barang.getBarcode(), 0);
        
        if (barcodeSudahAda) {
            throw new Exception("Barcode sudah digunakan barang lain.");
        }
        
        // Simpan ke database
        boolean berhasil = barangRepository.simpan(barang);
        
        if (!berhasil) {
            throw new Exception("Gagal menyimpan data barang.");
        }
    }
    
    public void updateBarang(Barang barang) throws Exception {
        // Validasi 1: Cek data valid
        if (!barang.isValid()) {
            throw new Exception(barang.getValidationError());
        }
        
        // Validasi 2: Cek barcode belum dipakai oleh barang LAIN
        // Parameter kedua = idBarang itu sendiri (diabaikan dalam pengecekan)
        boolean barcodeSudahAda = barangRepository.barcodeSudahDipakai(
                barang.getBarcode(), barang.getIdBarang());
        
        if (barcodeSudahAda) {
            throw new Exception("Barcode sudah digunakan barang lain.");
        }
        
        // Update ke database
        boolean berhasil = barangRepository.update(barang);
        
        if (!berhasil) {
            throw new Exception("Gagal mengupdate data barang.");
        }
    }
    
    public void hapusBarang(int idBarang) throws Exception {
        boolean berhasil = barangRepository.hapus(idBarang);
        
        if (!berhasil) {
            throw new Exception("Gagal menghapus data barang.");
        }
    }
}
