package ui;

import util.*;

import db.DatabaseConnection;
import model.CreateAccount;
import util.FontUtil;
import util.FrameUtil;

import javax.imageio.ImageIO;
import javax.swing.*;

import dao.LoginDAO;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;

public class LoginFrame extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;

    public LoginFrame() {
        setTitle("Eshopping");
        setSize(1200, 750);
        setLayout(new GridLayout(1, 2));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(250, 250, 252));
        FrameUtil.addCloseConfirmation(this);

        add(createLeftPanel());
        add(createRightPanel());

        setVisible(true);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(70, 100, 240));
        
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setOpaque(false);
        brandPanel.setBorder(BorderFactory.createEmptyBorder(60, 60, 40, 60));
        
        JLabel brandLabel = new JLabel("Eshopping");
        brandLabel.setFont(FontUtil.loadFontUtil().deriveFont(Font.BOLD, 36f));
        brandLabel.setForeground(Color.WHITE);
        brandLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel taglineLabel = new JLabel("Your trusted Ecommerce Shop");
        taglineLabel.setFont(FontUtil.loadFontUtil().deriveFont(Font.PLAIN, 16f));
        taglineLabel.setForeground(new Color(220, 230, 255));
        taglineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        brandPanel.add(brandLabel);
        brandPanel.add(Box.createVerticalStrut(10));
        brandPanel.add(taglineLabel);
       
        JPanel imagePanel = new JPanel(new GridBagLayout());
        imagePanel.setOpaque(false);
        
        JLabel imageLabel = createImageLabel();
        imageLabel.setBorder(BorderFactory.createEmptyBorder(-45, 0, 0, 0));
        imagePanel.add(imageLabel);
        
        JPanel featuresPanel = new JPanel();
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));
        featuresPanel.setOpaque(false);
        featuresPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 60, 60));
        
        addFeature(featuresPanel, " Secure authentication");
        addFeature(featuresPanel, " Fast and reliable");
        addFeature(featuresPanel, " 24/7 support");
        
        panel.add(brandPanel, BorderLayout.NORTH);
        panel.add(imagePanel, BorderLayout.CENTER);
        panel.add(featuresPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JLabel createImageLabel() {
        JLabel imageLabel = new JLabel();
        
        try {
            File imageFile = new File("resources/img/shop.png");
            if (imageFile.exists()) {
                BufferedImage img = ImageIO.read(imageFile);
                Image scaledImg = img.getScaledInstance(500, 490, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImg));
            } else {
                imageLabel.setText("🛍️");
                imageLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 120));
                imageLabel.setForeground(new Color(220, 230, 255));
            }
        } catch (Exception e) {
            imageLabel.setText("🛍️");
            imageLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 120));
            imageLabel.setForeground(new Color(220, 230, 255));
        }
        
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        return imageLabel;
    }
    
    private void addFeature(JPanel panel, String text) {
        JLabel featureLabel = new JLabel(text);
        featureLabel.setFont(FontUtil.loadFontUtil().deriveFont(Font.PLAIN, 14f));
        featureLabel.setForeground(new Color(220, 230, 255));
        featureLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        featureLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        panel.add(featureLabel);
    }

    private JButton createButton(String text, Color bgColor, Color fgColor){
        JButton button = new JButton(text);
        button.setFont(FontUtil.loadFontUtil().deriveFont(Font.BOLD, 14f));
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setPreferredSize(new Dimension(400, 50));
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (bgColor.equals(new Color(70, 100, 240))) {
                    button.setBackground(new Color(60, 85, 220));
                } else {
                    button.setBackground(new Color(230, 230, 235));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(FontUtil.loadFontUtil().deriveFont(Font.PLAIN, 14f));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setBackground(Color.WHITE);
        field.setPreferredSize(new Dimension(400, 45));
        return field;
    }
    
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(FontUtil.loadFontUtil().deriveFont(Font.PLAIN, 14f));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setBackground(Color.WHITE);
        field.setPreferredSize(new Dimension(400, 45));
        return field;
    }

    private void addFormField(JPanel panel, String labelText, JComponent field) {
        JPanel fieldContainer = new JPanel();
        fieldContainer.setLayout(new BoxLayout(fieldContainer, BoxLayout.Y_AXIS));
        fieldContainer.setOpaque(false);
        fieldContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldContainer.setMaximumSize(new Dimension(400, 80));
        
        JLabel label = new JLabel(labelText);
        label.setForeground(new Color(50, 50, 70));
        label.setFont(FontUtil.loadFontUtil().deriveFont(Font.BOLD, 13f));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(400, 45));
        
        fieldContainer.add(label);
        fieldContainer.add(Box.createVerticalStrut(8));
        fieldContainer.add(field);
        fieldContainer.add(Box.createVerticalStrut(15));
        
        panel.add(fieldContainer);
    }

    private JPanel createLoginPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(FontUtil.loadFontUtil().deriveFont(Font.BOLD, 32f));
        titleLabel.setForeground(new Color(30, 30, 45));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Please login to your account");
        subtitleLabel.setFont(FontUtil.loadFontUtil().deriveFont(Font.PLAIN, 19));
        subtitleLabel.setForeground(new Color(120, 120, 140));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);

        JPanel inputFieldPanel = new JPanel();
        inputFieldPanel.setLayout(new BoxLayout(inputFieldPanel, BoxLayout.Y_AXIS));
        inputFieldPanel.setOpaque(false);
        inputFieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUser = createStyledTextField();
        txtPass = createStyledPasswordField();

        addFormField(inputFieldPanel, "Username", txtUser);
        addFormField(inputFieldPanel, "Password", txtPass);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setMaximumSize(new Dimension(400, 50));
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnLogin = createButton("Login", new Color(70, 100, 240), Color.WHITE);
        btnLogin.addActionListener(e -> login());
        buttonsPanel.add(btnLogin);

        JPanel signupPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        signupPanel.setOpaque(false);
        signupPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        signupPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JLabel signupLabel = new JLabel("Don't have an account?");
        signupLabel.setFont(FontUtil.loadFontUtil().deriveFont(Font.PLAIN, 14f));
        signupLabel.setForeground(new Color(120, 120, 140));
        
        JLabel link = createHyperLink();
        
        signupPanel.add(signupLabel);
        signupPanel.add(link);

        panel.add(headerPanel);
        panel.add(inputFieldPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(buttonsPanel);
        panel.add(signupPanel);

        return panel;
    }

    private JLabel createHyperLink(){
        JLabel link = new JLabel("<html><a href=''>Sign up here</a></html>");
        link.setFont(new Font("Arial", Font.PLAIN, 15));
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));

        link.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                dispose();
                new CreateAccountFrame();
            }
        });

        return link;
    }

    private JPanel createRightPanel(){
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        JPanel loginPanel = createLoginPanel();
        loginPanel.setMaximumSize(new Dimension(400, 600));

        panel.add(loginPanel);
        return panel;
    }

    private void login() {
        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {

            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Cannot connect to database!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            PreparedStatement ps = conn.prepareStatement(
                "SELECT id, username, email, phone_no, role FROM users WHERE username=? AND password=?"
            );

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                CreateAccount user = new CreateAccount();
                user.setUserId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone_no"));
                user.setRole(rs.getString("role"));
                
                UserSession.getInstance().setCurrentUser(user);
                
                System.out.println("Login successful for: " + user.getUsername() + 
                                " (ID: " + user.getUserId() + ", Role: " + user.getRole() + ")");
                
                JOptionPane.showMessageDialog(this, 
                    "Login Successful! Welcome back, " + username + "!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                dispose();
                
                String role = user.getRole().toLowerCase();
                if (role.equals("admin")) {
                    new ProductFrame(); 
                } else {
                    new ShopFrame(user); 
                }
                
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
            rs.close();
            ps.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}