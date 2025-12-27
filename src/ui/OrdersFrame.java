package ui;

import java.awt.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.*;

import dao.OrdersDAO;
import model.CreateAccount;
import model.Orders;
import ui.components.ScrollBar;
import ui.components.TopBar;
import util.FrameUtil;

import resources.colors.*;;

public class OrdersFrame extends JFrame {
    private CreateAccount currentUser;
    private OrdersDAO ordersDAO = new OrdersDAO();
    private TopBar topBar;
    private JPanel mainPanel, container;
    private NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
    

    public OrdersFrame(CreateAccount user) {
        this.currentUser = user;
        setTitle("Eshopping");
        setSize(1300, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initializeFrame();
        
        FrameUtil.addCloseConfirmation(this);
        setVisible(true);
    }

    private void initializeFrame() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        topBar = new TopBar("Eshopping", mainPanel, TopBar.Mode.ORDERS);
        add(topBar, BorderLayout.NORTH);
        
        JLabel titleLabel = new JLabel("Orders");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(17, 24, 39));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Color.WHITE);
        
        List<Orders> orders = retrieveOrders();
        displayOrders(orders);
        
        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        ScrollBar.styleScrollBar(scrollPane);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        
        add(mainPanel);
    }

    private List<Orders> retrieveOrders() {
        List<Orders> allOrders = ordersDAO.getAllOrders(currentUser.getUserId());
        List<Orders> pendingOrders = new ArrayList<>();
        for (Orders order : allOrders) {
            if (order.getStatus().equalsIgnoreCase("Pending")) {
                pendingOrders.add(order);
            }
        }
        return pendingOrders;
    }
    
    private void displayOrders(List<Orders> orders) {
        if (orders.isEmpty()) {
            container.add(createEmptyState());
        } else {
            for (Orders order : orders) {
                container.add(createOrderCard(order));
                container.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }
    }
    
    
    private JPanel createOrderCard(Orders order) {
        JPanel card = new JPanel(new BorderLayout(20, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        
        JPanel imagePanel = new JPanel(new GridBagLayout());
        imagePanel.setPreferredSize(new Dimension(120, 120));
        imagePanel.setBackground(new Color(249, 250, 251));
        imagePanel.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        
        JLabel imgLabel = new JLabel();
        imgLabel.setHorizontalAlignment(JLabel.CENTER);

        
        if (order.getImagePath() != null && !order.getImagePath().isEmpty()) {
            ImageIcon icon = new ImageIcon(order.getImagePath());
            Image scaledImg = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaledImg));
        } else {
            imgLabel.setText("🖼️");
            imgLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        }
        imagePanel.add(imgLabel);
      
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);
        
        JLabel productNameLabel = new JLabel(order.getProductName());
        productNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        productNameLabel.setForeground(new Color(17, 24, 39));
        productNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
        JLabel dateLabel = new JLabel(order.getOrderDate().format(formatter));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(107, 114, 128));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel brandLabel = new JLabel("Brand: " + order.getBrandName());
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brandLabel.setForeground(new Color(31, 41, 55));
        brandLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel qtyLabel = new JLabel("Quantity: " + order.getQuantity() + " × " + currency.format(order.getPrice()));
        qtyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        qtyLabel.setForeground(new Color(107, 114, 128));
        qtyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel statusBadge = createStatusBadge(order.getStatus());
        statusBadge.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        detailsPanel.add(productNameLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        detailsPanel.add(dateLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        detailsPanel.add(brandLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(qtyLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        detailsPanel.add(statusBadge);
        
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(200, 120));
        
        JLabel totalLabel = new JLabel("Total");
        totalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        totalLabel.setForeground(new Color(107, 114, 128));
        totalLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        JLabel priceLabel = new JLabel(currency.format(order.getTotal()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        priceLabel.setForeground(new Color(17, 24, 39));
        priceLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setBackground(ProductColors.DANGER);
        cancelBtn.setPreferredSize(new Dimension(150, 35));
        cancelBtn.setMaximumSize(new Dimension(150, 35));
        cancelBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        cancelBtn.addActionListener(e -> cancelOrder(order.getId()));
        if (!order.getStatus().equalsIgnoreCase("Pending")) 
            cancelBtn.setVisible(false);

        rightPanel.add(totalLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        rightPanel.add(priceLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        rightPanel.add(cancelBtn);
        rightPanel.add(Box.createVerticalGlue());
        
        card.add(imagePanel, BorderLayout.WEST);
        card.add(detailsPanel, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);
        
        return card;
    }

    private void refreshOrders() {
        container.removeAll();
        List<Orders> orders = retrieveOrders();
        displayOrders(orders);
        container.revalidate();
        container.repaint();
    }

    private void cancelOrder(int orderId){
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to cancel this order?",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;
       
        boolean success = ordersDAO.cancelOrderStatus(Long.valueOf(orderId));
        if (success) {
            JOptionPane.showMessageDialog(
                this,
                "Order has been cancelled successfully.",
                "Cancelled",
                JOptionPane.INFORMATION_MESSAGE
            );
            refreshOrders();
            if (topBar != null) topBar.refreshAllBadges();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Unable to cancel this order." + orderId,
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private JLabel createStatusBadge(String status) {
        JLabel badge = new JLabel(status);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        
        switch (status) {
            case "Pending":
                badge.setForeground(new Color(217, 119, 6));
                badge.setBackground(new Color(254, 243, 199));
                break;
            case "Processing":
                badge.setForeground(new Color(37, 99, 235));
                badge.setBackground(new Color(219, 234, 254));
                break;
            case "Shipped":
                badge.setForeground(new Color(139, 92, 246));
                badge.setBackground(new Color(237, 233, 254));
                break;
            case "Delivered":
                badge.setForeground(new Color(5, 150, 105));
                badge.setBackground(new Color(209, 250, 229));
                break;
            case "Cancelled":
                badge.setForeground(new Color(220, 38, 38));
                badge.setBackground(new Color(254, 226, 226));
                break;
            default:
                badge.setForeground(new Color(71, 85, 105));
                badge.setBackground(new Color(241, 245, 249));
        }
        
        return badge;
    }
    
    private JPanel createEmptyState() {
        JPanel emptyPanel = new JPanel(new GridBagLayout());
        emptyPanel.setBackground(Color.WHITE);
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        
        JLabel iconLabel = new JLabel("📦");
        iconLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 90));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("No Orders Found");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(31, 41, 55));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        content.add(iconLabel);
        content.add(Box.createRigidArea(new Dimension(0, 30)));
        content.add(titleLabel);
        
        emptyPanel.add(content);
        return emptyPanel;
    }
}