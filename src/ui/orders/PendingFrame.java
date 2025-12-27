package ui.orders;

import java.util.List;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;

import ui.components.TopBar.Status;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import authentication.OnProcessOrderEmail;
import dao.OrdersDAO;
import model.CreateAccount;
import model.Orders;
import view.ActionEditor;
import view.ActionPanelProvider;
import view.ActionRenderer;
import ui.OrdersFrame;
import ui.components.ScrollBar;
import ui.components.TopBar;
import util.FrameUtil;
import view.ImageRenderer;
import resources.colors.ProductColors;

public class PendingFrame extends JFrame implements ActionPanelProvider {
    private CreateAccount currentUser;
    private TopBar topBar;
    private JPanel mainPanel, container;
    private JTable table;

    private OrdersDAO ordersDAO = new OrdersDAO();

    public PendingFrame(){}

    public PendingFrame(CreateAccount user){
        this.currentUser = user;
        setTitle("Eshopping - Pending Orders");
        setSize(1300, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initializeFrame();
        loadTableData();

        FrameUtil.addCloseConfirmation(this);
        setVisible(true);
    }
    
    private void loadTableData() {
        table = new JTable();
        styleTable(table);
        displayOrders("Pending");
    }

    
    private void initializeFrame(){
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        topBar = new TopBar("Eshopping", mainPanel, Status.PENDING);
        add(topBar, BorderLayout.NORTH);

        container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(true);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);

        ScrollBar.styleScrollBar(scrollPane);
        mainPanel.add(scrollPane,  BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }


    private List<Orders> retrievePendingOrders(){
        if (currentUser.getRole() != null && currentUser.getRole().equalsIgnoreCase("admin")) {
            return ordersDAO.getAllOrdersByStatus("Pending");
        } else {
            return ordersDAO.getOrderByStatus(currentUser.getUserId(), "Pending");
        }
    }

    private DefaultTableModel createTable(){
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.setRowCount(0);

        String[] headerNames = {"ID", "Image", "Product", "Quantity","Buyer", "Phone Number", "Address", "Total Amount", "Status", "Date", "Action"};

        tableModel = new DefaultTableModel(headerNames, 0){
             @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 10;
                }
        };

        return tableModel;
    }

    @Override
    public JPanel createActionPanel(int row) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.setOpaque(false);

        JButton btnProcess = new JButton("Process");
        btnProcess.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnProcess.setBackground(ProductColors.SUCCESS);
        btnProcess.setForeground(Color.WHITE);
        btnProcess.setFocusPainted(false);
        btnProcess.setBorderPainted(false);
        btnProcess.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnProcess.setPreferredSize(new Dimension(85, 30));
        
        btnProcess.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnProcess.setBackground(ProductColors.SUCCESS.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btnProcess.setBackground(ProductColors.SUCCESS);
            }
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnCancel.setBackground(ProductColors.DANGER);
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setBorderPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.setPreferredSize(new Dimension(85, 30));
        
        btnCancel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnCancel.setBackground(ProductColors.DANGER.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btnCancel.setBackground(ProductColors.DANGER);
            }
        });

        btnProcess.addActionListener(e -> processOrder(row));
        btnCancel.addActionListener(e -> cancelOrder(row));

        panel.add(btnProcess);
        panel.add(btnCancel);

        return panel;
    }


    private void processOrder(int row) {
        if (row == -1 || row >= table.getRowCount()) {
            JOptionPane.showMessageDialog(
                this,
                "Invalid order selection.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String status = table.getValueAt(row, 8).toString();

        if (!status.equalsIgnoreCase("Pending")) {
            JOptionPane.showMessageDialog(
                this,
                "Only pending orders can be processed.",
                "Invalid Action",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Object orderIdObj = table.getValueAt(row, 0);
        Long orderId = ((Number) orderIdObj).longValue();

        System.out.println("Processing order ID: " + orderId);

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Set this order to Processing?",
            "Confirm Processing",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;
        boolean success = ordersDAO.updateOrderStatus(orderId, "Processing");
        

        if (!success) {
            JOptionPane.showMessageDialog(
                this,
                "Failed to process order. It may have already been updated.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Orders order = ordersDAO.getOrderById(orderId);

        if (order != null && order.getEmail() != null) {
                OnProcessOrderEmail.sendOnProcessOrderEmail(
                    order.getEmail(),
                    order.getProductName(),
                    order.getTotal(),
                    order.getStatus()  
                );
                JOptionPane.showMessageDialog(
                    this,
                    "Order is now Processing",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } 
            displayOrders("Pending");
        if (topBar != null) topBar.refreshAllBadges();
    } 
    

    private void cancelOrder(int row){
        if (row == -1 || row >= table.getRowCount()) {
            JOptionPane.showMessageDialog(
                this,
                "Invalid order selection.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Object orderIdObj = table.getValueAt(row, 0);
        Long orderId = ((Number) orderIdObj).longValue();

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to cancel this order?",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        boolean success = ordersDAO.cancelOrderStatus(orderId);

        if (success) {
            JOptionPane.showMessageDialog(
                this,
                "Order has been cancelled successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );

            displayOrders("Pending");
            if (topBar != null) topBar.refreshAllBadges();
            

        } else {
            JOptionPane.showMessageDialog(
                this,
                "Failed to cancel order. It may have already been processed.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }


    private void displayOrders(String status){
        List<Orders> orders = retrievePendingOrders();
        
        if(orders.isEmpty()){
            mainPanel.removeAll();
            mainPanel.add(createEmptyState(), BorderLayout.CENTER);
            mainPanel.revalidate();
            mainPanel.repaint();
        } else{
            mainPanel.removeAll();
            
            DefaultTableModel tableModel = createTable();
            table.setModel(tableModel);
            
            for(Orders order : orders){ 
                tableModel.addRow(new Object[]{
                    order.getId(),
                    order.getImagePath(), 
                    order.getProductName(), 
                    order.getQuantity(),
                    order.getUsername(),
                    order.getPhoneNumber(),
                    order.getAddress(),
                    String.format("₱%,.2f", order.getTotal()),
                    order.getStatus(), 
                    order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    "Actions" 
                });
            }
            
            applyColumnRenderers();
            
            table.getColumnModel().getColumn(0).setMinWidth(0);
            table.getColumnModel().getColumn(0).setMaxWidth(0);
            table.getColumnModel().getColumn(0).setWidth(0);
            
            table.getColumnModel().getColumn(1).setPreferredWidth(80);  
            table.getColumnModel().getColumn(2).setPreferredWidth(150);
            table.getColumnModel().getColumn(3).setPreferredWidth(70);  
            table.getColumnModel().getColumn(4).setPreferredWidth(120); 
            table.getColumnModel().getColumn(5).setPreferredWidth(120); 
            table.getColumnModel().getColumn(6).setPreferredWidth(200); 
            table.getColumnModel().getColumn(7).setPreferredWidth(100); 
            table.getColumnModel().getColumn(8).setPreferredWidth(80);  
            table.getColumnModel().getColumn(9).setPreferredWidth(100);
            table.getColumnModel().getColumn(10).setPreferredWidth(200); 
            
            JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
            tablePanel.setBackground(ProductColors.CARD_BG);
            tablePanel.setBorder(createBorder("Pending"));

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getViewport().setBackground(Color.WHITE);
            tablePanel.add(scrollPane, BorderLayout.CENTER);

            mainPanel.add(tablePanel, BorderLayout.CENTER);
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }


    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(80);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(224, 231, 255));
        table.setSelectionForeground(ProductColors.TEXT_PRIMARY);
        
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(ProductColors.BACKGROUND);
        table.getTableHeader().setForeground(ProductColors.TEXT_PRIMARY);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ProductColors.BORDER_COLOR));
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));
        
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
    
    private void applyColumnRenderers() {
        if (table.getColumnCount() > 0) {

            table.getColumnModel().getColumn(1).setCellRenderer(new ImageRenderer());
            
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            for (int i = 2; i < 9; i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
            
            table.getColumnModel().getColumn(10).setCellRenderer(new ActionRenderer(this));
            table.getColumnModel().getColumn(10).setCellEditor(new ActionEditor(this, table));
        }
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
        
        JLabel titleLabel = new JLabel("No Pending Orders Found");
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