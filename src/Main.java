import java.util.List;
import model.Barang;
import model.ItemLaporan;
import service.BarangService;
import service.LaporanService;
import config.DatabaseConfig;

/**
 * Main class untuk testing backend
 * (Ini hanya untuk testing, bukan entry point sesungguhnya)
 * 
 * @author MKN
 */
public class Main {
    
    /**
     * Method main - entry point program
     */
    public static void main(String[] args) {
        System.out.println("=== TEST BACKEND SISTEM POS TOKO PARDONO ===\n");
        
        // 1. Test Koneksi Database
        testKoneksiDatabase();
        
        // 2. Test Barang Service
        testBarangService();
        
        // 3. Test Laporan Service
        testLaporanService();
    }
    
    /**
     * Test koneksi ke database
     */
    private static void testKoneksiDatabase() {
        System.out.println("1. Test Koneksi Database:");
        if (DatabaseConfig.testConnection()) {
            System.out.println("   ✓ Koneksi berhasil!\n");
        } else {
            System.out.println("   ✗ Koneksi gagal!\n");
        }
    }
    
    /**
     * Test BarangService - ambil semua barang
     */
    private static void testBarangService() {
        System.out.println("2. Test Barang Service:");
        BarangService barangService = new BarangService();

        try {
            // Test ambil semua barang
            List<Barang> list = barangService.ambilSemuaBarang();
            System.out.println("   ✓ Total barang: " + list.size());
            
            // Tampilkan 3 barang pertama
            if (!list.isEmpty()) {
                System.out.println("   ✓ Contoh barang:");
                for (int i = 0; i < Math.min(3, list.size()); i++) {
                    Barang b = list.get(i);
                    System.out.println("      - " + b.getNamaBarang() + 
                                     " | Stok: " + b.getStok() + 
                                     " | Harga: Rp " + b.getHargaJual());
                }
            }
            System.out.println();
            
        } catch (Exception e) {
            System.out.println("   ✗ Error: " + e.getMessage() + "\n");
        }
    }
    
    /**
     * Test LaporanService - ringkasan dan barang terlaris
     */
    private static void testLaporanService() {
        System.out.println("3. Test Laporan Service:");
        LaporanService laporanService = new LaporanService();

        try {
            // Hari ini
            java.util.Date today = new java.util.Date();

            // Test ringkasan
            double[] ringkasan = laporanService.getRingkasan(today, today);
            System.out.println("   ✓ Transaksi hari ini: " + (int)ringkasan[0]);
            System.out.println("   ✓ Total penjualan: Rp " + ringkasan[1]);

            // Test barang terlaris
            List<ItemLaporan> terlaris = laporanService.getBarangTerlaris(today, today);
            System.out.println("   ✓ Barang terlaris hari ini: " + terlaris.size() + " item");
            
            if (!terlaris.isEmpty()) {
                System.out.println("   ✓ Top 3 Terlaris:");
                for (int i = 0; i < Math.min(3, terlaris.size()); i++) {
                    ItemLaporan item = terlaris.get(i);
                    System.out.println("      " + (i+1) + ". " + item.getNamaBarang() + 
                                     " | Qty: " + item.getTotalQtyTerjual() + 
                                     " | Total: Rp " + item.getTotalPenjualan());
                }
            }
            System.out.println();

        } catch (Exception e) {
            System.out.println("   ✗ Error: " + e.getMessage() + "\n");
        }
    }
}