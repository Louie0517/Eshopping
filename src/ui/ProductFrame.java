package ui;

import dao.*;


import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class ProductFrame extends JFrame {

    // ===== Components =====
    private JTable table;
    private DefaultTableModel model;

    private JTextField txtName, txtPrice, txtQty;
    private JComboBox<Category> cmbCategory;
    private JComboBox<Brand> cmbBrand;
    private JComboBox<Supplier> cmbSupplier;
    private JTextField txtUnit;
    private JLabel lblDate;

    // ===== DAOs =====
    private ProductDAO productDAO = new ProductDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private BrandDAO brandDAO = new BrandDAO();
    private SupplierDAO supplierDAO = new SupplierDAO();

    public ProductFrame() {
        setTitle("E-Commerce Product Management");
        setSize(1200, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // ===== Table Panel =====
        model = new DefaultTableModel(new String[]{
                "ID", "Name", "Category", "Brand", "Supplier",
                "Unit", "Price", "Quantity", "Date Added"}, 0);
        table = new JTable(model);
        loadProducts();
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== Form Panel =====
        JPanel form = new JPanel(new GridLayout(6, 4, 5, 5));

        // Row 1
        form.add(new JLabel("Name:"));
        txtName = new JTextField();
        form.add(txtName);

        form.add(new JLabel("Category:"));
        cmbCategory = new JComboBox<>();
        JButton btnAddCategory = new JButton("+");
        btnAddCategory.setToolTipText("Add New Category");
        JPanel catPanel = new JPanel(new BorderLayout());
        catPanel.add(cmbCategory, BorderLayout.CENTER);
        catPanel.add(btnAddCategory, BorderLayout.EAST);
        form.add(catPanel);

        // Row 2
        form.add(new JLabel("Brand:"));
        cmbBrand = new JComboBox<>();
        JButton btnAddBrand = new JButton("+");
        btnAddBrand.setToolTipText("Add New Brand");
        JPanel brandPanel = new JPanel(new BorderLayout());
        brandPanel.add(cmbBrand, BorderLayout.CENTER);
        brandPanel.add(btnAddBrand, BorderLayout.EAST);
        form.add(brandPanel);

        form.add(new JLabel("Supplier:"));
        cmbSupplier = new JComboBox<>();
        JButton btnAddSupplier = new JButton("+");
        btnAddSupplier.setToolTipText("Add New Supplier");
        JPanel supPanel = new JPanel(new BorderLayout());
        supPanel.add(cmbSupplier, BorderLayout.CENTER);
        supPanel.add(btnAddSupplier, BorderLayout.EAST);
        form.add(supPanel);

        // Row 3
        form.add(new JLabel("Unit:"));
        txtUnit = new JTextField();
        form.add(txtUnit);

        form.add(new JLabel("Price (PHP):"));
        txtPrice = new JTextField();
        form.add(txtPrice);

        // Row 4
        form.add(new JLabel("Quantity:"));
        txtQty = new JTextField();
        form.add(txtQty);

        form.add(new JLabel("Date Added:"));
        lblDate = new JLabel(LocalDate.now().toString());
        form.add(lblDate);

        // Row 5 - Buttons
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        form.add(btnAdd);
        form.add(btnUpdate);
        form.add(btnDelete);

        add(form, BorderLayout.SOUTH);

        // ===== Load Dropdowns =====
        loadDropdowns();

        // ===== Button Actions =====
        btnAddCategory.addActionListener(e -> addNewCategory());
        btnAddBrand.addActionListener(e -> addNewBrand());
        btnAddSupplier.addActionListener(e -> addNewSupplier());

        btnAdd.addActionListener(e -> addProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());

        // ===== Table Click =====
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtName.setText(model.getValueAt(row, 1).toString());
                    cmbCategory.setSelectedItem(model.getValueAt(row, 2));
                    cmbBrand.setSelectedItem(model.getValueAt(row, 3));
                    cmbSupplier.setSelectedItem(model.getValueAt(row, 4));
                    txtUnit.setText(model.getValueAt(row, 5).toString());
                    txtPrice.setText(model.getValueAt(row, 6).toString().replace("PHP ", ""));
                    txtQty.setText(model.getValueAt(row, 7).toString());
                    lblDate.setText(model.getValueAt(row, 8).toString());
                }
            }
        });

        setVisible(true);
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
                    p.getName(),
                    cat,
                    brand,
                    sup,
                    p.getUnit(),
                    currency.format(p.getPrice()),
                    p.getQuantity(),
                    p.getDateAdded()
            });
        }
    }

    // ===== Load Dropdowns =====
    private void loadDropdowns() {
        cmbCategory.removeAllItems();
        for (Category c : categoryDAO.getAllCategories()) cmbCategory.addItem(c);

        cmbBrand.removeAllItems();
        for (Brand b : brandDAO.getAllBrands()) cmbBrand.addItem(b);

        cmbSupplier.removeAllItems();
        for (Supplier s : supplierDAO.getAllSuppliers()) cmbSupplier.addItem(s);
    }

    // ===== Add Product =====
    private void addProduct() {
        try {
            Product p = new Product();
            p.setName(txtName.getText());
            p.setCategoryId(((Category)cmbCategory.getSelectedItem()).getId());
            p.setBrandId(((Brand)cmbBrand.getSelectedItem()).getId());
            p.setSupplierId(((Supplier)cmbSupplier.getSelectedItem()).getId());
            p.setUnit(txtUnit.getText());
            p.setPrice(Double.parseDouble(txtPrice.getText()));
            p.setQuantity(Integer.parseInt(txtQty.getText()));
            p.setDateAdded(LocalDate.now());

            if(productDAO.addProduct(p)) {
                JOptionPane.showMessageDialog(this, "Product added!");
                loadProducts();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // ===== Update Product =====
    private void updateProduct() {
        int row = table.getSelectedRow();
        if(row >= 0) {
            try {
                Product p = new Product();
                p.setId((int) model.getValueAt(row, 0));
                p.setName(txtName.getText());
                p.setCategoryId(((Category)cmbCategory.getSelectedItem()).getId());
                p.setBrandId(((Brand)cmbBrand.getSelectedItem()).getId());
                p.setSupplierId(((Supplier)cmbSupplier.getSelectedItem()).getId());
                p.setUnit(txtUnit.getText());
                p.setPrice(Double.parseDouble(txtPrice.getText()));
                p.setQuantity(Integer.parseInt(txtQty.getText()));
                p.setDateAdded(LocalDate.parse(lblDate.getText()));

                if(productDAO.updateProduct(p)) {
                    JOptionPane.showMessageDialog(this, "Product updated!");
                    loadProducts();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    // ===== Delete Product =====
    private void deleteProduct() {
        int row = table.getSelectedRow();
        if(row >= 0) {
            int id = (int) model.getValueAt(row, 0);
            if(productDAO.deleteProduct(id)) {
                JOptionPane.showMessageDialog(this, "Product deleted!");
                loadProducts();
            }
        }
    }

    // ===== Add New Category / Brand / Supplier =====
    private void addNewCategory() {
        String name = JOptionPane.showInputDialog(this, "Enter new category:");
        if(name != null && !name.isBlank()) {
            if(categoryDAO.addCategory(new Category(0, name))) {
                JOptionPane.showMessageDialog(this, "Category added!");
                loadDropdowns();
            }
        }
    }

    private void addNewBrand() {
        String name = JOptionPane.showInputDialog(this, "Enter new brand:");
        if(name != null && !name.isBlank()) {
            if(brandDAO.addBrand(new Brand(0, name))) {
                JOptionPane.showMessageDialog(this, "Brand added!");
                loadDropdowns();
            }
        }
    }

    private void addNewSupplier() {
        String name = JOptionPane.showInputDialog(this, "Enter new supplier:");
        if(name != null && !name.isBlank()) {
            if(supplierDAO.addSupplier(new Supplier(0, name))) {
                JOptionPane.showMessageDialog(this, "Supplier added!");
                loadDropdowns();
            }
        }
    }
}
