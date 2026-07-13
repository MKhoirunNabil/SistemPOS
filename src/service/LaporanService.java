/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import model.ItemLaporan;
import repository.LaporanRepository;

import java.sql.Date;
import java.util.List;

/**
 *
 * @author MKN
 */
public class LaporanService {
    
    private final LaporanRepository laporanRepository;
    
    public LaporanService() {
        this.laporanRepository = new LaporanRepository();
    }
    
    public double[] getRingkasan(java.util.Date tanggalAwal, java.util.Date tanggalAkhir) 
            throws Exception {
        
        // Validasi: tanggal awal <= tanggal akhir
        if (tanggalAwal.after(tanggalAkhir)) {
            throw new Exception("Tanggal awal tidak boleh lebih besar dari tanggal akhir.");
        }
        
        // Konversi java.util.Date ke java.sql.Date
        Date sqlTanggalAwal = new Date(tanggalAwal.getTime());
        Date sqlTanggalAkhir = new Date(tanggalAkhir.getTime());
        
        // Delegasi ke repository
        return laporanRepository.getRingkasan(sqlTanggalAwal, sqlTanggalAkhir);
    }
    
    public List<ItemLaporan> getBarangTerlaris(java.util.Date tanggalAwal, 
                                                java.util.Date tanggalAkhir) 
            throws Exception {
        
        // Validasi rentang tanggal
        if (tanggalAwal.after(tanggalAkhir)) {
            throw new Exception("Tanggal awal tidak boleh lebih besar dari tanggal akhir.");
        }
        
        // Konversi tipe tanggal
        Date sqlTanggalAwal = new Date(tanggalAwal.getTime());
        Date sqlTanggalAkhir = new Date(tanggalAkhir.getTime());
        
        // Ambil top 5 barang terlaris
        return laporanRepository.getBarangTerlaris(sqlTanggalAwal, sqlTanggalAkhir, 5);
    }
    
    public List<ItemLaporan> getBarangPalingSedikitTerjual(java.util.Date tanggalAwal, 
                                                            java.util.Date tanggalAkhir) 
            throws Exception {
        
        // Validasi rentang tanggal
        if (tanggalAwal.after(tanggalAkhir)) {
            throw new Exception("Tanggal awal tidak boleh lebih besar dari tanggal akhir.");
        }
        
        // Konversi tipe tanggal
        Date sqlTanggalAwal = new Date(tanggalAwal.getTime());
        Date sqlTanggalAkhir = new Date(tanggalAkhir.getTime());
        
        // Ambil top 5 barang paling sedikit terjual
        return laporanRepository.getBarangPalingSedikitTerjual(
                sqlTanggalAwal, sqlTanggalAkhir, 5);
    }
}
