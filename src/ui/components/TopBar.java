package ui.components;

import util.UserSession;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import dao.CartDAO;
import dao.OrdersDAO;
import model.CreateAccount;

import ui.CartFrame;
import ui.CreateAccountFrame;
import ui.InventoryFrame;
import ui.OrdersFrame;
import ui.ProductFrame;
import ui.SalesFrame;
import ui.ShopFrame;
import ui.UserProfileFrame;
import ui.orders.*;

import util.FontUtil;
import util.NotifBadgeUtil;

public class TopBar extends JPanel {
    private JLabel shopLabel;
    private JTable mainTable;
    private JPanel contentPanel, mainPanel;
    private Runnable reloadDataCallback;
    private Mode mode;
    private Status status;
    private JTextField searchField; 
    
    private JLabel pendingCountBadge;
    private JLabel processingCountBadge;
    private JLabel shippedCountBadge;
    private JLabel cartCountBadge;
    
    private NotifBadgeUtil pendingBadgeUtil;
    private NotifBadgeUtil processingBadgeUtil;
    private NotifBadgeUtil shippedBadgeUtil;
    private NotifBadgeUtil cartBadgeUtil;


    private OrdersDAO ordersDAO = new OrdersDAO();
    private CartDAO cartDAO = new CartDAO();

    public enum Mode{
        ADMIN,
        BUYER,
        CART,
        ORDERS,
        SALES,
        INVENTORY,
    }

    public enum Status{
        PENDING,
        PROCESSING,
        SHIPPED,
    }

    public TopBar(String shopName, JPanel mainPanel, Mode mode){
        this.mainPanel = mainPanel;
        this.mode = mode;
        initializeTopBar(shopName);
    }

    public TopBar(String shopName, JPanel mainPanel, Status status){
        this.mainPanel = mainPanel;
        this.status = status;
        initializeTopBar(shopName);
    }
  
    public TopBar(String shopName, JTable mainTable, JPanel contentPanel, Runnable reloadDataCallback, Mode mode){
        this.mainTable = mainTable;
        this.contentPanel = contentPanel;
        this.reloadDataCallback = reloadDataCallback;
        this.mode = mode;
        initializeTopBar(shopName);
    }
    
    public TopBar(String shopName, Mode mode){
        this.mode = mode;
        initializeTopBar(shopName);
    }
    
    private void initializeTopBar(String shopName) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 60));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 230)));

        JPanel leftPanel = createLeftSection(shopName);
        JPanel centerPanel = createCenterSection(); 
        JPanel rightPanel = createRightSection();

        add(leftPanel, BorderLayout.WEST);
        if(centerPanel != null) add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    private JPanel createLeftSection(String shopName){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        panel.setOpaque(false);

        shopLabel = new JLabel(shopName);
        shopLabel.setFont(FontUtil.loadFontUtil().deriveFont(Font.BOLD, 18f));
        shopLabel.setForeground(new Color(30, 30, 45));

        panel.add(shopLabel);
        return panel;
    }

    // SEARCH - Shows for both ADMIN and BUYER modes
    private JPanel createCenterSection(){
        if (mode == Mode.CART || mode == Mode.ORDERS
            || mode == Mode.SALES || mode == Mode.INVENTORY || status == Status.PENDING
            || status == Status.PROCESSING || status == Status.SHIPPED
        ) return null;

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        panel.setOpaque(false);

        searchField = new JTextField(25);
        searchField.setFont(FontUtil.loadFontUtil().deriveFont(Font.PLAIN, 13f));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(20, 20, 23), 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
           
        ImageIcon img = new ImageIcon("resources\\img\\search.png");
        Image scale = img.getImage().getScaledInstance(16, 14, Image.SCALE_AREA_AVERAGING);
        ImageIcon search = new ImageIcon(scale);

        JButton searchButton = new RoundedButton(search, 27);
        searchButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        searchButton.setPreferredSize(new Dimension(45, 36));
        searchButton.setBackground(new Color(18, 18, 18));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setBorderPainted(false);
        searchButton.setBounds(12, 13, 30, 25);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if(mode == Mode.ADMIN && mainTable != null) {
            SearchBar searchBar = new SearchBar(mainTable, contentPanel, reloadDataCallback);
            searchBar.setupAutocomplete(searchField);
            searchButton.addActionListener(e -> searchBar.searching(panel, searchField.getText()));
            searchField.addActionListener(e -> searchBar.searching(panel, searchField.getText()));
        }

        panel.add(searchField);
        panel.add(searchButton);

        return panel;
    }

    public Long getUserId() {
        CreateAccount currentUser = UserSession.getInstance().getCurrentUser();
        return currentUser != null ? currentUser.getUserId() : null;
    }

    private JPanel createRightSection(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        panel.setOpaque(false);

        if(mode == Mode.ADMIN || mode == Mode.SALES || mode == Mode.INVENTORY
        ) {

            if(mode != Mode.ADMIN){
                JButton homeButton = createButton("Home", this::home);
                homeButton.setBackground(new Color(25, 118, 210));
                homeButton.setForeground(new Color(255, 255, 255));
                sessionControlListener(homeButton, ProductFrame.class);
                panel.add(homeButton);
            }

            JButton pendingButton = createButton("Track Orders", this::trackOrders);
            pendingButton.setBackground(new Color(25, 118, 210));
            pendingButton.setForeground(new Color(255, 255, 255));
            sessionControlListener(pendingButton, PendingFrame.class);
        
            JButton salesButton = createButton("Sales", this::sales);
            salesButton.setBackground(new Color(25, 118, 210));
            salesButton.setForeground(new Color(255, 255, 255));
            sessionControlListener(salesButton, SalesFrame.class);

            JButton inventoryButton = createButton("Inventory", this::inventory);
            inventoryButton.setBackground(new Color(25, 118, 210));
            inventoryButton.setForeground(new Color(255, 255, 255));
            sessionControlListener(inventoryButton, InventoryFrame.class);
            
            panel.add(pendingButton);
            panel.add(salesButton);
            panel.add(inventoryButton);
            
            JPanel adminPanel = createUserPanel();
            panel.add(adminPanel);
            
        } else if(mode == Mode.BUYER || mode == Mode.CART 
            || mode == Mode.ORDERS) {

            if (mode != Mode.BUYER) {
                JButton homeButton = createButton("Home", this::cart);
                homeButton.setBackground(new Color(25, 118, 210));
                homeButton.setForeground(new Color(255, 255, 255));
                sessionControlListener(homeButton, ShopFrame.class);
                panel.add(homeButton);
            }
            
            JButton cartButton = createButton("Cart", this::cart);
            cartButton.setBackground(new Color(25, 118, 210));
            cartButton.setForeground(new Color(255, 255, 255));
            sessionControlListener(cartButton, CartFrame.class);

            cartBadgeUtil = NotifBadgeUtil.createNotifBadge(
                    cartButton, 
                    cartDAO.countCartItems(getUserId())
                );
            cartCountBadge = cartBadgeUtil.badge;

            JButton ordersButton = createButton("Orders", this::buyerOrders);
            ordersButton.setBackground(new Color(25, 118, 210));
            ordersButton.setForeground(new Color(255, 255, 255));
            sessionControlListener(ordersButton, OrdersFrame.class);
        
            panel.add(cartBadgeUtil.panel);
            panel.add(ordersButton);
        
            JPanel shopPanel = createUserPanel();
            panel.add(shopPanel);

        } else if(status == Status.PENDING || status == Status.PROCESSING || status == Status.SHIPPED){
        
            JButton homeButton = createButton("Home", this::home);
            homeButton.setBackground(new Color(25, 118, 210));
            homeButton.setForeground(new Color(255, 255, 255));
            sessionControlListener(homeButton, ProductFrame.class);
            panel.add(homeButton);
            

        JButton pendingButton = createButton("Pending", this::pending);
                pendingButton.setBackground(new Color(25, 118, 210));
                pendingButton.setForeground(new Color(255, 255, 255));
                sessionControlListener(pendingButton, PendingFrame.class);

                pendingBadgeUtil = NotifBadgeUtil.createNotifBadge(
                    pendingButton, 
                    ordersDAO.countOrdersPerStatus("Pending")
                );
                pendingCountBadge = pendingBadgeUtil.badge;

                JButton processingButton = createButton("Processing", this::processing);
                processingButton.setBackground(new Color(25, 118, 210));
                processingButton.setForeground(new Color(255, 255, 255));
                sessionControlListener(processingButton, ProcessingFrame.class);

                processingBadgeUtil = NotifBadgeUtil.createNotifBadge(
                    processingButton, 
                    ordersDAO.countOrdersPerStatus("Processing")  
                );
                processingCountBadge = processingBadgeUtil.badge;

                JButton shippedButton = createButton("Shipped", this::shipped);
                shippedButton.setBackground(new Color(25, 118, 210));
                shippedButton.setForeground(new Color(255, 255, 255));
                sessionControlListener(shippedButton, ShippedFrame.class);

                shippedBadgeUtil = NotifBadgeUtil.createNotifBadge(
                    shippedButton, 
                    ordersDAO.countOrdersPerStatus("Shipped")  // Fixed: was "Processed"
                );
                shippedCountBadge = shippedBadgeUtil.badge;

                panel.add(pendingBadgeUtil.panel);
                panel.add(processingBadgeUtil.panel);
                panel.add(shippedBadgeUtil.panel);

                JPanel statusPanel = createUserPanel();
                panel.add(statusPanel);
            }

        return panel;
    }   

    public void refreshCartCountBadge(){
        if (cartCountBadge != null) {
            int newCount = cartDAO.countCartItems(getUserId());
            cartCountBadge.setText(String.valueOf(newCount));
            cartCountBadge.setVisible(newCount > 0);
        }
    }

    public void refreshPendingBadge() {
        if (pendingCountBadge != null) {
            int newCount = ordersDAO.countOrdersPerStatus("Pending");
            pendingCountBadge.setText(String.valueOf(newCount));
            pendingCountBadge.setVisible(newCount > 0);
        }
    }

    public void refreshProcessingBadge() {
        if (processingCountBadge != null) {
            int newCount = ordersDAO.countOrdersPerStatus("Processing");
            processingCountBadge.setText(String.valueOf(newCount));
            processingCountBadge.setVisible(newCount > 0);
        }
    }

    public void refreshShippedBadge() {
        if (shippedCountBadge != null) {
            int newCount = ordersDAO.countOrdersPerStatus("Shipped");
            shippedCountBadge.setText(String.valueOf(newCount));
            shippedCountBadge.setVisible(newCount > 0);
        }
    }

    public void refreshAllBadges() {
        refreshPendingBadge();
        refreshProcessingBadge();
        refreshShippedBadge();
    }

    public void updateCountBadge(int newCount) {
        if (pendingCountBadge != null) {
            pendingCountBadge.setText(String.valueOf(newCount));
            pendingCountBadge.setVisible(newCount > 0);
        }
    }


    // OPEN FRAME DYNAMICALLY
   private void sessionControlListener(JButton btn, Class<? extends JFrame> frameClass) {
    btn.addActionListener(e -> {
        CreateAccount currentUser = UserSession.getInstance().getCurrentUser();

        if (currentUser != null && currentUser.getUserId() != null) {

            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor((Component) e.getSource());
            if (currentFrame != null) currentFrame.dispose();

            try {
                JFrame newFrame = frameClass.getConstructor(CreateAccount.class).newInstance(currentUser);
                newFrame.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Failed to open the page",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog((Component) e.getSource(),
                "Please login to access this page",
                "Login Required",
                JOptionPane.WARNING_MESSAGE);
        }
    });
}


    private JButton createButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(FontUtil.loadFontUtil().deriveFont(12f));
        button.setPreferredSize(new Dimension(120, 30));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> action.run());
        return button;
    }

    private JPanel createUserPanel(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        panel.setOpaque(false);

        ImageIcon originalIcon = new ImageIcon("resources\\img\\user.png");
        Image scaledImage = originalIcon.getImage().getScaledInstance(23, 20, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JButton adminBtn = new JButton(scaledIcon);
        adminBtn.setBackground(Color.BLACK);
        adminBtn.setFont(new Font("Arial", Font.BOLD, 13));
        adminBtn.setForeground(new Color(50, 50, 70));
        adminBtn.setPreferredSize(new Dimension(50, 40));
        adminBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        adminBtn.setIconTextGap(8);
        adminBtn.setBorderPainted(false);
        adminBtn.setFocusPainted(false);
        adminBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        adminBtn.setContentAreaFilled(false);

        adminBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                adminBtn.setContentAreaFilled(true);
                adminBtn.setBackground(new Color(245, 245, 250));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                adminBtn.setContentAreaFilled(false);
            }
        });

        panel.add(adminBtn);
        adminBtn.addActionListener(e -> showAdminMenu(adminBtn));

        return panel;
    }

    private void showAdminMenu(Component component){
        JPopupMenu menu = new JPopupMenu();
        menu.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 230), 1));

        JMenuItem profileMenuItem = createMenuItem("Profile", e -> openProfile());
        
        JMenuItem logoutItem = createMenuItem("Logout", e -> logout());

        menu.add(profileMenuItem);
        menu.add(logoutItem);

        menu.show(component, 0, component.getHeight() + 5);
    }

    private JMenuItem createMenuItem(String text, java.awt.event.ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(FontUtil.loadFontUtil().deriveFont(Font.PLAIN, 13f));
        item.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        item.addActionListener(action);
        return item;
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public void clearSearch() {
        if (searchField != null) {
            searchField.setText("");
        }
    }


    private void pending(){}
    private void processing(){}
    private void shipped(){}
  
    private void sales(){}
    private void inventory(){}

     private void home(){}

    // Buyer mode methods
    private void cart(){}
    private void buyerOrders(){}
    private void trackOrders(){}

   
    // Common methods
    private void openProfile(){
         CreateAccount currentUser = UserSession.getInstance().getCurrentUser();
         new UserProfileFrame(currentUser).setVisible(true);
         JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
           if (frame != null) {
                frame.dispose();
           }
    }
   
    
    private void logout(){
        int c = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", 
        "Confirm Logout", JOptionPane.YES_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            new CreateAccountFrame().setVisible(true);
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                frame.dispose();
            }
        }
    }
}