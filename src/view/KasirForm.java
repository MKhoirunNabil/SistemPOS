/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.text.*;
import model.Pengguna;
import model.Barang;
import model.Transaksi;
import model.DetailTransaksi;
import service.KasirService;
/**
 *
 * @author user
 */
public class KasirForm extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(KasirForm.class.getName());
    private Pengguna userLogin;
    private KasirService kasirService;
    private Transaksi transaksiAktif;
    private List<Barang> listBarang;
    private NumberFormat formatRupiah;
    /**
     * Creates new form KasirForm2
     * @param userLogin
     */
    public KasirForm(Pengguna userLogin) {
        initComponents();
        setLocationRelativeTo(null);
        
        this.userLogin = userLogin;
        this.kasirService = new KasirService();
        this.listBarang = new ArrayList<>();
        this.formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        
        txtBayar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { txtBayarChanged(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { txtBayarChanged(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { txtBayarChanged(); }
        });
        
        loadDataAwal();

    }
    
    private void loadDataAwal() {
        buatTransaksiBaru();
        loadSemuaBarang();
    }
    
    private void buatTransaksiBaru() {
        try {
            transaksiAktif = kasirService.buatTransaksiBaru(userLogin);
            
            // Update info transaksi
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            lblTanggalTransaksi.setText(sdf.format(new Date()));
            lblNoTransaksi.setText(transaksiAktif.getNoTransaksi());
            lblKasirTransaksi.setText(transaksiAktif.getNamaKasir());
            
            // Reset keranjang & pembayaran
            refreshTabelKeranjang();
            resetPembayaran();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Gagal membuat transaksi baru: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private void loadSemuaBarang() {
        try {
            listBarang = kasirService.ambilSemuaBarang();
            refreshTabelBarang(listBarang);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Gagal memuat data barang: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private void refreshTabelBarang(List<Barang> data) {
        DefaultTableModel model = (DefaultTableModel) tblDataBarang.getModel();
        model.setRowCount(0);
        
        for (Barang b : data) {
            model.addRow(new Object[]{
                b.getBarcode(),
                b.getNamaBarang(),
                b.getKategori(),
                formatRupiah.format(b.getHargaJual()),
                b.getStok(),
                b.getSatuan()
            });
        }
    }
    
    private void refreshTabelKeranjang() {
        DefaultTableModel model = (DefaultTableModel) tblKeranjang.getModel();
        model.setRowCount(0);
        
        if (transaksiAktif != null) {
            for (DetailTransaksi detail : transaksiAktif.getDetailList()) {
                model.addRow(new Object[]{
                    detail.getBarcode(),
                    detail.getNamaBarang(),
                    formatRupiah.format(detail.getHarga()),
                    detail.getQty(),
                    formatRupiah.format(detail.getSubtotal())
                });
            }
        }
    }
    
    private void updateTotal() {
        if (transaksiAktif != null) {
            double total = transaksiAktif.getTotal();
            lblNilaiTotal.setText(formatRupiah.format(total));
            txtTotalBayar.setText(formatRupiah.format(total));
        }
    }
    
    private void resetPembayaran() {
        txtBayar.setText("0");
        txtKembalian.setText("Rp 0");
        txtTotalBayar.setText("Rp 0");
        lblNilaiTotal.setText("Rp 0");
    }
    
    private Barang cariBarangByBarcode(String barcode) {
        for (Barang b : listBarang) {
            if (b.getBarcode().equals(barcode)) {
                return b;
            }
        }
        return null;
    }
    
    private void txtCariBarangChanged() {
        String keyword = txtCariBarang.getText().trim();
        
        try {
            if (keyword.isEmpty()) {
                refreshTabelBarang(listBarang);
            } else {
                List<Barang> hasil = kasirService.cariBarang(keyword);
                refreshTabelBarang(hasil);
            }
        } catch (Exception ex) {
            System.err.println("Error cari barang: " + ex.getMessage());
        }
    }
    
    private void tblKeranjangQtyChanged(int row) {
        try {
            DefaultTableModel model = (DefaultTableModel) tblKeranjang.getModel();
            
            // Ambil qty baru
            Object qtyObj = model.getValueAt(row, 3);
            int qtyBaru = Integer.parseInt(qtyObj.toString());
            
            // Validasi qty > 0
            if (qtyBaru <= 0) {
                JOptionPane.showMessageDialog(
                    this,
                    "Qty harus lebih dari 0.",
                    "Validasi Qty",
                    JOptionPane.WARNING_MESSAGE
                );
                refreshTabelKeranjang();
                return;
            }
            
            // Ambil detail dari transaksi
            DetailTransaksi detail = transaksiAktif.getDetailList().get(row);
            
            // Validasi terhadap stok
            Barang barang = cariBarangByBarcode(detail.getBarcode());
            if (barang != null && qtyBaru > barang.getStok()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Qty melebihi stok tersedia (" + barang.getStok() + ").",
                    "Validasi Stok",
                    JOptionPane.WARNING_MESSAGE
                );
                refreshTabelKeranjang();
                return;
            }
            
            // Update qty
            detail.setQty(qtyBaru);
            
            // Refresh tampilan
            refreshTabelKeranjang();
            updateTotal();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Qty harus berupa angka.",
                "Validasi Qty",
                JOptionPane.WARNING_MESSAGE
            );
            refreshTabelKeranjang();
        }
    }
    
    private void txtBayarChanged() {
        try {
            double total = transaksiAktif != null ? transaksiAktif.getTotal() : 0;
            String bayarStr = txtBayar.getText().trim();
            
            if (bayarStr.isEmpty()) {
                txtKembalian.setText("Rp 0");
                return;
            }
            
            double bayar = Double.parseDouble(bayarStr);
            double kembalian = bayar - total;
            
            txtKembalian.setText(formatRupiah.format(kembalian));
            
        } catch (NumberFormatException ex) {
            txtKembalian.setText("Rp 0");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelHeaderWrapper2 = new javax.swing.JPanel();
        panelJudulTransaksi = new javax.swing.JPanel();
        lblJudulTransaksi = new javax.swing.JLabel();
        btnTutupTransaksi = new javax.swing.JButton();
        sepJudulTransaksi = new javax.swing.JSeparator();
        panelInfoTransaksi = new javax.swing.JPanel();
        lblTanggalTransaksi = new javax.swing.JLabel();
        lblNoTransaksi = new javax.swing.JLabel();
        lblKasirTransaksi = new javax.swing.JLabel();
        panelBodyTransaksi = new javax.swing.JPanel();
        panelDaftarBarang = new javax.swing.JPanel();
        panelSearchBarang = new javax.swing.JPanel();
        lblJudulDaftarBarang = new javax.swing.JLabel();
        panelKotakSearch = new javax.swing.JPanel();
        txtCariBarang = new javax.swing.JTextField();
        btnClearSearch = new javax.swing.JButton();
        scrollDaftarBarang = new javax.swing.JScrollPane();
        tblDataBarang = new javax.swing.JTable();
        btnTambahKeranjang = new javax.swing.JButton();
        panelKeranjang = new javax.swing.JPanel();
        lblJudulKeranjang = new javax.swing.JLabel();
        scrollKeranjang = new javax.swing.JScrollPane();
        tblKeranjang = new javax.swing.JTable();
        panelKeranjangFooter = new javax.swing.JPanel();
        panelTombolKeranjang = new javax.swing.JPanel();
        btnHapusItem = new javax.swing.JButton();
        btnKosongkanKeranjang = new javax.swing.JButton();
        panelTotalBelanja = new javax.swing.JPanel();
        lblLabeltotal = new javax.swing.JLabel();
        lblNilaiTotal = new javax.swing.JLabel();
        panelPembayaran = new javax.swing.JPanel();
        lblJudulPembayaran = new javax.swing.JLabel();
        panelFieldPembayaran = new javax.swing.JPanel();
        lblTotalBayar = new javax.swing.JLabel();
        txtTotalBayar = new javax.swing.JTextField();
        lblBayar = new javax.swing.JLabel();
        txtBayar = new javax.swing.JTextField();
        lblKembalian = new javax.swing.JLabel();
        txtKembalian = new javax.swing.JTextField();
        panelTombolPembayaran = new javax.swing.JPanel();
        btnBayarCetak = new javax.swing.JButton();
        btnCetakUlang = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Transaksi Penjualan - Kasir");
        setBackground(new java.awt.Color(243, 244, 246));
        setSize(new java.awt.Dimension(950, 700));

        panelHeaderWrapper2.setBackground(new java.awt.Color(255, 255, 255));
        panelHeaderWrapper2.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 20, 10, 20));
        panelHeaderWrapper2.setLayout(new javax.swing.BoxLayout(panelHeaderWrapper2, javax.swing.BoxLayout.Y_AXIS));

        panelJudulTransaksi.setBackground(new java.awt.Color(255, 255, 255));
        panelJudulTransaksi.setLayout(new java.awt.BorderLayout());

        lblJudulTransaksi.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblJudulTransaksi.setForeground(new java.awt.Color(17, 24, 39));
        lblJudulTransaksi.setText("TRANSAKSI PENJUALAN");
        panelJudulTransaksi.add(lblJudulTransaksi, java.awt.BorderLayout.WEST);

        btnTutupTransaksi.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTutupTransaksi.setForeground(new java.awt.Color(107, 114, 128));
        btnTutupTransaksi.setText("[x]");
        btnTutupTransaksi.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnTutupTransaksi.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTutupTransaksi.setFocusable(false);
        btnTutupTransaksi.setPreferredSize(new java.awt.Dimension(32, 32));
        btnTutupTransaksi.addActionListener(this::btnTutupTransaksiActionPerformed);
        panelJudulTransaksi.add(btnTutupTransaksi, java.awt.BorderLayout.EAST);

        panelHeaderWrapper2.add(panelJudulTransaksi);

        sepJudulTransaksi.setForeground(new java.awt.Color(209, 213, 219));
        panelHeaderWrapper2.add(sepJudulTransaksi);

        panelInfoTransaksi.setBackground(new java.awt.Color(255, 255, 255));
        panelInfoTransaksi.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panelInfoTransaksi.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 25, 4));

        lblTanggalTransaksi.setForeground(new java.awt.Color(55, 65, 81));
        lblTanggalTransaksi.setText("Tanggal: 15/01/2024 14:30");
        panelInfoTransaksi.add(lblTanggalTransaksi);

        lblNoTransaksi.setForeground(new java.awt.Color(55, 65, 81));
        lblNoTransaksi.setText("No: TRX20240115001");
        panelInfoTransaksi.add(lblNoTransaksi);

        lblKasirTransaksi.setForeground(new java.awt.Color(55, 65, 81));
        lblKasirTransaksi.setText("Kasir: Admin");
        panelInfoTransaksi.add(lblKasirTransaksi);

        panelHeaderWrapper2.add(panelInfoTransaksi);

        getContentPane().add(panelHeaderWrapper2, java.awt.BorderLayout.NORTH);

        panelBodyTransaksi.setBackground(new java.awt.Color(243, 244, 246));
        panelBodyTransaksi.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panelBodyTransaksi.setLayout(new java.awt.GridLayout(1, 2, 20, 0));

        panelDaftarBarang.setBackground(new java.awt.Color(255, 255, 255));
        panelDaftarBarang.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(229, 231, 235)), javax.swing.BorderFactory.createEmptyBorder(15, 10, 15, 15)));
        panelDaftarBarang.setLayout(new java.awt.BorderLayout());

        panelSearchBarang.setBackground(new java.awt.Color(255, 255, 255));
        panelSearchBarang.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panelSearchBarang.setLayout(new javax.swing.BoxLayout(panelSearchBarang, javax.swing.BoxLayout.Y_AXIS));

        lblJudulDaftarBarang.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblJudulDaftarBarang.setForeground(new java.awt.Color(17, 24, 39));
        lblJudulDaftarBarang.setText("DAFTAR BARANG");
        panelSearchBarang.add(lblJudulDaftarBarang);

        panelKotakSearch.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 0, 0, 0));
        panelKotakSearch.setLayout(new java.awt.BorderLayout());

        txtCariBarang.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(209, 213, 219)));
        txtCariBarang.setPreferredSize(new java.awt.Dimension(64, 32));
        panelKotakSearch.add(txtCariBarang, java.awt.BorderLayout.CENTER);

        btnClearSearch.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnClearSearch.setForeground(new java.awt.Color(107, 114, 128));
        btnClearSearch.setText("[x]");
        btnClearSearch.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnClearSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClearSearch.setFocusable(false);
        btnClearSearch.setPreferredSize(new java.awt.Dimension(32, 32));
        btnClearSearch.addActionListener(this::btnClearSearchActionPerformed);
        panelKotakSearch.add(btnClearSearch, java.awt.BorderLayout.EAST);

        panelSearchBarang.add(panelKotakSearch);

        panelDaftarBarang.add(panelSearchBarang, java.awt.BorderLayout.NORTH);

        tblDataBarang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Barcode", "Nama", "Kategori", "Harga"
            }
        ));
        tblDataBarang.setGridColor(new java.awt.Color(229, 231, 235));
        tblDataBarang.setRowHeight(28);
        tblDataBarang.setSelectionBackground(new java.awt.Color(219, 234, 254));
        scrollDaftarBarang.setViewportView(tblDataBarang);

        panelDaftarBarang.add(scrollDaftarBarang, java.awt.BorderLayout.CENTER);

        btnTambahKeranjang.setBackground(new java.awt.Color(37, 99, 235));
        btnTambahKeranjang.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnTambahKeranjang.setForeground(new java.awt.Color(255, 255, 255));
        btnTambahKeranjang.setText("+ Tambah ke Keranjang");
        btnTambahKeranjang.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        btnTambahKeranjang.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTambahKeranjang.setFocusable(false);
        btnTambahKeranjang.setPreferredSize(new java.awt.Dimension(153, 40));
        btnTambahKeranjang.addActionListener(this::btnTambahKeranjangActionPerformed);
        panelDaftarBarang.add(btnTambahKeranjang, java.awt.BorderLayout.SOUTH);

        panelBodyTransaksi.add(panelDaftarBarang);

        panelKeranjang.setBackground(new java.awt.Color(255, 255, 255));
        panelKeranjang.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15), javax.swing.BorderFactory.createLineBorder(new java.awt.Color(229, 231, 235))));
        panelKeranjang.setLayout(new java.awt.BorderLayout());

        lblJudulKeranjang.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblJudulKeranjang.setForeground(new java.awt.Color(17, 24, 39));
        lblJudulKeranjang.setText("KERANJANG BELANJA");
        lblJudulKeranjang.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panelKeranjang.add(lblJudulKeranjang, java.awt.BorderLayout.NORTH);

        tblKeranjang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Barcode", "Nama", "Harga", "Qty", "Subtotal"
            }
        ));
        tblKeranjang.setGridColor(new java.awt.Color(229, 231, 235));
        tblKeranjang.setRowHeight(28);
        scrollKeranjang.setViewportView(tblKeranjang);

        panelKeranjang.add(scrollKeranjang, java.awt.BorderLayout.CENTER);

        panelKeranjangFooter.setBackground(new java.awt.Color(255, 255, 255));
        panelKeranjangFooter.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panelKeranjangFooter.setLayout(new javax.swing.BoxLayout(panelKeranjangFooter, javax.swing.BoxLayout.Y_AXIS));

        panelTombolKeranjang.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        panelTombolKeranjang.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));

        btnHapusItem.setBackground(new java.awt.Color(254, 242, 242));
        btnHapusItem.setForeground(new java.awt.Color(220, 38, 38));
        btnHapusItem.setText("Hapus Item");
        btnHapusItem.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(254, 202, 202)));
        btnHapusItem.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHapusItem.setFocusable(false);
        btnHapusItem.setPreferredSize(new java.awt.Dimension(110, 32));
        btnHapusItem.addActionListener(this::btnHapusItemActionPerformed);
        panelTombolKeranjang.add(btnHapusItem);

        btnKosongkanKeranjang.setBackground(new java.awt.Color(254, 242, 242));
        btnKosongkanKeranjang.setForeground(new java.awt.Color(220, 38, 38));
        btnKosongkanKeranjang.setText("Kosongkan Keranjang");
        btnKosongkanKeranjang.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(254, 202, 202)));
        btnKosongkanKeranjang.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnKosongkanKeranjang.setFocusable(false);
        btnKosongkanKeranjang.setPreferredSize(new java.awt.Dimension(150, 32));
        btnKosongkanKeranjang.addActionListener(this::btnKosongkanKeranjangActionPerformed);
        panelTombolKeranjang.add(btnKosongkanKeranjang);

        panelTotalBelanja.setBackground(new java.awt.Color(239, 246, 255));
        panelTotalBelanja.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(12, 15, 12, 15), javax.swing.BorderFactory.createLineBorder(new java.awt.Color(191, 219, 254))));
        panelTotalBelanja.setLayout(new java.awt.BorderLayout());

        lblLabeltotal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblLabeltotal.setForeground(new java.awt.Color(17, 24, 39));
        lblLabeltotal.setText("TOTAL BELANJA");
        panelTotalBelanja.add(lblLabeltotal, java.awt.BorderLayout.WEST);

        lblNilaiTotal.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblNilaiTotal.setForeground(new java.awt.Color(37, 99, 235));
        lblNilaiTotal.setText("Rp 45.000");
        panelTotalBelanja.add(lblNilaiTotal, java.awt.BorderLayout.CENTER);

        panelTombolKeranjang.add(panelTotalBelanja);

        panelKeranjangFooter.add(panelTombolKeranjang);

        panelKeranjang.add(panelKeranjangFooter, java.awt.BorderLayout.SOUTH);

        panelBodyTransaksi.add(panelKeranjang);

        getContentPane().add(panelBodyTransaksi, java.awt.BorderLayout.CENTER);

        panelPembayaran.setBackground(new java.awt.Color(255, 255, 255));
        panelPembayaran.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(15, 20, 15, 20), javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(209, 213, 219))));
        panelPembayaran.setPreferredSize(new java.awt.Dimension(950, 250));
        panelPembayaran.setLayout(new java.awt.BorderLayout());

        lblJudulPembayaran.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblJudulPembayaran.setForeground(new java.awt.Color(17, 24, 39));
        lblJudulPembayaran.setText("PEMBAYARAN");
        lblJudulPembayaran.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panelPembayaran.add(lblJudulPembayaran, java.awt.BorderLayout.NORTH);

        lblTotalBayar.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblTotalBayar.setForeground(new java.awt.Color(55, 65, 81));
        lblTotalBayar.setText("Total");

        txtTotalBayar.setEditable(false);
        txtTotalBayar.setBackground(new java.awt.Color(243, 244, 246));
        txtTotalBayar.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        txtTotalBayar.setPreferredSize(new java.awt.Dimension(200, 32));

        lblBayar.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblBayar.setForeground(new java.awt.Color(55, 65, 81));
        lblBayar.setText("Bayar");

        txtBayar.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtBayar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(209, 213, 219)));
        txtBayar.setPreferredSize(new java.awt.Dimension(200, 32));

        lblKembalian.setForeground(new java.awt.Color(55, 65, 81));
        lblKembalian.setText("Kembalian");

        txtKembalian.setEditable(false);
        txtKembalian.setBackground(new java.awt.Color(243, 244, 246));
        txtKembalian.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtKembalian.setPreferredSize(new java.awt.Dimension(200, 32));

        javax.swing.GroupLayout panelFieldPembayaranLayout = new javax.swing.GroupLayout(panelFieldPembayaran);
        panelFieldPembayaran.setLayout(panelFieldPembayaranLayout);
        panelFieldPembayaranLayout.setHorizontalGroup(
            panelFieldPembayaranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFieldPembayaranLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFieldPembayaranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFieldPembayaranLayout.createSequentialGroup()
                        .addGroup(panelFieldPembayaranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTotalBayar)
                            .addComponent(lblBayar))
                        .addGap(41, 41, 41)
                        .addGroup(panelFieldPembayaranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTotalBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelFieldPembayaranLayout.createSequentialGroup()
                        .addComponent(lblKembalian)
                        .addGap(18, 18, 18)
                        .addComponent(txtKembalian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(697, Short.MAX_VALUE))
        );
        panelFieldPembayaranLayout.setVerticalGroup(
            panelFieldPembayaranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFieldPembayaranLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(panelFieldPembayaranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotalBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalBayar))
                .addGap(10, 10, 10)
                .addGroup(panelFieldPembayaranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBayar))
                .addGap(10, 10, 10)
                .addGroup(panelFieldPembayaranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblKembalian)
                    .addComponent(txtKembalian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelPembayaran.add(panelFieldPembayaran, java.awt.BorderLayout.CENTER);

        panelTombolPembayaran.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 0, 0, 0));
        panelTombolPembayaran.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 12, 0));

        btnBayarCetak.setBackground(new java.awt.Color(22, 163, 74));
        btnBayarCetak.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnBayarCetak.setForeground(new java.awt.Color(255, 255, 255));
        btnBayarCetak.setText("BAYAR & CETAK");
        btnBayarCetak.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnBayarCetak.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBayarCetak.setFocusable(false);
        btnBayarCetak.setPreferredSize(new java.awt.Dimension(170, 42));
        btnBayarCetak.addActionListener(this::btnBayarCetakActionPerformed);
        panelTombolPembayaran.add(btnBayarCetak);

        btnCetakUlang.setBackground(new java.awt.Color(37, 99, 235));
        btnCetakUlang.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCetakUlang.setForeground(new java.awt.Color(255, 255, 255));
        btnCetakUlang.setText("CETAK ULANG");
        btnCetakUlang.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnCetakUlang.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCetakUlang.setFocusable(false);
        btnCetakUlang.setPreferredSize(new java.awt.Dimension(150, 42));
        btnCetakUlang.addActionListener(this::btnCetakUlangActionPerformed);
        panelTombolPembayaran.add(btnCetakUlang);

        btnBatal.setBackground(new java.awt.Color(220, 38, 38));
        btnBatal.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnBatal.setForeground(new java.awt.Color(255, 255, 255));
        btnBatal.setText("BATAL");
        btnBatal.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnBatal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBatal.setFocusable(false);
        btnBatal.setPreferredSize(new java.awt.Dimension(120, 42));
        btnBatal.addActionListener(this::btnBatalActionPerformed);
        panelTombolPembayaran.add(btnBatal);

        panelPembayaran.add(panelTombolPembayaran, java.awt.BorderLayout.SOUTH);

        getContentPane().add(panelPembayaran, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClearSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearSearchActionPerformed
        // TODO add your handling code here:
        txtCariBarang.setText("");
        loadSemuaBarang();
    }//GEN-LAST:event_btnClearSearchActionPerformed

    private void btnTambahKeranjangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahKeranjangActionPerformed
        // TODO add your handling code here:
        int selectedRow = tblDataBarang.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Pilih barang yang ingin ditambahkan ke keranjang.",
                "Validasi",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        try {
            // Ambil barcode dari tabel
            DefaultTableModel model = (DefaultTableModel) tblDataBarang.getModel();
            String barcode = model.getValueAt(selectedRow, 0).toString();
            
            // Cari barang dari list
            Barang barang = cariBarangByBarcode(barcode);
            if (barang == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Barang tidak ditemukan.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            // Input qty
            String inputQty = JOptionPane.showInputDialog(
                this,
                "Masukkan jumlah untuk " + barang.getNamaBarang() + ":",
                "Input Qty",
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (inputQty == null || inputQty.trim().isEmpty()) {
                return; // User cancel
            }
            
            int qty = Integer.parseInt(inputQty.trim());
            
            // Buat detail & tambah ke transaksi
            DetailTransaksi detail = kasirService.buatDetailDariBarang(barang, qty);
            transaksiAktif.tambahDetail(detail);
            
            // Refresh tampilan
            refreshTabelKeranjang();
            updateTotal();
            loadSemuaBarang(); // Refresh stok
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Qty harus berupa angka.",
                "Validasi Qty",
                JOptionPane.WARNING_MESSAGE
            );
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Validasi",
                JOptionPane.WARNING_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error menambah barang: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }//GEN-LAST:event_btnTambahKeranjangActionPerformed

    private void btnHapusItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusItemActionPerformed
        // TODO add your handling code here:
        int selectedRow = tblKeranjang.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Pilih item yang ingin dihapus.",
                "Validasi",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        int konfirmasi = JOptionPane.showConfirmDialog(
            this,
            "Hapus item ini dari keranjang?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (konfirmasi == JOptionPane.YES_OPTION) {
            transaksiAktif.hapusDetail(selectedRow);
            refreshTabelKeranjang();
            updateTotal();
            loadSemuaBarang(); // Refresh stok
        }
    }//GEN-LAST:event_btnHapusItemActionPerformed

    private void btnKosongkanKeranjangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKosongkanKeranjangActionPerformed
        // TODO add your handling code here:
        if (transaksiAktif.getDetailList().isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Keranjang sudah kosong.",
                "Info",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        
        int konfirmasi = JOptionPane.showConfirmDialog(
            this,
            "Kosongkan semua item di keranjang?",
            "Konfirmasi Kosongkan",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (konfirmasi == JOptionPane.YES_OPTION) {
            transaksiAktif.kosongkanDetail();
            refreshTabelKeranjang();
            updateTotal();
            loadSemuaBarang(); // Refresh stok
        }
    }//GEN-LAST:event_btnKosongkanKeranjangActionPerformed

    private void btnBayarCetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBayarCetakActionPerformed
        // TODO add your handling code here:
        if (transaksiAktif.getDetailList().isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Keranjang masih kosong. Tambahkan barang terlebih dahulu.",
                "Validasi",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        try {
            // Ambil jumlah bayar
            String bayarStr = txtBayar.getText().trim();
            if (bayarStr.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Masukkan jumlah uang pembayaran.",
                    "Validasi",
                    JOptionPane.WARNING_MESSAGE
                );
                txtBayar.requestFocus();
                return;
            }
            
            double bayar = Double.parseDouble(bayarStr);
            
            // Proses pembayaran
            int idTransaksi = kasirService.prosesPembayaran(transaksiAktif, bayar);
            
            // Tampilkan struk (sementara di dialog)
            String struk = "=================================\n"
                         + "       TOKO PARDONO\n"
                         + "=================================\n"
                         + "No. Transaksi: " + transaksiAktif.getNoTransaksi() + "\n"
                         + "Kasir: " + transaksiAktif.getNamaKasir() + "\n"
                         + "Tanggal: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) + "\n"
                         + "=================================\n\n";
            
            for (DetailTransaksi detail : transaksiAktif.getDetailList()) {
                struk += detail.getNamaBarang() + "\n"
                       + "  " + detail.getQty() + " x " + formatRupiah.format(detail.getHarga())
                       + " = " + formatRupiah.format(detail.getSubtotal()) + "\n";
            }
            
            struk += "\n=================================\n"
                   + "TOTAL: " + formatRupiah.format(transaksiAktif.getTotal()) + "\n"
                   + "BAYAR: " + formatRupiah.format(transaksiAktif.getBayar()) + "\n"
                   + "KEMBALIAN: " + formatRupiah.format(transaksiAktif.getKembalian()) + "\n"
                   + "=================================\n\n"
                   + "Terima kasih atas kunjungan Anda!\n\n"
                   + "** Cetak struk otomatis akan tersedia\n"
                   + "   pada update berikutnya. **";
            
            JOptionPane.showMessageDialog(
                this,
                struk,
                "Transaksi Berhasil",
                JOptionPane.PLAIN_MESSAGE
            );
            
            // Mulai transaksi baru
            buatTransaksiBaru();
            loadSemuaBarang(); // Refresh stok
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Jumlah bayar harus berupa angka.",
                "Validasi",
                JOptionPane.WARNING_MESSAGE
            );
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Validasi Pembayaran",
                JOptionPane.WARNING_MESSAGE
            );
            txtBayar.requestFocus();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error proses pembayaran: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }//GEN-LAST:event_btnBayarCetakActionPerformed

    private void btnCetakUlangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCetakUlangActionPerformed
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(
            this,
            "Fitur Cetak Ulang akan tersedia pada update berikutnya.",
            "Info",
            JOptionPane.INFORMATION_MESSAGE
        );
    }//GEN-LAST:event_btnCetakUlangActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        // TODO add your handling code here:
        if (transaksiAktif.getDetailList().isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Tidak ada transaksi yang sedang berjalan.",
                "Info",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        
        int konfirmasi = JOptionPane.showConfirmDialog(
            this,
            "Batalkan transaksi ini?\nSemua item di keranjang akan dihapus.",
            "Konfirmasi Batal",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (konfirmasi == JOptionPane.YES_OPTION) {
            buatTransaksiBaru();
            loadSemuaBarang();
        }
    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnTutupTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTutupTransaksiActionPerformed
        // TODO add your handling code here:
        if (!transaksiAktif.getDetailList().isEmpty()) {
            int konfirmasi = JOptionPane.showConfirmDialog(
                this,
                "Masih ada transaksi yang belum selesai.\nYakin ingin keluar?",
                "Konfirmasi Tutup",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (konfirmasi == JOptionPane.NO_OPTION) {
                return;
            }
        }
        
        this.dispose();
    }//GEN-LAST:event_btnTutupTransaksiActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
                public void run() {
                // Buat dummy user untuk testing
                model.Pengguna dummyKasir = new model.Kasir(
                    2,                      // idUser
                    "kasir1",               // username
                    "kasir123",             // password
                    "Kasir Testing",        // nama
                    "KASIR"                 // role
                );
            
                // Buka KasirForm dengan dummy user
                KasirForm kasirForm = new KasirForm(dummyKasir);
                kasirForm.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnBayarCetak;
    private javax.swing.JButton btnCetakUlang;
    private javax.swing.JButton btnClearSearch;
    private javax.swing.JButton btnHapusItem;
    private javax.swing.JButton btnKosongkanKeranjang;
    private javax.swing.JButton btnTambahKeranjang;
    private javax.swing.JButton btnTutupTransaksi;
    private javax.swing.JLabel lblBayar;
    private javax.swing.JLabel lblJudulDaftarBarang;
    private javax.swing.JLabel lblJudulKeranjang;
    private javax.swing.JLabel lblJudulPembayaran;
    private javax.swing.JLabel lblJudulTransaksi;
    private javax.swing.JLabel lblKasirTransaksi;
    private javax.swing.JLabel lblKembalian;
    private javax.swing.JLabel lblLabeltotal;
    private javax.swing.JLabel lblNilaiTotal;
    private javax.swing.JLabel lblNoTransaksi;
    private javax.swing.JLabel lblTanggalTransaksi;
    private javax.swing.JLabel lblTotalBayar;
    private javax.swing.JPanel panelBodyTransaksi;
    private javax.swing.JPanel panelDaftarBarang;
    private javax.swing.JPanel panelFieldPembayaran;
    private javax.swing.JPanel panelHeaderWrapper2;
    private javax.swing.JPanel panelInfoTransaksi;
    private javax.swing.JPanel panelJudulTransaksi;
    private javax.swing.JPanel panelKeranjang;
    private javax.swing.JPanel panelKeranjangFooter;
    private javax.swing.JPanel panelKotakSearch;
    private javax.swing.JPanel panelPembayaran;
    private javax.swing.JPanel panelSearchBarang;
    private javax.swing.JPanel panelTombolKeranjang;
    private javax.swing.JPanel panelTombolPembayaran;
    private javax.swing.JPanel panelTotalBelanja;
    private javax.swing.JScrollPane scrollDaftarBarang;
    private javax.swing.JScrollPane scrollKeranjang;
    private javax.swing.JSeparator sepJudulTransaksi;
    private javax.swing.JTable tblDataBarang;
    private javax.swing.JTable tblKeranjang;
    private javax.swing.JTextField txtBayar;
    private javax.swing.JTextField txtCariBarang;
    private javax.swing.JTextField txtKembalian;
    private javax.swing.JTextField txtTotalBayar;
    // End of variables declaration//GEN-END:variables
}
