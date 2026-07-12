/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author MKN
 */
public class Transaksi {
    private int idTransaksi;
    private String noTransaksi;
    private Timestamp tanggal;
    private int idUser;
    private String namaKasir;
    private double total;
    private double bayar;
    private double kembalian;
    
    private final List<DetailTransaksi> detailList;
    public Transaksi(String noTransaksi, int idUser, String namaKasir) {
        this.noTransaksi = noTransaksi;
        this.idUser = idUser;
        this.namaKasir = namaKasir;
        this.tanggal = new Timestamp(System.currentTimeMillis());
        this.detailList = new ArrayList<>();
        this.total = 0;
        this.bayar = 0;
        this.kembalian = 0;
    }
    
    public List<DetailTransaksi> getDetailList() {
        return Collections.unmodifiableList(detailList);
    }
    
    public void tambahDetail(DetailTransaksi detail) {
        detailList.add(detail);
        hitungTotal();
    }
    
    public void hapusDetail(int index) {
        if (index >= 0 && index < detailList.size()) {
            detailList.remove(index);
            hitungTotal();
        }
    }
    
    public void kosongkanDetail() {
        detailList.clear();
        hitungTotal();
    }
    
    private void hitungTotal() {
        this.total = 0;
        for (DetailTransaksi detail : detailList) {
            this.total += detail.getSubtotal();
        }
    }
    
    public void bayar(double bayar) {
        if (bayar < this.total) {
            throw new IllegalArgumentException("Uang pembayaran kurang.");
        }
        this.bayar = bayar;
        this.kembalian = bayar - this.total;
    }
    
    public int getIdTransaksi() {
        return idTransaksi;
    }
    
    public void setIdTransaksi(int idTransaksi) {
        this.idTransaksi = idTransaksi;
    }
    
    public String getNoTransaksi() {
        return noTransaksi;
    }
    
    public Timestamp getTanggal() {
        return tanggal;
    }
    
    public void setTanggal(Timestamp tanggal) {
        this.tanggal = tanggal;
    }
    
    public int getIdUser() {
        return idUser;
    }
    
    public String getNamaKasir() {
        return namaKasir;
    }
    
    public double getTotal() {
        return total;
    }
    
    public double getBayar() {
        return bayar;
    }
    
    public double getKembalian() {
        return kembalian;
    }
}
