package model;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.time.LocalDateTime;

/**
 *
 * @author MKN
 */
public class Barang implements Validatable {
    private int idBarang;
    private String barcode;
    private String namaBarang;
    private String kategori;
    private double hargaBeli;
    private double hargaJual;
    private int stok;
    private String satuan;
    
    public Barang() {
        this.satuan = "pcs"; // default
    }
    
    public Barang(int idBarang, String barcode, String namaBarang, String kategori,
                  double hargaBeli, double hargaJual, int stok, String satuan) {
        this.idBarang = idBarang;
        this.barcode = barcode;
        this.namaBarang = namaBarang;
        this.kategori = kategori;
        this.hargaBeli = hargaBeli;
        this.hargaJual = hargaJual;
        this.stok = stok;
        this.satuan = satuan;
    }
    
    @Override
    public boolean isValid() {
        return getValidationError().isEmpty();
    }
    
    @Override
    public String getValidationError() {
        if (barcode == null || barcode.trim().isEmpty()) {
            return "Barcode tidak boleh kosong";
        }
        if (namaBarang == null || namaBarang.trim().isEmpty()) {
            return "Nama barang tidak boleh kosong";
        }
        if (hargaBeli < 0) {
            return "Harga beli tidak boleh negatif";
        }
        if (hargaJual < 0) {
            return "Harga jual tidak boleh negatif";
        }
        if (stok < 0) {
            return "Stok tidak boleh negatif";
        }
        return ""; // Valid
    }
    
    public int getIdBarang() {
        return idBarang;
    }
    
    public void setIdBarang(int idBarang) {
        this.idBarang = idBarang;
    }
    
    public String getBarcode() {
        return barcode;
    }
    
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    
    public String getNamaBarang() {
        return namaBarang;
    }
    
    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }
    
    public String getKategori() {
        return kategori;
    }
    
    public void setKategori(String kategori) {
        this.kategori = kategori;
    }
    
    public double getHargaBeli() {
        return hargaBeli;
    }
    
    public void setHargaBeli(double hargaBeli) {
        this.hargaBeli = hargaBeli;
    }
    
    public double getHargaJual() {
        return hargaJual;
    }
    
    public void setHargaJual(double hargaJual) {
        this.hargaJual = hargaJual;
    }
    
    public int getStok() {
        return stok;
    }
    
    public void setStok(int stok) {
        this.stok = stok;
    }
    
    public String getSatuan() {
        return satuan;
    }
    
    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }
}