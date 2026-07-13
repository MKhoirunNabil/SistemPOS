/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author MKN
 */
public class ItemLaporan {
    // Field private - ENCAPSULATION
    private String namaBarang;
    private int totalQtyTerjual;
    private double totalPenjualan;
    
    public ItemLaporan(String namaBarang, int totalQtyTerjual, double totalPenjualan) {
        this.namaBarang = namaBarang;
        this.totalQtyTerjual = totalQtyTerjual;
        this.totalPenjualan = totalPenjualan;
    }
    
    public String getNamaBarang() {
        return namaBarang;
    }
    
    public int getTotalQtyTerjual() {
        return totalQtyTerjual;
    }
    
    public double getTotalPenjualan() {
        return totalPenjualan;
    }
    
    @Override
    public String toString() {
        return String.format("%s | Qty: %d | Total: Rp %.2f", 
                namaBarang, totalQtyTerjual, totalPenjualan);
    }
}