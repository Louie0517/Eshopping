package ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import model.CreateAccount;
import ui.CreateAccountFrame;
import util.FontUtil;
import util.FrameUtil;

public class TopBar extends JPanel {
    private JLabel shopLabel;
    private JLabel adminLabel;
    private JButton menuButton;


    public void topBar(String shopName, String adminUsername ){
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 60));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 230)));

        JPanel leftPanel = createLeftSection(shopName);
        JPanel centerPanel = createCenterSection(); // for search
        JPanel rightPanel = createRightSection(adminUsername);

        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

    }

    private JPanel createLeftSection(String shopName){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        panel.setOpaque(false);

        JLabel logoLabel = new JLabel("\"🛍️");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        shopLabel = new JLabel(shopName);
        shopLabel.setFont(FontUtil.loadFontUtil().deriveFont(Font.BOLD, 18f));
        shopLabel.setForeground(new Color(30, 30, 45));

        panel.add(logoLabel);
        panel.add(shopLabel);
        return panel;

    }

    private JPanel createCenterSection(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 12));
        panel.setOpaque(false);

        JTextField searchField = new JTextField(25);
        searchField.setFont(FontUtil.loadFontUtil().deriveFont(Font.PLAIN, 13f));
        searchField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
        BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        searchField.putClientProperty("JTextField.placeholderText", "Search...");
        // search logic
        return panel;
    }


    private JPanel createRightSection(String adminUsername){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        panel.setOpaque(false);

        JButton dashboardButton = new JButton("Dashboard");
        dashboardButton.addActionListener(e -> dashBoard());

        JButton ordersButton = new JButton("Orders");
        ordersButton.addActionListener(e -> orders());

        JButton salesButton = new JButton("Sales");
        salesButton.addActionListener(e -> sales());

        JButton inventoryButton = new JButton("Inventory");
        inventoryButton.addActionListener(e -> inventory());
        
        JPanel adminPanel = createAdminPanel(adminUsername);
        
        panel.add(dashboardButton);
        panel.add(ordersButton);
        panel.add(salesButton);
        panel.add(inventoryButton);
        panel.add(adminPanel);
        return panel;

    }

    private JPanel createAdminPanel(String adminUsername){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel adminAvtLabel = new JLabel("👤");
        adminAvtLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        adminLabel = new JLabel(adminUsername);
        adminLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
        adminLabel.setForeground(new Color(50, 50, 70));

        JLabel dropdownLabel = new JLabel("▼");
        dropdownLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        dropdownLabel.setForeground(new Color(50, 50, 70));

        panel.add(adminAvtLabel);
        panel.add(adminLabel);
        panel.add(dropdownLabel);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showAdminMenu(panel);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setOpaque(true);
                panel.setBackground(new Color(245, 245, 250));
                panel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setOpaque(false);
                panel.repaint();
            }
        });

        return panel;
    }

    private void showAdminMenu(Component component){
        JPopupMenu menu = new JPopupMenu();
        menu.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 230), 1));

        JMenuItem profilMenuItem = createMenuItem("👤  Profile", e -> openProfile());
        JMenuItem accountSettingsItem = createMenuItem("⚙️  Account Settings", e -> openAccountSettings());
        JMenuItem logoutItem = createMenuItem("🚪  Logout", e -> logout());

        menu.add(profilMenuItem);
        menu.add(accountSettingsItem);
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

    private void openProfile(){}
    private void openAccountSettings(){
        
    }
    private void logout(){
        int c = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", 
        "Confirm Logout", JOptionPane.YES_OPTION);
        if(c == JOptionPane.YES_OPTION) new CreateAccountFrame();
    }

}

/// order, dash frame etc 
