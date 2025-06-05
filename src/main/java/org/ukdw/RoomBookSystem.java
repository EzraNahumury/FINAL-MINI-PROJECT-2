package org.ukdw;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.ukdw.data.Gedung;
import org.ukdw.data.Pemesanan;
import org.ukdw.data.Ruangan;
import org.ukdw.managers.*;
import org.ukdw.utils.DBConnectionManager;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static javafx.application.Application.launch;

public class RoomBookSystem{
    private UserManager userManager;
    private boolean isLogin = false;
    private GedungManager gedungManager;
    private RuanganManager ruanganManager;
    private PemesananManager pemesananManager;

    public RoomBookSystem() {
        userManager = new UserManager(DBConnectionManager.getConnection());
        gedungManager = new GedungManager(DBConnectionManager.getConnection());
        ruanganManager = new RuanganManager(DBConnectionManager.getConnection());
        pemesananManager = new PemesananManager(DBConnectionManager.getConnection());
    }

    private void start() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Selamat datang di System pemesanan ruangan UKDW");
            System.out.println("0. Exit");
            if (isLogin) {
                System.out.println("2. Registrasi User");
                System.out.println("3. Lihat Semua Data Gedung");
                System.out.println("4. Tambah Data Gedung");
                System.out.println("5. Ubah Data Gedung");
                System.out.println("6. Hapus Data Gedung");
                System.out.println("7. Lihat Semua Data Ruangan");
                System.out.println("8. Tambah Data Ruangan");
                System.out.println("9. Ubah Data Ruangan");
                System.out.println("10. Hapus Data Ruangan");
                System.out.println("11. Lihat Semua Pesanan Ruangan");
                System.out.println("12. Tambah Pemesanan Ruangan");
                System.out.println("13. Ubah Pemesanan Ruangan");
                System.out.println("14. Hapus Pemesanan Ruangan");
                System.out.println("15. Logout");
            } else {
                System.out.println("1. Login");
            }

            System.out.print("Silahkan menentukan pilihan: ");
            int choice = scanner.nextInt();
            if (!isLogin) {
                if (choice > 1) {
                    choice = -99;
                }
            }
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 0:
                    exitApps();
                    return;
                case 1:
                    login(scanner);
                    break;
                case 2:
                    registerUser(scanner);
                    break;
                case 3:
                    lihatSemuaDataGedung();
                    break;
                case 4:
                    addGedung(scanner);
                    break;
                case 5:
                    editGedung(scanner);
                    break;
                case 6:
                    deleteGedung(scanner);
                    break;
                case 7:
                    lihatSemuaDataRuangan();
                    break;
                case 8:
                    addRuangan(scanner);
                    break;
                case 9:
                    editRuangan(scanner);
                    break;
                case 10:
                    deleteRuangan(scanner);
                    break;
                case 11:
                    lihatSemuaPemesanan();
                    break;
                case 12:
                    addPemesanan(scanner);
                    break;
                case 13:
                    editPemesanan(scanner);
                    break;
                case 14:
                    deletePemesanan(scanner);
                    break;
                case 15:
                    isLogin = false;
                    break;
                case -99:
                    System.out.println("Anda Belum Login.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void exitApps() {
        System.out.println("Keluar aplikasi. Goodbye!");
        System.exit(0);
    }

    private void login(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (userManager.authenticateUser(username, password)) {
            System.out.println("Login successful!");
            isLogin = true;
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    private void registerUser(Scanner scanner) {
        System.out.print("Masukan email: ");
        String email = scanner.nextLine();
        System.out.print("Masukan username: ");
        String username = scanner.nextLine();
        System.out.print("Masukan password: ");
        String password = scanner.nextLine();

        if (userManager.registerUser(email, username, password)) {
            System.out.println("Registration successful!");
        } else {
            System.out.println("Registration failed. Please try again.");
        }
    }

    private void lihatSemuaDataGedung() {
        List<Gedung> gedungs = gedungManager.getAllGedung();
        if (gedungs.isEmpty()) {
            System.out.println("Data gedung kosong.");
        } else {
            System.out.println("daftar Gedung:");
            for (Gedung gedung : gedungs) {
                System.out.println("Id : " + gedung.getId()
                        + ", Nama : " + gedung.getNama()
                        + ", Alamat :  " + gedung.getAlamat());
            }
        }
    }

    private void addGedung(Scanner scanner) {
        System.out.print("Masukan nama gedung: ");
        String nama = scanner.nextLine();
        System.out.print("Masukan alamat gedung: ");
        String alamatGedung = scanner.nextLine();

        if (gedungManager.addGedung(nama, alamatGedung)) {
            System.out.println("Data Gedung added successfully!");
        } else {
            System.out.println("Failed to add data Gedung. Please try again.");
        }
    }

    private void editGedung(Scanner scanner) {
        System.out.print("Masukan ID Gedung yang akan diubah: ");
        int idGedung = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Masukan nama Gedung yang baru: ");
        String newNama = scanner.nextLine();
        System.out.print("Masukan alamat Gedung yang baru: ");
        String newAlamat = scanner.nextLine();

        if (gedungManager.editGedung(idGedung, newNama, newAlamat)) {
            System.out.println("Gedung edited successfully!");
        } else {
            System.out.println("Failed to edit Gedung. Please check the gedung ID and try again.");
        }
    }

    private void deleteGedung(Scanner scanner) {
        System.out.print("Masukan ID Gedung yang akan didelete: ");
        int gedungId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (gedungManager.deleteGedung(gedungId)) {
            System.out.println("Gedung deleted successfully!");
        } else {
            System.out.println("Failed to delete Gedung. Please check the Gedung ID and try again.");
        }
    }

    private void lihatSemuaDataRuangan() {
        List<Ruangan> ruangans = ruanganManager.getAllRuangan();
        if (ruangans.isEmpty()) {
            System.out.println("Data ruangan kosong.");
        } else {
            System.out.println("daftar ruangan:");
            for (Ruangan ruangan : ruangans) {
                System.out.println("Id : " + ruangan.getId()
                        + ", Nama : " + ruangan.getName()
                        + ", id gedung :  " + ruangan.getIdGedung());
            }
        }
    }

    private void addRuangan(Scanner scanner) {
        System.out.print("Masukan nama ruangan: ");
        String nama = scanner.nextLine();
        System.out.print("Masukan id gedung : ");
        int idGedung = Integer.parseInt(scanner.nextLine());
        if (ruanganManager.addRuangan(nama, idGedung)) {
            System.out.println("Data Ruangan added successfully!");
        } else {
            System.out.println("Failed to add data Ruangan. Please try again.");
        }
    }

    private void editRuangan(Scanner scanner) {
        System.out.print("Masukan ID Ruangan yang akan diubah: ");
        int idRuangan = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Masukan nama Ruangan yang baru: ");
        String newNama = scanner.nextLine();
        System.out.print("Masukan id Gedung yang baru: ");
        int newIdGedung = Integer.parseInt(scanner.nextLine());

        if (ruanganManager.editRuangan(newNama, newIdGedung, idRuangan)) {
            System.out.println("Ruangan edited successfully!");
        } else {
            System.out.println("Failed to edit Ruangan. Please check the ruangan ID and try again.");
        }
    }

    private void deleteRuangan(Scanner scanner) {
        System.out.print("Masukan ID ruangan yang akan didelete: ");
        int ruanganId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        if (ruanganManager.deleteRuangan(ruanganId)) {
            System.out.println("Ruangan deleted successfully!");
        } else {
            System.out.println("Failed to delete Gedung. Please check the Gedung ID and try again.");
        }
    }

    private void lihatSemuaPemesanan() {
        List<Pemesanan> pemesanans = pemesananManager.getAllPemesanan();
        if (pemesanans.isEmpty()) {
            System.out.println("Data pemesanan kosong.");
        } else {
            System.out.println("daftar pemesanan:");
            for (Pemesanan pemesanan : pemesanans) {
                System.out.println("Id : " + pemesanan.getId()
                        + ", Pemesan : " + pemesanan.getUserEmail()
                        + ", id Ruangan :  " + pemesanan.getIdRuangan()
                        + ", Tanggal mulai :  " + pemesanan.getCheckInDate()
                        + ", Jam  mulai:  " + pemesanan.getCheckInTime()
                        + ", Tanggal Selesai :  " + pemesanan.getCheckOutDate()
                        + ", Jam Selesai :  " + pemesanan.getCheckOutTime()
                );
            }
        }
    }

    private void addPemesanan(Scanner scanner) {
        System.out.print("Masukan email pemesan: ");
        String emailPemesan = scanner.nextLine();
        System.out.print("Masukan id ruangan : ");
        int idRuangan = Integer.parseInt(scanner.nextLine());
        System.out.print("Masukan Tanggal mulai: ");
        String checkInDate = scanner.nextLine();
        System.out.print("Masukan Waktu mulai: ");
        String checkInTime = scanner.nextLine();
        System.out.print("Masukan Tanggal selesai: ");
        String checkoutDate = scanner.nextLine();
        System.out.print("Masukan jam selesai: ");
        String checkoutTime = scanner.nextLine();
        if (pemesananManager.addPemesanan(emailPemesan, idRuangan, checkInDate, checkoutDate, checkInTime, checkoutTime)) {
            System.out.println("Data Pesanan ruangan added successfully!");
        } else {
            System.out.println("Failed to add data pesanan Ruangan. Please try again.");
        }
    }

    private void editPemesanan(Scanner scanner) {
        System.out.print("Masukan ID Pemesanan yang akan diubah: ");
        int idPemesanan = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Masukan email pemesan yang baru: ");
        String newEmailPemesan = scanner.nextLine();
        System.out.print("Masukan id ruangan yang baru : ");
        int newidRuangan = Integer.parseInt(scanner.nextLine());
        System.out.print("Masukan Tanggal mulai yang baru: ");
        String newcheckInDate = scanner.nextLine();
        System.out.print("Masukan Waktu mulai yang baru: ");
        String newcheckInTime = scanner.nextLine();
        System.out.print("Masukan Tanggal selesai yang baru: ");
        String newcheckoutDate = scanner.nextLine();
        System.out.print("Masukan jam selesai yang baru: ");
        String newcheckoutTime = scanner.nextLine();
        if (pemesananManager.editPemesanan(idPemesanan, newEmailPemesan, newidRuangan, newcheckInDate, newcheckoutDate, newcheckInTime, newcheckoutTime)) {
            System.out.println("Pemesanan Ruangan edited successfully!");
        } else {
            System.out.println("Failed to edit Pemesanan Ruangan.");
        }
    }

    private void deletePemesanan(Scanner scanner) {
        System.out.print("Masukan ID Pemesanan yang akan didelete: ");
        int idPemesanan = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        if (pemesananManager.deletePemesanan(idPemesanan)) {
            System.out.println("Pemesanan deleted successfully!");
        } else {
            System.out.println("Failed to delete pemesanan. Please check the pemesanan ID and try again.");
        }
    }

    public static void main(String[] args) {
//        RoomBookSystem roomBookSystem = new RoomBookSystem();
////        DBConnectionManager.createTables();
//        roomBookSystem.start();
        launch();
    }
}
