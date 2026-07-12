/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author MKN
 */
public class DetailTransaksi {
    private int idDetail;
    private int idTransaksi;
    private int idBarang;
    private String barcode;
    private String namaBarang;
    private double harga;
    private int qty;
    private double subtotal;
    
    public DetailTransaksi(int idBarang, String barcode, String namaBarang, 
                           double harga, int qty) {
        this.idBarang = idBarang;
        this.barcode = barcode;
        this.namaBarang = namaBarang;
        this.harga = harga;
        this.qty = qty;
        hitungSubtotal(); // Otomatis hitung subtotal
    }
    
    private void hitungSubtotal() {
        this.subtotal = this.harga * this.qty;
    }
    
    public void setQty(int qty) {
        this.qty = qty;
        hitungSubtotal(); // Auto recalculate
    }
    
    public int getIdDetail() {
        return idDetail;
    }
    
    public void setIdDetail(int idDetail) {
        this.idDetail = idDetail;
    }
    
    public int getIdTransaksi() {
        return idTransaksi;
    }
    
    public void setIdTransaksi(int idTransaksi) {
        this.idTransaksi = idTransaksi;
    }
    
    public int getIdBarang() {
        return idBarang;
    }
    
    public String getBarcode() {
        return barcode;
    }
    
    public String getNamaBarang() {
        return namaBarang;
    }
    
    public double getHarga() {
        return harga;
    }
    
    public int getQty() {
        return qty;
    }
    
    public double getSubtotal() {
        return subtotal;
    }
}