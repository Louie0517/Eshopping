package ui;

import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.swing.*;

import authentication.CheckOutEmail;
import dao.CartDAO;
import dao.OrdersDAO;
import model.Cart;
import model.CreateAccount;
import model.Orders;
import ui.components.ScrollBar;
import ui.components.TopBar;
import util.FrameUtil;

public class CartFrame extends JFrame {
    private CartDAO cartDAO = new CartDAO();
    private OrdersDAO ordersDAO = new OrdersDAO();

    private CreateAccount user;
    private Long userId;
    
    private TopBar topBar;
    private JPanel mainPanel;
    private JPanel cartContainer;
    private JPanel summaryPanel;
    private JScrollPane scrollPane;
    private NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
    
    private List<Cart> cartItems;
    
    // Summary labels that need to be updated
    private JLabel subtotalValueLabel;
    private JLabel totalValueLabel;
    private double shippingCost = 50.0;

    
    public CartFrame(CreateAccount user) {
        this.user = user;
        this.userId = user.getUserId();
        initializeFrame();
    }
    
    private void initializeFrame() {
        setTitle("Eshopping - Shopping Cart");
        setSize(1300, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        createMainFrame();
        
        FrameUtil.addCloseConfirmation(this);
        setVisible(true);
    }

    private void createMainFrame() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        topBar = new TopBar("Eshopping", mainPanel, TopBar.Mode.CART);
        add(topBar, BorderLayout.NORTH);
       
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setBackground(Color.WHITE);
        
        cartContainer = new JPanel();
        cartContainer.setLayout(new BoxLayout(cartContainer, BoxLayout.Y_AXIS));
        cartContainer.setBackground(Color.WHITE);
        
        loadCartItems();
        
        scrollPane = new JScrollPane(cartContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        ScrollBar.styleScrollBar(scrollPane);
        
        summaryPanel = createSummaryPanel();
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(summaryPanel, BorderLayout.EAST);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Shopping Cart");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(17, 24, 39));
        
        header.add(titleLabel, BorderLayout.WEST);
        
        return header;
    }
    
    private void loadCartItems() {
        cartItems = retrieveCart();
        
        System.out.println("Loading " + cartItems.size() + " cart items");
        
        if (cartItems.isEmpty()) {
            System.out.println("Cart is empty - showing empty state");
            cartContainer.add(createEmptyState());
        } else {
            System.out.println("Adding cart items to shop");
            for (Cart item : cartItems) {
                System.out.println("Adding item: " + item.getProdName());
                cartContainer.add(createCartItemCard(item));
                cartContainer.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }
    }
    
    private List<Cart> retrieveCart() {
        System.out.println("Loading cart for user ID: " + userId);
        
        List<Cart> items = cartDAO.getCartByUser(userId);
        System.out.println("Cart items found: " + items.size());
        
        return items;
    }
    
    private JPanel createCartItemCard(Cart item) {
        JPanel card = new JPanel(new BorderLayout(20, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        
        JPanel imagePanel = new JPanel(new GridBagLayout());
        imagePanel.setPreferredSize(new Dimension(100, 100));
        imagePanel.setBackground(new Color(249, 250, 251));
        imagePanel.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        
        JLabel imgLabel = new JLabel();
        imgLabel.setHorizontalAlignment(JLabel.CENTER);
        
        if (item.getImgPath() != null && !item.getImgPath().isEmpty()) {
            ImageIcon icon = new ImageIcon(item.getImgPath());
            Image scaledImg = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaledImg));
        } else {
            imgLabel.setText("🖼️");
            imgLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 35));
        }
        imagePanel.add(imgLabel);
        
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);
        
        JLabel nameLabel = new JLabel(item.getProdName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(new Color(17, 24, 39));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel priceLabel = new JLabel(currency.format(item.getSellingPrice()) + " per item");
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        priceLabel.setForeground(new Color(107, 114, 128));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        qtyPanel.setOpaque(false);
        qtyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton minusBtn = createQtyButton("-");
        JLabel qtyLabel = new JLabel(String.valueOf(item.getQty()));
        qtyLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        qtyLabel.setForeground(new Color(17, 24, 39));
        qtyLabel.setPreferredSize(new Dimension(40, 30));
        qtyLabel.setHorizontalAlignment(JLabel.CENTER);
        qtyLabel.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        
        JButton plusBtn = createQtyButton("+");
        
        // Item subtotal label
        JLabel itemSubtotalLabel = new JLabel(currency.format(item.getSubtotal()));
        itemSubtotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        itemSubtotalLabel.setForeground(new Color(17, 24, 39));
        
        minusBtn.addActionListener(e -> updateQuantity(item, -1, qtyLabel, itemSubtotalLabel));
        plusBtn.addActionListener(e -> updateQuantity(item, 1, qtyLabel, itemSubtotalLabel));
        
        qtyPanel.add(minusBtn);
        qtyPanel.add(qtyLabel);
        qtyPanel.add(plusBtn);
        
        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        detailsPanel.add(priceLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        detailsPanel.add(qtyPanel);
        
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(180, 100));
        
        itemSubtotalLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        rightPanel.add(itemSubtotalLabel);
        rightPanel.add(Box.createVerticalGlue());
        
        JButton removeBtn = new JButton("Remove");
        removeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        removeBtn.setForeground(new Color(220, 38, 38));
        removeBtn.setBackground(Color.WHITE);
        removeBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 38, 38), 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        removeBtn.setFocusPainted(false);
        removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        removeBtn.addActionListener(e -> removeItem(item));
        
        removeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                removeBtn.setBackground(new Color(254, 226, 226));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                removeBtn.setBackground(Color.WHITE);
            }
        });
        
        rightPanel.add(removeBtn);
        
        card.add(imagePanel, BorderLayout.WEST);
        card.add(detailsPanel, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);
        
        return card;
    }
    
    private JButton createQtyButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(new Color(71, 85, 105));
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(241, 245, 249));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(Color.WHITE);
            }
        });
        
        return btn;
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));
        panel.setPreferredSize(new Dimension(350, 300));
        panel.setMaximumSize(new Dimension(350, 400));
        
        JLabel titleLabel = new JLabel("Order Summary");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(17, 24, 39));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        double subtotal = calculateSubtotal();
        double total = subtotal + shippingCost;
        
        // Subtotal row with stored reference
        subtotalValueLabel = new JLabel(currency.format(subtotal));
        subtotalValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        subtotalValueLabel.setForeground(new Color(17, 24, 39));
        panel.add(createSummaryRow("Subtotal:", subtotalValueLabel));
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Shipping row
        panel.add(createSummaryRow("Shipping:", currency.format(shippingCost)));
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        
        // Divider
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(separator);
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        
        // Total row with stored reference
        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JLabel totalTextLabel = new JLabel("Total:");
        totalTextLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalTextLabel.setForeground(new Color(17, 24, 39));
        
        totalValueLabel = new JLabel(currency.format(total));
        totalValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        totalValueLabel.setForeground(new Color(17, 24, 39));
        
        totalRow.add(totalTextLabel, BorderLayout.WEST);
        totalRow.add(totalValueLabel, BorderLayout.EAST);
        
        panel.add(totalRow);
        panel.add(Box.createRigidArea(new Dimension(0, 24)));
        
        JButton checkoutBtn = new JButton("Proceed to Checkout");
        checkoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.setBackground(new Color(59, 130, 246));
        checkoutBtn.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));
        checkoutBtn.setFocusPainted(false);
        checkoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        checkoutBtn.addActionListener(e -> checkout());
        
        checkoutBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                List<Orders> order = ordersDAO.getOrderDetails(user.getUserId());

                if(!order.isEmpty()){
                    Orders latestOrder = order.get(0);
                    CheckOutEmail.sendEmail(
                        user.getEmail(),
                        latestOrder.getProductName(),
                        latestOrder.getOrderDate(),
                        latestOrder.getTotal(),
                        latestOrder.getStatus()
                    );
                }

                checkoutBtn.setBackground(new Color(37, 99, 235));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                checkoutBtn.setBackground(new Color(59, 130, 246));
            }
        });
            
        panel.add(checkoutBtn);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JPanel createSummaryRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelComp.setForeground(new Color(107, 114, 128));
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueComp.setForeground(new Color(17, 24, 39));
        
        row.add(labelComp, BorderLayout.WEST);
        row.add(valueComp, BorderLayout.EAST);
        
        return row;
    }
    
    private JPanel createSummaryRow(String label, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelComp.setForeground(new Color(107, 114, 128));
        
        row.add(labelComp, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        
        return row;
    }
    
    private JPanel createEmptyState() {
        JPanel emptyPanel = new JPanel(new GridBagLayout());
        emptyPanel.setBackground(Color.WHITE);
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        
        JLabel iconLabel = new JLabel("🛒");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 90));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("Your Cart is Empty");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(31, 41, 55));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel("Add items to get started");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        descLabel.setForeground(new Color(107, 114, 128));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        content.add(iconLabel);
        content.add(Box.createRigidArea(new Dimension(0, 24)));
        content.add(titleLabel);
        content.add(Box.createRigidArea(new Dimension(0, 12)));
        content.add(descLabel);
        
        emptyPanel.add(content);
        return emptyPanel;
    }
    
    private double calculateSubtotal() {
        return cartItems.stream()
            .mapToDouble(Cart::getSubtotal)
            .sum();
    }
    
    private void updateQuantity(Cart item, int change, JLabel qtyLabel, JLabel itemSubtotalLabel) {
        int newQty = item.getQty() + change;
        
        if (newQty < 1) {
            JOptionPane.showMessageDialog(this,
                "Quantity cannot be less than 1",
                "Invalid Quantity",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        item.setQty(newQty);
        boolean success = cartDAO.updateQty(item);
        
        if (success) {
          
            qtyLabel.setText(String.valueOf(newQty));
            
        
            double newItemSubtotal = item.getSellingPrice() * newQty;
            item.setSubtotal(newItemSubtotal);
            itemSubtotalLabel.setText(currency.format(newItemSubtotal));
           
            updateSummary();
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to update quantity",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateSummary() {
        double subtotal = calculateSubtotal();
        double total = subtotal + shippingCost;
        
        subtotalValueLabel.setText(currency.format(subtotal));
        totalValueLabel.setText(currency.format(total));
    }
    
    private void removeItem(Cart item) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Remove " + item.getProdName() + " from cart?",
            "Confirm Removal",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            System.out.println("Deleting cart_id = " + item.getCartId());

            boolean success = cartDAO.deleteCart(item.getCartId());
            
            if (success) {
                refreshCart();
                topBar.refreshCartCountBadge();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to remove item",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void checkout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Your cart is empty",
                "Cannot Checkout",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = cartDAO.checkout(user.getUserId(), cartItems);

        if (success) {
            JOptionPane.showMessageDialog(this,
                "Checkout successful!\nTotal: " +
                currency.format(calculateSubtotal() + 50),
                "Checkout",
                JOptionPane.INFORMATION_MESSAGE);
                // ADD TO CHECKOUT
            List<Orders> order = ordersDAO.getOrderDetails(user.getUserId());

            if(!order.isEmpty()){
                Orders latestOrder = order.get(0);
                CheckOutEmail.sendEmail(
                    user.getEmail(),
                    latestOrder.getProductName(),
                    latestOrder.getOrderDate(),
                    latestOrder.getSubtotal() + 50,
                    latestOrder.getStatus()
                );
            }
            new OrdersFrame(user);
            dispose();

        } else {
            JOptionPane.showMessageDialog(this,
                "Checkout failed. Please try again.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshCart() {
        cartContainer.removeAll();
        mainPanel.remove(summaryPanel);
        
        loadCartItems();
        summaryPanel = createSummaryPanel();
        
        JPanel contentPanel = (JPanel) mainPanel.getComponent(1);
        contentPanel.add(summaryPanel, BorderLayout.EAST);
        
        cartContainer.revalidate();
        cartContainer.repaint();
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}