package ui;

import dao.*;
import model.*;
import resources.colors.ProductColors;
import ui.components.TopBar;
import util.FrameUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class ProductFrame extends JFrame {

    // Components
    private JTable table;
    private DefaultTableModel model;
    private CreateAccount user;

    private JTextField txtName, txtPrice, txtStck, txtMarkup, txtImagePath;
    private JComboBox<Category> cmbCategory;
    private JComboBox<Brand> cmbBrand;
    private JComboBox<Supplier> cmbSupplier;
    private JTextField txtUnit;
    private JLabel lblDate;

    // DAOs
    private ProductDAO productDAO = new ProductDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private BrandDAO brandDAO = new BrandDAO();
    private SupplierDAO supplierDAO = new SupplierDAO();
    private OrdersDAO ordersDAO = new OrdersDAO();
    
    public ProductFrame(CreateAccount user){
        this.user = user;
        initializeFrame();
    }
    
    public ProductFrame(){
        initializeFrame();
    }
    
    public void initializeFrame() {
        setTitle("Eshopping - Product Management");
        setSize(1400, 850);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(ProductColors.BACKGROUND);
        setLayout(new BorderLayout(15, 15));
        FrameUtil.addCloseConfirmation(this);

        JPanel mainContainer = new JPanel(new BorderLayout(15, 15));
        mainContainer.setBackground(ProductColors.BACKGROUND);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        model = new DefaultTableModel(new String[]{
                "ID", "Name", "Category", "Brand", "Supplier",
                "Unit", "Cost Price", "Stock", "Mark up", "Selling Price", "Image", "Date Added"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(model);
        styleTable(table);
        
        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        loadProducts();
        
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(ProductColors.CARD_BG);
        tablePanel.setBorder(createBorder("Product Inventory"));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        mainContainer.add(tablePanel, BorderLayout.CENTER);

        // ===== Top Bar =====
        TopBar topBar = new TopBar("Eshopping", table, tablePanel, () -> loadProducts(), TopBar.Mode.ADMIN);
        add(topBar, BorderLayout.NORTH);

        // ===== Modern Form Panel =====
        JPanel formContainer = createForm();
        mainContainer.add(formContainer, BorderLayout.SOUTH);

        add(mainContainer, BorderLayout.CENTER);

        // ===== Load Dropdowns =====
        loadDropdowns();

        // ===== Table Click =====
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    populateFormFromTable(row);
                }
            }
        });

        setVisible(true);
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(45);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(224, 231, 255));
        table.setSelectionForeground(ProductColors.TEXT_PRIMARY);
        
        // Header styling
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(ProductColors.BACKGROUND);
        table.getTableHeader().setForeground(ProductColors.TEXT_PRIMARY);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ProductColors.BORDER_COLOR));
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));
        
        // Center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 2; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : ProductColors.BACKGROUND);
                }
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return c;
            }
        });
    }

    private JPanel createForm() {
        JPanel formContainer = new JPanel(new BorderLayout(10, 10));
        formContainer.setBackground(ProductColors.CARD_BG);
        formContainer.setBorder(createBorder("Product Details"));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(ProductColors.CARD_BG);
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        int row = 0;

        // Row 1: Name and Category
        addFormField(form, gbc, "Product Name", txtName = createStyledTextField(), 0, row);
        
        cmbCategory = createComboBox();
        JButton btnAddCategory = createIconButton("+", ProductColors.PRIMARY, "Add Category");
        addFormFieldWithButton(form, gbc, "Category", cmbCategory, btnAddCategory, 2, row);
        btnAddCategory.addActionListener(e -> addNewCategory());
        row++;

        // Row 2: Brand and Supplier
        cmbBrand = createComboBox();
        JButton btnAddBrand = createIconButton("+", ProductColors.PRIMARY, "Add Brand");
        addFormFieldWithButton(form, gbc, "Brand", cmbBrand, btnAddBrand, 0, row);
        btnAddBrand.addActionListener(e -> addNewBrand());

        cmbSupplier = createComboBox();
        JButton btnAddSupplier = createIconButton("+", ProductColors.PRIMARY, "Add Supplier");
        addFormFieldWithButton(form, gbc, "Supplier", cmbSupplier, btnAddSupplier, 2, row);
        btnAddSupplier.addActionListener(e -> addNewSupplier());
        row++;

        // Row 3: Unit and Price
        addFormField(form, gbc, "Unit", txtUnit = createStyledTextField(), 0, row);
        addFormField(form, gbc, "Cost Price (₱)", txtPrice = createStyledTextField(), 2, row);
        row++;

        // Row 4: Stock and Markup
        addFormField(form, gbc, "Stock Quantity", txtStck = createStyledTextField(), 0, row);
        addFormField(form, gbc, "Markup (%)", txtMarkup = createStyledTextField(), 2, row);
        row++;

        // Row 5: Date and Image
        lblDate = new JLabel(LocalDate.now().toString());
        styleLabel(lblDate);
        addFormField(form, gbc, "Date Added", lblDate, 0, row);

        txtImagePath = createStyledTextField();
        txtImagePath.setEditable(false);
        JButton btnBrowse = createBtn("Browse", ProductColors.WARNING);
        btnBrowse.addActionListener(e -> browseImage());
        addFormFieldWithButton(form, gbc, "Product Image", txtImagePath, btnBrowse, 2, row);
        row++;

        formContainer.add(form, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        formContainer.add(buttonPanel, BorderLayout.SOUTH);

        return formContainer;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        panel.setBackground(ProductColors.CARD_BG);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ProductColors.BORDER_COLOR));

        JButton btnAdd = createBtn("Add Product", ProductColors.SUCCESS);
        JButton btnUpdate = createBtn("Update Product", ProductColors.PRIMARY);
        JButton btnDelete = createBtn("Delete Product", ProductColors.DANGER);

        btnAdd.addActionListener(e -> addProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());

        panel.add(btnAdd);
        panel.add(btnUpdate);
        panel.add(btnDelete);

        return panel;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        
        JLabel lbl = new JLabel(label);
        styleLabel(lbl);
        panel.add(lbl, gbc);

        gbc.gridx = x + 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private void addFormFieldWithButton(JPanel panel, GridBagConstraints gbc, String label, 
                                        JComponent field, JButton button, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        
        JLabel lbl = new JLabel(label);
        styleLabel(lbl);
        panel.add(lbl, gbc);

        JPanel fieldPanel = new JPanel(new BorderLayout(5, 0));
        fieldPanel.setBackground(ProductColors.CARD_BG);
        fieldPanel.add(field, BorderLayout.CENTER);
        fieldPanel.add(button, BorderLayout.EAST);

        gbc.gridx = x + 1;
        gbc.weightx = 0.7;
        panel.add(fieldPanel, gbc);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(200, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(ProductColors.BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(ProductColors.PRIMARY, 2, true),
                    BorderFactory.createEmptyBorder(5, 12, 5, 12)
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(ProductColors.BORDER_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(5, 12, 5, 12)
                ));
            }
        });
        
        return field;
    }

    private <T> JComboBox<T> createComboBox() {
        JComboBox<T> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setPreferredSize(new Dimension(200, 38));
        combo.setBackground(Color.WHITE);
        combo.setBorder(new LineBorder(ProductColors.BORDER_COLOR, 1, true));
        return combo;
    }

    private JButton createBtn(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 40));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private JButton createIconButton(String text, Color bgColor, String tooltip) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(40, 38));
        button.setToolTipText(tooltip);
        button.setBorder(BorderFactory.createEmptyBorder());
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private void styleLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(ProductColors.TEXT_SECONDARY);
    }

    private Border createBorder(String title) {
        TitledBorder titleBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ProductColors.BORDER_COLOR, 1, true),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 15),
            ProductColors.TEXT_PRIMARY
        );
        return BorderFactory.createCompoundBorder(
            titleBorder,
            BorderFactory.createEmptyBorder(10, 15, 15, 15)
        );
    }

    private void browseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image Files", "jpg", "jpeg", "png", "gif"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            txtImagePath.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void populateFormFromTable(int row) {
        txtName.setText(model.getValueAt(row, 1).toString());
        cmbCategory.setSelectedItem(model.getValueAt(row, 2));
        cmbBrand.setSelectedItem(model.getValueAt(row, 3));
        cmbSupplier.setSelectedItem(model.getValueAt(row, 4));
        txtUnit.setText(model.getValueAt(row, 5).toString());
        txtPrice.setText(model.getValueAt(row, 6).toString().replace("PHP ", "").replace("₱", "").replace(",", ""));
        txtStck.setText(model.getValueAt(row, 7).toString());
        txtMarkup.setText(model.getValueAt(row, 8).toString());
        Object imgValue = model.getValueAt(row, 10);
        txtImagePath.setText(imgValue != null ? imgValue.toString() : ""); 
        lblDate.setText(model.getValueAt(row, 11).toString());
    }

    // ===== Load Products =====
    private void loadProducts() {
        model.setRowCount(0);
        List<Product> products = productDAO.getAllProducts();
        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

        for (Product p : products) {
            Category cat = categoryDAO.getCategoryById(p.getCategoryId());
            Brand brand = brandDAO.getBrandById(p.getBrandId());
            Supplier sup = supplierDAO.getSupplierById(p.getSupplierId());

            model.addRow(new Object[]{
                    p.getId(),              
                    p.getProductName(),            
                    cat,                    
                    brand,                  
                    sup,                    
                    p.getUnit(),           
                    currency.format(p.getPrice()),       
                    p.getStock(),           
                    p.getMarkUp(),          
                    currency.format(p.getSellingPrice()), 
                    p.getImagePath(),       
                    p.getDateAdded()        
            });
        }
    }

    private void loadDropdowns() {
        cmbCategory.removeAllItems();
        for (Category c : categoryDAO.getAllCategories()) cmbCategory.addItem(c);

        cmbBrand.removeAllItems();
        for (Brand b : brandDAO.getAllBrands()) cmbBrand.addItem(b);

        cmbSupplier.removeAllItems();
        for (Supplier s : supplierDAO.getAllSuppliers()) cmbSupplier.addItem(s);
    }

    private void addProduct() {
        try {
            Product p = new Product();
            p.setProductName(txtName.getText());
            p.setCategoryId(((Category)cmbCategory.getSelectedItem()).getId());
            p.setBrandId(((Brand)cmbBrand.getSelectedItem()).getId());
            p.setSupplierId(((Supplier)cmbSupplier.getSelectedItem()).getId());
            p.setUnit(txtUnit.getText());
            p.setPrice(Double.parseDouble(txtPrice.getText()));
            p.setStock(Integer.parseInt(txtStck.getText()));
            p.setMarkUp(Double.parseDouble(txtMarkup.getText()));
            p.setImagePath(txtImagePath.getText());
            p.setDateAdded(LocalDate.now());

            if(productDAO.addProduct(p)) {
                JOptionPane.showMessageDialog(this, "Product added successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadProducts();
                clearForm();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProduct() {
        int row = table.getSelectedRow();
        if(row >= 0) {
            try {
                Product p = new Product();
                p.setId((int) model.getValueAt(row, 0));
                p.setProductName(txtName.getText());
                p.setCategoryId(((Category)cmbCategory.getSelectedItem()).getId());
                p.setBrandId(((Brand)cmbBrand.getSelectedItem()).getId());
                p.setSupplierId(((Supplier)cmbSupplier.getSelectedItem()).getId());
                p.setUnit(txtUnit.getText());
                p.setPrice(Double.parseDouble(txtPrice.getText()));
                p.setStock(Integer.parseInt(txtStck.getText()));
                p.setMarkUp(Double.parseDouble(txtMarkup.getText()));
                p.setImagePath(txtImagePath.getText());
                p.setDateAdded(LocalDate.parse(lblDate.getText()));

                if(productDAO.updateProduct(p)) {
                    JOptionPane.showMessageDialog(this, "Product updated successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadProducts();
                    clearForm();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product to update.", 
                "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteProduct() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a product.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int id = (int) model.getValueAt(row, 0);

        boolean hasOrders = ordersDAO.productHasOrders(id);

        if (hasOrders) {
            JOptionPane.showMessageDialog(
                this,
                "⚠ Cannot delete this product.\n" +
                "It is already linked to existing orders.\n" +
                "Consider deactivating it instead.",
                "Delete Not Allowed",
                JOptionPane.WARNING_MESSAGE
            );
        } else {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this product?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                boolean deleted = productDAO.deleteProduct(id); // hard delete

                if (deleted) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Product deleted successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    loadProducts();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "Failed to delete product.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
    }

    private void addNewCategory() {
        String name = JOptionPane.showInputDialog(this, "Enter new category name:");
        if(name != null && !name.isBlank()) {
            if(categoryDAO.addCategory(new Category(0, name))) {
                JOptionPane.showMessageDialog(this, "Category added successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadDropdowns();
            }
        }
    }

    private void addNewBrand() {
        String name = JOptionPane.showInputDialog(this, "Enter new brand name:");
        if(name != null && !name.isBlank()) {
            if(brandDAO.addBrand(new Brand(0, name))) {
                JOptionPane.showMessageDialog(this, "Brand added successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadDropdowns();
            }
        }
    }

    private void addNewSupplier() {
        String name = JOptionPane.showInputDialog(this, "Enter new supplier name:");
        if(name != null && !name.isBlank()) {
            if(supplierDAO.addSupplier(new Supplier(0, name))) {
                JOptionPane.showMessageDialog(this, "Supplier added successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadDropdowns();
            }
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtPrice.setText("");
        txtStck.setText("");
        txtMarkup.setText("");
        txtImagePath.setText("");
        txtUnit.setText("");
        lblDate.setText(LocalDate.now().toString());
        table.clearSelection();
    }
}