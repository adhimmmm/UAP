package org.example.uap;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class VendingMachineApp {
    private JFrame frame;
    private JTable itemTable;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private DefaultTableModel historyTableModel;
    private List<Item> items;
    private List<PurchaseHistory> purchaseHistories;
    private JTextField moneyField;
    private JLabel feedbackLabel;

    private final String adminUsername = "admin";
    private final String adminPassword = "password";

    public VendingMachineApp() {
        items = new ArrayList<>();
        purchaseHistories = new ArrayList<>();

        // Tambahkan beberapa item default
        items.add(new Item("Coklat", 5000, 10, "images/coklat.png"));
        items.add(new Item("Keripik", 7000, 15, "images/keripik.png"));
        items.add(new Item("Minuman", 3000, 20, "images/minuman.png"));

        frame = new JFrame("Vending Machine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);

        showUserPanel();

        frame.setVisible(true);
    }

    // Halaman pembeli
    private void showUserPanel() {
        JPanel userPanel = new JPanel(new BorderLayout());

        // Tabel untuk daftar barang
        tableModel = new DefaultTableModel(new String[]{"Nama", "Harga", "Stok", "Gambar"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 3 ? ImageIcon.class : String.class;
            }
        };
        itemTable = new JTable(tableModel);
        updateTable();

        JScrollPane tableScrollPane = new JScrollPane(itemTable);

        // Panel bawah untuk pembelian
        JPanel purchasePanel = new JPanel(new FlowLayout());
        JLabel moneyLabel = new JLabel("Masukkan uang Anda: ");
        moneyField = new JTextField(10);
        JButton buyButton = new JButton("Beli");
        feedbackLabel = new JLabel(" ");

        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String moneyInput = moneyField.getText();
                    if (moneyInput.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Masukkan jumlah uang yang valid!", "Message", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    int money = Integer.parseInt(moneyInput);
                    int selectedRow = itemTable.getSelectedRow();

                    if (selectedRow == -1) {
                        JOptionPane.showMessageDialog(frame, "Silahkan Pilih item yang ingin dibeli!", "Message", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    Item selectedItem = items.get(selectedRow);

                    if (money >= selectedItem.getPrice() && selectedItem.getStock() > 0) {
                        selectedItem.setStock(selectedItem.getStock() - 1);
                        updateTable();
                        JOptionPane.showMessageDialog(frame, "Berhasil membeli " + selectedItem.getName() + ". Kembalian: Rp " + (money - selectedItem.getPrice()), "Message", JOptionPane.INFORMATION_MESSAGE);

                        // Tambahkan ke historis pembelian
                        purchaseHistories.add(new PurchaseHistory(selectedItem.getName(), selectedItem.getPrice(), 1));
                        updateHistoryTable();
                    } else if (selectedItem.getStock() <= 0) {
                        JOptionPane.showMessageDialog(frame, "Stok barang habis!", "Message", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Uang tidak cukup!", "Message", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Masukkan jumlah uang yang valid!", "Message", JOptionPane.INFORMATION_MESSAGE);
                } finally {
                    // Kosongkan field setelah pembelian selesai
                    moneyField.setText("");
                }
            }
        });


        // Tombol untuk admin
        JButton adminButton = new JButton("Admin");
        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLoginPanel();
            }
        });

        purchasePanel.add(moneyLabel);
        purchasePanel.add(moneyField);
        purchasePanel.add(buyButton);
        purchasePanel.add(adminButton);
        purchasePanel.add(feedbackLabel);

        // Tabel historis pembelian
        historyTableModel = new DefaultTableModel(new String[]{"Nama", "Harga", "Jumlah"}, 0);
        historyTable = new JTable(historyTableModel);
        JScrollPane historyScrollPane = new JScrollPane(historyTable);
        historyScrollPane.setBorder(BorderFactory.createTitledBorder("Historis Pembelian"));

        userPanel.add(tableScrollPane, BorderLayout.CENTER);
        userPanel.add(purchasePanel, BorderLayout.SOUTH);
        userPanel.add(historyScrollPane, BorderLayout.EAST);

        frame.setContentPane(userPanel);
        frame.revalidate();
        frame.repaint();
    }

    // Halaman login admin
    private void showLoginPanel() {
        JPanel loginPanel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Admin Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Kembali");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.equals(adminUsername) && password.equals(adminPassword)) {
                    showAdminPanel();
                } else {
                    JOptionPane.showMessageDialog(frame, "Username atau password salah!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUserPanel();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(loginButton, gbc);

        gbc.gridx = 1;
        formPanel.add(backButton, gbc);

        loginPanel.add(formPanel, BorderLayout.CENTER);

        frame.setContentPane(loginPanel);
        frame.revalidate();
        frame.repaint();
    }

    // Halaman admin
    private void showAdminPanel() {
        JPanel adminPanel = new JPanel(new BorderLayout());

        // Tabel daftar barang
        tableModel = new DefaultTableModel(new String[]{"Nama", "Harga", "Stok", "Gambar"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 3 ? ImageIcon.class : String.class;
            }
        };
        itemTable = new JTable(tableModel);
        updateTable();
        JScrollPane tableScrollPane = new JScrollPane(itemTable);

        // Panel form untuk CRUD
        JPanel formPanel = new JPanel(new GridLayout(9, 2));
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField stockField = new JTextField();
        JTextField imageField = new JTextField();
        JButton browseButton = new JButton("Pilih Gambar");

        JButton addButton = new JButton("Tambah");
        addButton.setBackground(Color.GREEN);
        JButton updateButton = new JButton("Ubah");
        updateButton.setBackground(Color.BLUE);
        JButton deleteButton = new JButton("Hapus");
        deleteButton.setBackground(Color.RED);
        JButton backButton = new JButton("Kembali");

        formPanel.add(new JLabel("Nama"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Harga"));
        formPanel.add(priceField);
        formPanel.add(new JLabel("Stok"));
        formPanel.add(stockField);
        formPanel.add(new JLabel("Gambar"));
        formPanel.add(imageField);
        formPanel.add(new JLabel(" "));
        formPanel.add(browseButton);
        formPanel.add(addButton);
        formPanel.add(updateButton);
        formPanel.add(deleteButton);
        formPanel.add(backButton);

        // Tombol untuk memilih gambar
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // Hanya izinkan file, bukan folder
                int returnValue = fileChooser.showOpenDialog(frame);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    String selectedFilePath = fileChooser.getSelectedFile().getAbsolutePath();

                    try {
                        // Validasi ekstensi file gambar
                        if (isValidImageFile(selectedFilePath)) {
                            imageField.setText(selectedFilePath);
                        } else {
                            throw new IllegalArgumentException("File yang dipilih bukan file gambar yang valid. Harap pilih file dengan ekstensi .png, .jpg, atau .jpeg.");
                        }
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(frame,
                                ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Tombol tambah item
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = nameField.getText();
                    int price = Integer.parseInt(priceField.getText());
                    int stock = Integer.parseInt(stockField.getText());
                    String imagePath = imageField.getText();

                    if (name.isEmpty() || imagePath.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Nama dan gambar tidak boleh kosong!");
                        return;
                    }

                    items.add(new Item(name, price, stock, imagePath));
                    updateTable();
                    JOptionPane.showMessageDialog(frame, "Item berhasil ditambahkan!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Masukkan angka yang valid untuk harga dan stok!");
                }
            }
        });

        // Tombol ubah item
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = itemTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(frame, "Pilih item yang ingin diubah!");
                    return;
                }

                try {
                    String name = nameField.getText();
                    int price = Integer.parseInt(priceField.getText());
                    int stock = Integer.parseInt(stockField.getText());
                    String imagePath = imageField.getText();

                    if (name.isEmpty() || imagePath.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Nama dan gambar tidak boleh kosong!");
                        return;
                    }

                    Item selectedItem = items.get(selectedRow);
                    selectedItem.setName(name);
                    selectedItem.setPrice(price);
                    selectedItem.setStock(stock);
                    selectedItem.setImagePath(imagePath);
                    updateTable();
                    JOptionPane.showMessageDialog(frame, "Item berhasil diubah!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Masukkan angka yang valid untuk harga dan stok!");
                }
            }
        });

        // Tombol hapus item
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = itemTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(frame, "Pilih item yang ingin dihapus!");
                    return;
                }

                items.remove(selectedRow);
                updateTable();
                JOptionPane.showMessageDialog(frame, "Item berhasil dihapus!");
            }
        });

        // Tombol kembali ke halaman user
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUserPanel();
            }
        });

        adminPanel.add(tableScrollPane, BorderLayout.CENTER);
        adminPanel.add(formPanel, BorderLayout.SOUTH);

        frame.setContentPane(adminPanel);
        frame.revalidate();
        frame.repaint();
    }

    // Fungsi untuk memvalidasi ekstensi file gambar
    private boolean isValidImageFile(String filePath) {
        String[] validExtensions = {".png", ".jpg", ".jpeg"};
        for (String ext : validExtensions) {
            if (filePath.toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    // Memperbarui tabel barang
    private void updateTable() {
        tableModel.setRowCount(0);
        for (Item item : items) {
            try {
                ImageIcon imageIcon = new ImageIcon(item.getImagePath());
                Image image = imageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                tableModel.addRow(new Object[]{
                        item.getName(),
                        item.getPrice(),
                        item.getStock(),
                        new ImageIcon(image)
                });
            } catch (Exception e) {
                tableModel.addRow(new Object[]{
                        item.getName(),
                        item.getPrice(),
                        item.getStock(),
                        new ImageIcon(new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB))
                });
            }
        }
    }

    // Memperbarui tabel historis pembelian
    private void updateHistoryTable() {
        historyTableModel.setRowCount(0);
        for (PurchaseHistory history : purchaseHistories) {
            historyTableModel.addRow(new Object[]{history.getName(), history.getPrice(), history.getQuantity()});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new VendingMachineApp();
            }
        });
    }

    // Kelas item
    static class Item {
        private String name;
        private int price;
        private int stock;
        private String imagePath;

        public Item(String name, int price, int stock, String imagePath) {
            this.name = name;
            this.price = price;
            this.stock = stock;
            this.imagePath = imagePath;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public int getStock() {
            return stock;
        }

        public void setStock(int stock) {
            this.stock = stock;
        }

        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }
    }

    // Kelas untuk menyimpan data historis pembelian
    static class PurchaseHistory {
        private String name;
        private int price;
        private int quantity;

        public PurchaseHistory(String name, int price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        public String getName() {
            return name;
        }

        public int getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}