package ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.InventoryDAO;
import model.CreateAccount;
import model.InventoryLog;
import model.InventoryOverview;
import ui.components.TopBar;
import util.FrameUtil;

public class InventoryFrame extends JFrame {
    private JPanel mainPanel;
    private CreateAccount user;
    private JTable inventoryTable;
    private JTable logsTable;
    private DefaultTableModel inventoryTableModel;
    private DefaultTableModel logsTableModel;
    private InventoryDAO inventoryDAO;
    private JLabel statusLabel;
    private JLabel incomingLabel;
    private JLabel outgoingLabel;
    private static final int LOW_STOCK_THRESHOLD = 10;
    
    // Modern colors
    private static final Color BACKGROUND = new Color(248, 250, 252);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color PRIMARY = new Color(99, 102, 241);
    private static final Color SUCCESS = new Color(16, 185, 129);
    private static final Color DANGER = new Color(239, 68, 68);
    private static final Color WARNING = new Color(245, 158, 11);
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);

    public InventoryFrame(CreateAccount user) {
        this.user = user;
        this.inventoryDAO = new InventoryDAO();
        
        setTitle("Eshopping - Inventory Management");
        setSize(1400, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initializeFrame();
        loadInventoryData();
        
        FrameUtil.addCloseConfirmation(this);
        setVisible(true);
    }

    private void initializeFrame() {
        createMainFrame();
    }

    private void createMainFrame() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        TopBar topBar = new TopBar("Eshopping", mainPanel, TopBar.Mode.INVENTORY);
        add(topBar, BorderLayout.NORTH);

        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBackground(BACKGROUND);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JPanel inventoryPanel = createInventoryPanel();
        contentPanel.add(inventoryPanel);

        JPanel logsPanel = createLogsPanel();
        contentPanel.add(logsPanel);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(BACKGROUND);

        JLabel titleLabel = new JLabel("Inventory Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        statsPanel.setBackground(BACKGROUND);
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        incomingLabel = createStatLabel("↑ Incoming: 0", SUCCESS);
        outgoingLabel = createStatLabel("↓ Outgoing: 0", DANGER);
        statusLabel = createStatLabel("Total: 0", TEXT_SECONDARY);

        statsPanel.add(incomingLabel);
        statsPanel.add(outgoingLabel);
        statsPanel.add(statusLabel);

        leftPanel.add(titleLabel);
        leftPanel.add(statsPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(BACKGROUND);

        JButton exportPngBtn = createModernButton("Export PNG", PRIMARY);
        exportPngBtn.addActionListener(e -> exportToPNG());

        JButton exportCsvBtn = createModernButton("Export CSV", PRIMARY);
        exportCsvBtn.addActionListener(e -> exportToCSV());

        JButton addStockBtn = createModernButton("Add Stock", SUCCESS);
        addStockBtn.addActionListener(e -> showAddStockDialog());

        JButton removeStockBtn = createModernButton("Remove Stock", DANGER);
        removeStockBtn.addActionListener(e -> showRemoveStockDialog());

        JButton refreshBtn = createModernButton("Refresh", PRIMARY);
        refreshBtn.addActionListener(e -> loadInventoryData());

        buttonPanel.add(exportPngBtn);
        buttonPanel.add(exportCsvBtn);
        buttonPanel.add(addStockBtn);
        buttonPanel.add(removeStockBtn);
        buttonPanel.add(refreshBtn);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JLabel createStatLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(color);
        return label;
    }

    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel("Current Inventory");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Product Name", "Stock", "Status"};
        inventoryTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        inventoryTable = new JTable(inventoryTableModel);
        styleTable(inventoryTable);

        inventoryTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                int stock = (int) table.getModel().getValueAt(row, 2);
                
                if (!isSelected) {
                    if (stock < LOW_STOCK_THRESHOLD) {
                        c.setBackground(new Color(254, 242, 242));
                        c.setForeground(new Color(127, 29, 29));
                    } else {
                        c.setBackground(CARD_BG);
                        c.setForeground(TEXT_PRIMARY);
                    }
                }
                
                if (column == 0 || column == 2 || column == 3) {
                    setHorizontalAlignment(CENTER);
                } else {
                    setHorizontalAlignment(LEFT);
                }
                
                if (column == 3) {
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                }
                
                setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(CARD_BG);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLogsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel("Recent Activity");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Type", "Product", "Quantity", "User", "Date"};
        logsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        logsTable = new JTable(logsTableModel);
        styleTable(logsTable);

        logsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String action = (String) table.getModel().getValueAt(row, 0);
                
                if (!isSelected) {
                    c.setBackground(CARD_BG);
                    if ("IN".equals(action)) {
                        c.setForeground(SUCCESS);
                    } else {
                        c.setForeground(DANGER);
                    }
                }
                
                if (column == 0 || column == 2) {
                    setHorizontalAlignment(CENTER);
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {
                    setHorizontalAlignment(LEFT);
                }
                
                setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(logsTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(CARD_BG);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(238, 242, 255));
        table.setSelectionForeground(TEXT_PRIMARY);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(249, 250, 251));
        header.setForeground(TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(226, 232, 240)));
    }

    private void exportToPNG() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Inventory as PNG");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Image", "png"));
        fileChooser.setSelectedFile(new File("inventory_report.png"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".png")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
            }

            try {
                int width = mainPanel.getWidth();
                int height = mainPanel.getHeight();
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = image.createGraphics();
                
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                mainPanel.paint(g2d);
                g2d.dispose();

                ImageIO.write(image, "png", fileToSave);
                JOptionPane.showMessageDialog(this, 
                    "Inventory exported successfully to:\n" + fileToSave.getAbsolutePath(),
                    "Export Successful", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting to PNG: " + ex.getMessage(),
                    "Export Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Inventory as CSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV File", "csv"));
        fileChooser.setSelectedFile(new File("inventory_report.csv"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }

            try (FileWriter writer = new FileWriter(fileToSave)) {
                // Write header
                writer.append("Inventory Report\n");
                writer.append("Generated: " + java.time.LocalDateTime.now().toString() + "\n\n");
                
                // Write stats
                writer.append("Statistics\n");
                writer.append(incomingLabel.getText() + "\n");
                writer.append(outgoingLabel.getText() + "\n");
                writer.append(statusLabel.getText() + "\n\n");
                
                // Write inventory table
                writer.append("Current Inventory\n");
                for (int i = 0; i < inventoryTableModel.getColumnCount(); i++) {
                    writer.append(inventoryTableModel.getColumnName(i));
                    if (i < inventoryTableModel.getColumnCount() - 1) {
                        writer.append(",");
                    }
                }
                writer.append("\n");

                for (int i = 0; i < inventoryTableModel.getRowCount(); i++) {
                    for (int j = 0; j < inventoryTableModel.getColumnCount(); j++) {
                        Object value = inventoryTableModel.getValueAt(i, j);
                        String cellValue = value != null ? value.toString() : "";
                        // Escape commas and quotes in CSV
                        if (cellValue.contains(",") || cellValue.contains("\"")) {
                            cellValue = "\"" + cellValue.replace("\"", "\"\"") + "\"";
                        }
                        writer.append(cellValue);
                        if (j < inventoryTableModel.getColumnCount() - 1) {
                            writer.append(",");
                        }
                    }
                    writer.append("\n");
                }

                // Write logs table
                writer.append("\nRecent Activity\n");
                for (int i = 0; i < logsTableModel.getColumnCount(); i++) {
                    writer.append(logsTableModel.getColumnName(i));
                    if (i < logsTableModel.getColumnCount() - 1) {
                        writer.append(",");
                    }
                }
                writer.append("\n");

                for (int i = 0; i < logsTableModel.getRowCount(); i++) {
                    for (int j = 0; j < logsTableModel.getColumnCount(); j++) {
                        Object value = logsTableModel.getValueAt(i, j);
                        String cellValue = value != null ? value.toString() : "";
                        if (cellValue.contains(",") || cellValue.contains("\"")) {
                            cellValue = "\"" + cellValue.replace("\"", "\"\"") + "\"";
                        }
                        writer.append(cellValue);
                        if (j < logsTableModel.getColumnCount() - 1) {
                            writer.append(",");
                        }
                    }
                    writer.append("\n");
                }

                JOptionPane.showMessageDialog(this, 
                    "Inventory exported successfully to:\n" + fileToSave.getAbsolutePath(),
                    "Export Successful", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting to CSV: " + ex.getMessage(),
                    "Export Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadInventoryData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            List<InventoryOverview> inventory;
            List<InventoryLog> logs;
            InventoryDAO.InventoryStats stats;

            @Override
            protected Void doInBackground() {
                inventory = inventoryDAO.getInventoryOverview();
                logs = inventoryDAO.getAllInventoryLogs();
                stats = inventoryDAO.getInventoryStats();
                return null;
            }

            @Override
            protected void done() {
                // UPDATE INVETORY
                inventoryTableModel.setRowCount(0);
                int lowStockCount = 0;

                for (InventoryOverview item : inventory) {
                    String status = item.isLowStock(LOW_STOCK_THRESHOLD) ? "Low Stock" : "In Stock";
                    if (item.isLowStock(LOW_STOCK_THRESHOLD)) {
                        lowStockCount++;
                    }

                    inventoryTableModel.addRow(new Object[]{
                        item.getProductId(),
                        item.getName(),
                        item.getStock(),
                        status
                    });
                }

                // UPDATE LOGS TABLE
                logsTableModel.setRowCount(0);
                for (InventoryLog log : logs) {
                    logsTableModel.addRow(new Object[]{
                        log.getAction(),
                        log.getProductName(),
                        log.getChangeQty(),
                        log.getUsername() != null ? log.getUsername() : "System",
                        log.getFormattedDate()
                    });
                }

                // Update stats
                incomingLabel.setText("↑ Incoming: " + stats.totalIncoming);
                outgoingLabel.setText("↓ Outgoing: " + stats.totalOutgoing);
                statusLabel.setText("Total Items: " + inventory.size() + " | Low Stock: " + lowStockCount);
                
                if (lowStockCount > 0) {
                    statusLabel.setForeground(DANGER);
                } else {
                    statusLabel.setForeground(TEXT_SECONDARY);
                }
            }
        };

        worker.execute();
    }

    private void showAddStockDialog() {
        JDialog dialog = new JDialog(this, "Add Stock", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(15, 15));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Product ID:"));
        JTextField productIdField = new JTextField();
        formPanel.add(productIdField);

        formPanel.add(new JLabel("Quantity:"));
        JTextField quantityField = new JTextField();
        formPanel.add(quantityField);

        formPanel.add(new JLabel("Reason:"));
        JTextField reasonField = new JTextField();
        formPanel.add(reasonField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = createModernButton("Add Stock", SUCCESS);
        JButton cancelBtn = createModernButton("Cancel", TEXT_SECONDARY);

        saveBtn.addActionListener(e -> {
            try {
                int productId = Integer.parseInt(productIdField.getText());
                int quantity = Integer.parseInt(quantityField.getText());
                String reason = reasonField.getText();

                if (inventoryDAO.addStock(productId, quantity, user.getUserId().intValue(), reason)) {
                    JOptionPane.showMessageDialog(dialog, "Stock added successfully!");
                    dialog.dispose();
                    loadInventoryData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add stock", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showRemoveStockDialog() {
        JDialog dialog = new JDialog(this, "Remove Stock", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(15, 15));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Product ID:"));
        JTextField productIdField = new JTextField();
        formPanel.add(productIdField);

        formPanel.add(new JLabel("Quantity:"));
        JTextField quantityField = new JTextField();
        formPanel.add(quantityField);

        formPanel.add(new JLabel("Reason:"));
        JTextField reasonField = new JTextField();
        formPanel.add(reasonField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = createModernButton("Remove Stock", DANGER);
        JButton cancelBtn = createModernButton("Cancel", TEXT_SECONDARY);

        saveBtn.addActionListener(e -> {
            try {
                int productId = Integer.parseInt(productIdField.getText());
                int quantity = Integer.parseInt(quantityField.getText());
                String reason = reasonField.getText();

                if (inventoryDAO.removeStock(productId, quantity, user.getUserId().intValue(), reason)) {
                    JOptionPane.showMessageDialog(dialog, "Stock removed successfully!");
                    dialog.dispose();
                    loadInventoryData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to remove stock (insufficient quantity?)", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        Color hoverColor = bgColor.darker();
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
}