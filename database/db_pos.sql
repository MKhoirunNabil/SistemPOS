-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 12, 2026 at 10:13 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_pos`
--

-- --------------------------------------------------------

--
-- Table structure for table `barang`
--

CREATE TABLE `barang` (
  `id_barang` int(11) NOT NULL,
  `barcode` varchar(50) NOT NULL,
  `nama_barang` varchar(100) NOT NULL,
  `kategori` varchar(50) NOT NULL,
  `harga_beli` decimal(10,2) NOT NULL DEFAULT 0.00,
  `harga_jual` decimal(10,2) NOT NULL DEFAULT 0.00,
  `stok` int(11) NOT NULL DEFAULT 0,
  `satuan` varchar(20) NOT NULL DEFAULT 'Pcs',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `barang`
--

INSERT INTO `barang` (`id_barang`, `barcode`, `nama_barang`, `kategori`, `harga_beli`, `harga_jual`, `stok`, `satuan`, `created_at`) VALUES
(1, '8991001010101', 'Indomie Goreng', 'Makanan', 2500.00, 3000.00, 0, 'Pcs', '2026-07-08 15:26:33'),
(2, '8992761010102', 'Aqua 600ml', 'Minuman', 2000.00, 3000.00, 8, 'Pcs', '2026-07-08 15:26:33'),
(3, '8996001010103', 'Teh Pucuk Harum', 'Minuman', 3000.00, 4000.00, 40, 'Pcs', '2026-07-08 15:26:33'),
(4, '8997011010104', 'Mie Sedaap Goreng', 'Makanan', 2300.00, 3000.00, 80, 'Pcs', '2026-07-08 15:26:33'),
(5, '8998021010105', 'Beng Beng', 'Snack', 1500.00, 2000.00, 114, 'Pcs', '2026-07-08 15:26:33'),
(6, '8999031010106', 'Tango Wafer', 'Snack', 800.00, 1000.00, 190, 'Pcs', '2026-07-08 15:26:33'),
(7, '8991041010107', 'Kopi Kapal Api', 'Minuman', 1200.00, 1500.00, 138, 'Sachet', '2026-07-08 15:26:33'),
(8, '8992051010108', 'Gula Pasir 1kg', 'Sembako', 12000.00, 15000.00, 17, 'Kg', '2026-07-08 15:26:33'),
(9, '8993061010109', 'Minyak Goreng 1L', 'Sembako', 14000.00, 16000.00, 25, 'Liter', '2026-07-08 15:26:33'),
(10, '8994071010110', 'Sabun Mandi Lifebuoy', 'Toiletries', 3000.00, 4000.00, 59, 'Pcs', '2026-07-08 15:26:33');

-- --------------------------------------------------------

--
-- Table structure for table `detail_transaksi`
--

CREATE TABLE `detail_transaksi` (
  `id_detail` int(11) NOT NULL,
  `id_transaksi` int(11) NOT NULL,
  `id_barang` int(11) NOT NULL,
  `harga` decimal(10,2) NOT NULL DEFAULT 0.00,
  `qty` int(11) NOT NULL DEFAULT 1,
  `subtotal` decimal(12,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `detail_transaksi`
--

INSERT INTO `detail_transaksi` (`id_detail`, `id_transaksi`, `id_barang`, `harga`, `qty`, `subtotal`) VALUES
(1, 1, 7, 1500.00, 12, 18000.00),
(2, 1, 1, 3000.00, 15, 45000.00),
(3, 2, 2, 3000.00, 14, 42000.00),
(4, 2, 1, 3000.00, 85, 255000.00),
(5, 3, 2, 3000.00, 12, 36000.00),
(6, 3, 8, 15000.00, 13, 195000.00),
(7, 4, 2, 3000.00, 3, 9000.00),
(8, 4, 5, 2000.00, 6, 12000.00),
(9, 5, 2, 3000.00, 1, 3000.00),
(10, 6, 2, 3000.00, 12, 36000.00),
(11, 7, 6, 1000.00, 10, 10000.00),
(12, 7, 10, 4000.00, 1, 4000.00);

-- --------------------------------------------------------

--
-- Table structure for table `transaksi`
--

CREATE TABLE `transaksi` (
  `id_transaksi` int(11) NOT NULL,
  `tanggal` datetime NOT NULL,
  `id_user` int(11) NOT NULL,
  `total` decimal(12,2) NOT NULL DEFAULT 0.00,
  `bayar` decimal(12,2) NOT NULL DEFAULT 0.00,
  `kembalian` decimal(12,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transaksi`
--

INSERT INTO `transaksi` (`id_transaksi`, `tanggal`, `id_user`, `total`, `bayar`, `kembalian`) VALUES
(1, '2026-07-12 14:24:14', 1, 63000.00, 100000.00, 37000.00),
(2, '2026-07-12 14:26:05', 1, 297000.00, 300000.00, 3000.00),
(3, '2026-07-12 14:27:30', 1, 231000.00, 250000.00, 19000.00),
(4, '2026-07-12 14:37:07', 1, 21000.00, 25000.00, 4000.00),
(5, '2026-07-12 14:39:31', 2, 3000.00, 5000.00, 2000.00),
(6, '2026-07-12 14:49:41', 1, 36000.00, 40000.00, 4000.00),
(7, '2026-07-12 14:57:50', 1, 14000.00, 15000.00, 1000.00);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id_user` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `role` enum('Admin','Kasir') NOT NULL DEFAULT 'Kasir',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id_user`, `username`, `password`, `nama`, `role`, `created_at`) VALUES
(1, 'admin', 'admin123', 'Administrator', 'Admin', '2026-07-08 15:26:33'),
(2, 'kasir1', 'kasir123', 'Kasir Pardono', 'Kasir', '2026-07-08 15:26:33');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `barang`
--
ALTER TABLE `barang`
  ADD PRIMARY KEY (`id_barang`),
  ADD UNIQUE KEY `barcode` (`barcode`);

--
-- Indexes for table `detail_transaksi`
--
ALTER TABLE `detail_transaksi`
  ADD PRIMARY KEY (`id_detail`),
  ADD KEY `id_transaksi` (`id_transaksi`),
  ADD KEY `id_barang` (`id_barang`);

--
-- Indexes for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD PRIMARY KEY (`id_transaksi`),
  ADD KEY `id_user` (`id_user`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `barang`
--
ALTER TABLE `barang`
  MODIFY `id_barang` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `detail_transaksi`
--
ALTER TABLE `detail_transaksi`
  MODIFY `id_detail` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `transaksi`
--
ALTER TABLE `transaksi`
  MODIFY `id_transaksi` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `detail_transaksi`
--
ALTER TABLE `detail_transaksi`
  ADD CONSTRAINT `detail_transaksi_ibfk_1` FOREIGN KEY (`id_transaksi`) REFERENCES `transaksi` (`id_transaksi`) ON DELETE CASCADE,
  ADD CONSTRAINT `detail_transaksi_ibfk_2` FOREIGN KEY (`id_barang`) REFERENCES `barang` (`id_barang`) ON DELETE CASCADE;

--
-- Constraints for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD CONSTRAINT `transaksi_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
