package ui;

import com.toedter.calendar.JDateChooser;
import dao.CreateAccountDAO;
import model.CreateAccount;
import ui.components.ScrollBar;
import util.ConvertUtil;
import util.FontUtil;
import util.FrameUtil;
import authentication.ConfirmationEmail;
import validator.UserFormValidator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.MaskFormatter;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import java.text.ParseException;

public class CreateAccountFrame extends JFrame {

    private CreateAccountDAO createAccountDAO = new CreateAccountDAO();
  
    private CreateAccount user;
    private ConvertUtil convertUtil;
    private UserFormValidator formValidator;
    
    private JTextField userNameField;
    private JTextField emailField;
    private JFormattedTextField phoneField;
    private JPasswordField passwordField;
    private JDateChooser dateChooser;
    private JTextField addressField;
    private JComboBox<String> genderComboBox;
    private JComboBox<String> roleComboBox;
    private JTextArea bioArea;
  
    private JLabel profileLabel;
    private File selectedImageFile;
    
    private JButton confirmBtn;
    private JButton cancelBtn;

    public CreateAccountFrame() {
        initializeFrame();
        initializeComponents();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("Eshopping");
        setSize(1200, 750);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(250, 250, 252));
        
        FrameUtil.addCloseConfirmation(this);
    
        convertUtil = new ConvertUtil();
        formValidator = new UserFormValidator();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
      
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(new Color(250, 250, 252));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        
        JPanel headerPanel = createHeaderPanel();
      
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        
        JPanel leftPanel = createLeftPanel();
        JPanel rightPanel = createRightPanel();
        
        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);
        
        JPanel actionPanel = createActionPanel();
        
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);
        mainContainer.add(actionPanel, BorderLayout.SOUTH);
        
        add(mainContainer);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Create Your Account");
        titleLabel.setFont(FontUtil.loadFontUtil().deriveFont(Font.BOLD, 32f));
        titleLabel.setForeground(new Color(30, 30, 45));
        
        JLabel subtitleLabel = new JLabel("Join Eshopping today and start your journey");
        subtitleLabel.setFont(FontUtil.loadFontUtil().deriveFont(Font.PLAIN, 14f));
        subtitleLabel.setForeground(new Color(120, 120, 140));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);
        
        panel.add(textPanel, BorderLayout.WEST);
        
        return panel;
    }
   
    private JPanel createLeftPanel() {
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        innerPanel.setOpaque(false);

        userNameField = createStyledTextField();
        emailField = createStyledTextField();
        passwordField = createStyledPasswordField();
        phoneField = createStyledPhoneField();
        dateChooser = createStyledDateChooser();
        addressField = createStyledTextField();
        genderComboBox = createStyledGenderDropDown();
        roleComboBox = createStyledRoleDropDown();

        addFormField(innerPanel, "Username", userNameField, "Enter your username");
        addFormField(innerPanel, "Email Address", emailField, "your.email@example.com");
        addFormField(innerPanel, "Password", passwordField, "Create a strong password");
        addFormField(innerPanel, "Phone Number", phoneField, "___-___-____");
        addFormField(innerPanel, "Date of Birth", dateChooser, null);
        addFormField(innerPanel, "Address", addressField, "Enter your address");
        addFormField(innerPanel, "Gender", genderComboBox, null);
        addFormField(innerPanel, "Account Role", roleComboBox, null);

        JScrollPane scrollPane = new JScrollPane(innerPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        ScrollBar.styleScrollBar(scrollPane);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(scrollPane, BorderLayout.CENTER);

        return wrapper;
    }

   
    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JPanel profilePanel = createProfilePanel();
        JPanel bioPanel = createBioPanel();

        profilePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(profilePanel);
        panel.add(Box.createVerticalStrut(30));
        panel.add(bioPanel);

        return panel;
    }

    
    private void addFormField(JPanel panel, String labelText, JComponent field, String placeholder) {
        JPanel fieldContainer = new JPanel();
        fieldContainer.setLayout(new BoxLayout(fieldContainer, BoxLayout.Y_AXIS));
        fieldContainer.setOpaque(false);
        fieldContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldContainer.setMaximumSize(new Dimension(500, 85));
        
        JLabel label = new JLabel(labelText);
        label.setForeground(new Color(50, 50, 70));
        label.setFont(FontUtil.loadFontUtil().deriveFont(Font.BOLD, 13f));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(500, 45));
        field.setPreferredSize(new Dimension(500, 45));
        
        if (placeholder != null && field instanceof JTextField) {
            ((JTextField) field).putClientProperty("JTextField.placeholderText", placeholder);
        }
        
        fieldContainer.add(label);
        fieldContainer.add(Box.createVerticalStrut(8));
        fieldContainer.add(field);
        fieldContainer.add(Box.createVerticalStrut(20));
        
        panel.add(fieldContainer);
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(FontUtil.loadFontUtil().deriveFont(Font.PLAIN, 14f));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setBackground(Color.WHITE);
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
        return field;
    }

    private JFormattedTextField createStyledPhoneField() {
        try {
            MaskFormatter phoneMask = new MaskFormatter("###-###-####");
            phoneMask.setPlaceholderCharacter('_');
            JFormattedTextField field = new JFormattedTextField(phoneMask);
            field.setFont(FontUtil.loadFontUtil().deriveFont(Font.PLAIN, 14f));
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            field.setBackground(Color.WHITE);
            return field;
        } catch (ParseException e) {
            e.printStackTrace();
            return new JFormattedTextField();
        }
    }
    
    private JDateChooser createStyledDateChooser() {
        JDateChooser chooser = new JDateChooser();
        chooser.setFont(FontUtil.loadFontUtil().deriveFont(Font.PLAIN, 14f));
        chooser.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        chooser.setBackground(Color.WHITE);
        return chooser;
    }

    private JComboBox<String> createStyledGenderDropDown() {
        String[] genders = {"Male", "Female", "Others"};
        JComboBox<String> comboBox = new JComboBox<>(genders);
        comboBox.setSelectedIndex(-1);
        comboBox.setEditable(false);
        comboBox.setFont(FontUtil.loadFontUtil().deriveFont(Font.PLAIN, 14f));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        comboBox.setBackground(Color.WHITE);
        return comboBox;
    }

    private JComboBox<String> createStyledRoleDropDown() {
        String[] roles = {"Buyer", "Admin"};
        JComboBox<String> comboBox = new JComboBox<>(roles);
        comboBox.setSelectedIndex(-1);
        comboBox.setEditable(false);
        comboBox.setFont(FontUtil.loadFontUtil().deriveFont(Font.PLAIN, 14f));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        comboBox.setBackground(Color.WHITE);
        return comboBox;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        
        JLabel sectionLabel = new JLabel("Profile Picture");
        sectionLabel.setForeground(new Color(50, 50, 70));
        sectionLabel.setFont(FontUtil.loadFontUtil().deriveFont(Font.BOLD, 13f));
        sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel imageContainer = new JPanel(new BorderLayout());
        imageContainer.setBackground(Color.WHITE);
        imageContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        imageContainer.setMaximumSize(new Dimension(500, 220));
        imageContainer.setPreferredSize(new Dimension(500, 220));

        profileLabel = new JLabel("No image selected", SwingConstants.CENTER);
        profileLabel.setForeground(new Color(150, 150, 170));
        profileLabel.setFont(FontUtil.loadFontUtil().deriveFont(Font.PLAIN, 13f));

        JButton selectBtn = new JButton("Choose Image");
        selectBtn.setFont(FontUtil.loadFontUtil().deriveFont(Font.BOLD, 13f));
        selectBtn.setForeground(new Color(70, 100, 240));
        selectBtn.setBackground(new Color(240, 243, 255));
        selectBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        selectBtn.setFocusPainted(false);
        selectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        selectBtn.addActionListener(e -> chooseProfileImage());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        btnPanel.add(selectBtn);

        imageContainer.add(profileLabel, BorderLayout.CENTER);
        imageContainer.add(btnPanel, BorderLayout.SOUTH);

        panel.add(sectionLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(imageContainer);

        return panel;
    }

    private void chooseProfileImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Profile Picture");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image files", "jpg", "jpeg", "png", "gif"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            try {
                BufferedImage image = ImageIO.read(selectedImageFile);
                Image scaledImage = image.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                profileLabel.setText("");
                profileLabel.setIcon(new ImageIcon(scaledImage));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Failed to load image. Please select a valid image file.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                selectedImageFile = null;
            }
        }
    }

    private JPanel createBioPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel bioLabel = new JLabel("Bio");
        bioLabel.setForeground(new Color(50, 50, 70));
        bioLabel.setFont(FontUtil.loadFontUtil().deriveFont(Font.BOLD, 13f));
        bioLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        bioArea = new JTextArea(6, 30);
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        bioArea.setFont(FontUtil.loadFontUtil().deriveFont(Font.PLAIN, 14f));
        bioArea.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JScrollPane scrollPane = new JScrollPane(bioArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true));
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setMaximumSize(new Dimension(500, 150));
        scrollPane.setPreferredSize(new Dimension(500, 150));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(bioLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(scrollPane);

        return panel;
    }

    private JLabel createHyperLink(){
        JLabel link = new JLabel("<html><a href=''>Login Account</a></html>");
        link.setFont(new Font("Arial", Font.PLAIN, 15));
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));

        

        link.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        return link;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonsPanel.setOpaque(false);
        
        cancelBtn = createButton("Cancel", new Color(240, 240, 245), new Color(50, 50, 70));
        confirmBtn = createButton("Create Account", new Color(70, 100, 240), Color.WHITE);

        confirmBtn.addActionListener(e -> handleSubmit());
        cancelBtn.addActionListener(e -> handleCancel());

        buttonsPanel.add(cancelBtn);
        buttonsPanel.add(confirmBtn);
        
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        loginPanel.setOpaque(false);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JLabel subtitleLabel = new JLabel("Already have an account?");
        subtitleLabel.setFont(FontUtil.loadFontUtil().deriveFont(Font.PLAIN, 14f));
        subtitleLabel.setForeground(new Color(120, 120, 140));
        
        JLabel link = createHyperLink();
        
        loginPanel.add(subtitleLabel);
        loginPanel.add(link);
       
        buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(buttonsPanel);
        panel.add(loginPanel);

        return panel;
    }

    private JButton createButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(FontUtil.loadFontUtil().deriveFont(Font.BOLD, 14f));
        button.setForeground(fgColor);
        button.setBackground(bgColor);
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

    private void handleSubmit() {
        
        user = new CreateAccount(
            userNameField.getText().trim(),
            emailField.getText().trim(),
            new String(passwordField.getPassword()).trim(),
            phoneField.getText().trim(),
            convertUtil.dateChooserUtil(dateChooser),
            addressField.getText().trim(),
            convertUtil.genderDropDownUtil(genderComboBox),
            convertUtil.roleDropDownUtil(roleComboBox),
            bioArea.getText().trim(),
            convertUtil.selectedImagePathUtil(selectedImageFile)
        );

        if (formValidator.hasMissingField(user)) {
            showError("All fields are required");
            return;
        }

        if (formValidator.isUsernameTooShort(user)) {
            showError("Username must be at least 10 characters");
            return;
        }
        
        // modified
        if (!formValidator.isValidEmail(user.getEmail())) {
            showError("Invalid email format");
            return;
        }

        if(createAccountDAO.isEmailExists(user.getEmail())){
            showError("Email Already Exists");
            return;
        }

        if (!formValidator.isStrongPassword(user.getPassword())) {
            showError("Password must be greater than 9 characters with capital letters and special characters like '@");
            return;
        }

        java.util.Date dobAsDate = java.sql.Date.valueOf(convertUtil.dateChooserUtil(dateChooser));
        if (!formValidator.isValidBirthday(dobAsDate)) {
            showError("You must be 15 years old and above");
            return;
        }

        if (!formValidator.isValidImage(selectedImageFile)) {
            showError("Invalid image file. Please select a valid image (JPG, PNG, GIF)");
            return;
        }

        if (formValidator.isUsernameTooRepetitive(user)) {
            showError("Username must not be repetitive");
            return;
        }
        try {
            CreateAccountDAO userAccountDAO = new CreateAccountDAO();
            System.out.println("✔ Validation passed. Proceeding to INSERT...");

            userAccountDAO.addUser(user);
            ConfirmationEmail.sendEmail(user.getEmail());
            new LoginFrame();
            dispose();
            
            JOptionPane.showMessageDialog(this, 
                "Account created successfully! A confirmation email has been sent.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            handleCancel();
        } catch (Exception e) {
            showError("Failed to create account. Please try again.");
            e.printStackTrace();
        }
    }

    private void handleCancel() {
        userNameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        phoneField.setText("");
        addressField.setText("");
        dateChooser.setDate(null);
        genderComboBox.setSelectedIndex(-1);
        roleComboBox.setSelectedIndex(-1);
        bioArea.setText("");
        profileLabel.setIcon(null);
        profileLabel.setText("No image selected");
        selectedImageFile = null;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    
}