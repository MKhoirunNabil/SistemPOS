/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import model.Barang;
import model.DetailTransaksi;
import model.Pengguna;
import model.Transaksi;
import repository.BarangRepository;
import repository.TransaksiRepository;

import java.util.List;

/**
 *
 * @author MKN
 */
public class KasirService {
    
    private final BarangRepository barangRepository;
    private final TransaksiRepository transaksiRepository;
    
    public KasirService() {
        this.barangRepository = new BarangRepository();
        this.transaksiRepository = new TransaksiRepository();
    }
    
    public List<Barang> ambilSemuaBarang() throws Exception {
        return barangRepository.cariSemua();
    }
    
    public List<Barang> cariBarang(String keyword) throws Exception {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ambilSemuaBarang();
        }
        return barangRepository.cariByKeyword(keyword.trim());
    }
    
    public Transaksi buatTransaksiBaru(Pengguna user) throws Exception {
        String noTransaksi = transaksiRepository.buatNomorTransaksiBaru();
        return new Transaksi(noTransaksi, user.getIdUser(), user.getNama());
    }
    
    public DetailTransaksi buatDetailDariBarang(Barang barang, int qty) {
        // Validasi qty
        if (qty <= 0) {
            throw new IllegalArgumentException("Jumlah barang harus lebih dari 0");
        }
        
        if (qty > barang.getStok()) {
            throw new IllegalArgumentException(
                "Stok tidak cukup. Stok tersedia: " + barang.getStok()
            );
        }
        
        // Buat detail transaksi
        return new DetailTransaksi(
            barang.getIdBarang(),
            barang.getBarcode(),
            barang.getNamaBarang(),
            barang.getHargaJual(), // Pakai harga jual
            qty
        );
    }
    
    public int prosesPembayaran(Transaksi transaksi, double jumlahBayar) throws Exception {
        // Validasi keranjang tidak kosong
        if (transaksi.getDetailList().isEmpty()) {
            throw new Exception("Keranjang belanja kosong. Tambahkan barang terlebih dahulu.");
        }
        
        // Proses bayar (akan throw exception jika uang kurang)
        transaksi.bayar(jumlahBayar);
        
        // Simpan transaksi ke database (dengan transaction)
        int idTransaksi = transaksiRepository.simpanTransaksi(transaksi);
        
        return idTransaksi;
    }
    
    public double[] getStatistikHariIni() throws Exception {
        return transaksiRepository.getStatistikHariIni();
    }
}
