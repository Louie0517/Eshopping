package ui;

import util.UserSession;

import java.awt.*;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import authentication.CheckOutEmail;

import dao.CartDAO;
import dao.OrdersDAO;
import dao.ProductDAO;
import dao.SearchDAO;

import model.Cart;
import model.CreateAccount;
import model.Orders;
import model.Product;
import ui.components.ScrollBar;
import ui.components.Sorting;
import ui.components.TopBar;
import util.FrameUtil;

public class ShopFrame extends JFrame{
    private JPanel mainPanel;
    private JPanel productsContainer;
    private JScrollPane scrollPane;

    private ProductDAO productDAO = new ProductDAO();
    private SearchDAO searchDAO = new SearchDAO();
    private OrdersDAO ordersDAO = new OrdersDAO();
    
    private JComboBox<Sorting.SortingCriteria> sortComboBox;
    private List<Product> currentProducts;
    private TopBar topBar;
    private JTextField searchField;
    private Sorting sortCmp;
    private CreateAccount currentUser;
    
    public ShopFrame(CreateAccount user) {
        this.currentUser = user;
        UserSession.getInstance().setCurrentUser(user);
        
        System.out.println("ShopFrame created for user: " + user.getUsername());
        
        setTitle("Eshopping");
        setSize(1300, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initializeFrame();
        
        FrameUtil.addCloseConfirmation(this);
        setVisible(true);
    }
    
    public ShopFrame() {
        this.currentUser = UserSession.getInstance().getCurrentUser();
        
        if (this.currentUser == null) {
            System.out.println("WARNING: No user logged in. Creating test user.");
            this.currentUser = new CreateAccount();
            this.currentUser.setUserId(1L);
            this.currentUser.setUsername("Test User");
            UserSession.getInstance().setCurrentUser(this.currentUser);
        }
        
        System.out.println("ShopFrame created for user: " + currentUser.getUsername());
        
        setTitle("Eshopping");
        setSize(1300, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initializeFrame();
        
        FrameUtil.addCloseConfirmation(this);
        setVisible(true);
    }
    
    private void initializeFrame(){
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

      
        topBar = new TopBar("Eshopping", mainPanel, TopBar.Mode.BUYER);
        add(topBar, BorderLayout.NORTH);
        
   
        searchField = topBar.getSearchField();
        if (searchField != null) {
            setupSearchListener();
        }

        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        toolbarPanel.setBackground(Color.WHITE);
        
        JLabel sortLabel = new JLabel("Sort by:");
        sortLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sortLabel.setForeground(new Color(71, 85, 105));
        
        sortComboBox = new JComboBox<>(Sorting.SortingCriteria.values());
        sortComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sortComboBox.setBackground(Color.WHITE);
        sortComboBox.setForeground(new Color(51, 65, 85));
        sortComboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        sortComboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sortComboBox.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Sorting.SortingCriteria) {
                    setText(((Sorting.SortingCriteria) value).getDisplayName());
                }
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                if (isSelected) {
                    setBackground(new Color(241, 245, 249));
                    setForeground(new Color(15, 23, 42));
                }
                return this;
            }
        });
        
        sortCmp = new Sorting();
        JButton sortBtn = createSortBtn();
        sortBtn.addActionListener(e -> handleSort());
        
        toolbarPanel.add(sortLabel);
        toolbarPanel.add(sortComboBox);
        toolbarPanel.add(sortBtn);
        
        mainPanel.add(toolbarPanel, BorderLayout.NORTH);
      
        productsContainer = new JPanel(new GridLayout(0, 3, 20, 20));
        productsContainer.setBackground(Color.WHITE);
        
        currentProducts = productDAO.getProductsdetails();
        displayProducts(currentProducts);

        scrollPane = new JScrollPane(productsContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        ScrollBar.styleScrollBar(scrollPane);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void addToCart(Product product) {
        System.out.println("Current User: " + (currentUser != null ? currentUser.getUsername() + " (ID: " + currentUser.getUserId() + ")" : "null"));
        System.out.println("Product: " + product.getProductName() + " (ID: " + product.getId() + ")");
        
        if (!UserSession.getInstance().isLoggedIn()) {
            System.out.println("User not logged in!");
            JOptionPane.showMessageDialog(this,
                "Please login to add items to cart",
                "Login Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
      
        Cart cartItem = new Cart();
        cartItem.setUserId(currentUser.getUserId().intValue());
        cartItem.setProductId(product.getId());
        cartItem.setQty(1);
        cartItem.setBuyDate(java.time.LocalDate.now());
        
        System.out.println("Cart Item - User ID: " + cartItem.getUserId() + 
                        ", Product ID: " + cartItem.getProductId() + 
                        ", Qty: " + cartItem.getQty());
        
        CartDAO cartDAO = new CartDAO();
        boolean success = cartDAO.addCart(cartItem);
        
        System.out.println("Database insert result: " + success);
    
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                product.getProductName() + " added to cart!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
                 topBar.refreshCartCountBadge();
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to add item to cart",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
        private void setupSearchListener() {
            searchField.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyReleased(java.awt.event.KeyEvent e) {
                    String query = searchField.getText().trim();
                    performSearch(query);
                }
            });
    }
    
    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            currentProducts = productDAO.getProductsdetails();
            refreshProductDisplay(currentProducts);
            return;
        }
        
        try {
            List<Product> searchResults = searchDAO.search(query.trim());
            currentProducts = searchResults;
            refreshProductDisplay(currentProducts);
        } catch (Exception e) {
            System.out.println("Search error: " + e.getMessage());
            currentProducts = productDAO.getProductsdetails();
            refreshProductDisplay(currentProducts);
        }
    }

    private void handleSort() {
        Sorting.SortingCriteria selectedCriteria = 
            (Sorting.SortingCriteria) sortComboBox.getSelectedItem();
        
        if (selectedCriteria != null) {
            List<Product> sortedProducts = sortCmp.sortProducts(selectedCriteria);
            currentProducts = sortedProducts;
            refreshProductDisplay(sortedProducts);
        }
    }
    
    private JButton createSortBtn() {
        JButton sortBtn = new JButton("Apply");
        sortBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sortBtn.setForeground(Color.WHITE);
        sortBtn.setBackground(new Color(59, 130, 246));
        sortBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        sortBtn.setFocusPainted(false);
        sortBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        
        sortBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                sortBtn.setBackground(new Color(37, 99, 235));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                sortBtn.setBackground(new Color(59, 130, 246));
            }
        });
        
        return sortBtn;
    }
    
    private void displayProducts(List<Product> products) {
        for(Product product : products){
            productsContainer.add(productCardsPanel(product));
        }
    }
    
    private void refreshProductDisplay(List<Product> products) {
        productsContainer.removeAll();
        
        if (products.isEmpty()) {
            productsContainer.setLayout(new GridBagLayout());
            JPanel emptyStatePanel = createEmptyPanel();
            productsContainer.add(emptyStatePanel);
        } else {
            productsContainer.setLayout(new GridLayout(0, 3, 20, 20));
            displayProducts(products);
        }
        
        productsContainer.revalidate();
        productsContainer.repaint();
    }
    
    private JPanel createEmptyPanel() {
        JPanel emptyPanel = new JPanel();
        emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
        emptyPanel.setBackground(Color.WHITE);
        emptyPanel.setBorder(BorderFactory.createEmptyBorder(80, 40, 80, 40));
        
        // LARGE SEARCH ICON IF NOT FOUND
        JLabel iconLabel = new JLabel("🔍");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 90));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // TITLE
        JLabel titleLabel = new JLabel("No Products Found");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(31, 41, 55));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // DESCRIPTION
        JLabel descLabel = new JLabel("We couldn't find any products matching your search");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        descLabel.setForeground(new Color(107, 114, 128));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // CLEAR SEARCH BTTN
        JButton clearBtn = new JButton("Clear Search");
        clearBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setBackground(new Color(59, 130, 246));
        clearBtn.setBorder(BorderFactory.createEmptyBorder(12, 32, 12, 32));
        clearBtn.setFocusPainted(false);
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearBtn.setMaximumSize(new Dimension(180, 45));
        
        clearBtn.addActionListener(e -> {
            if (searchField != null) {
                searchField.setText("");
                performSearch(""); 
            }
        });
        
        clearBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                clearBtn.setBackground(new Color(37, 99, 235));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                clearBtn.setBackground(new Color(59, 130, 246));
            }
        });
        
        emptyPanel.add(iconLabel);
        emptyPanel.add(Box.createRigidArea(new Dimension(0, 24)));
        emptyPanel.add(titleLabel);
        emptyPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        emptyPanel.add(descLabel);
        emptyPanel.add(Box.createRigidArea(new Dimension(0, 32)));
        emptyPanel.add(clearBtn);
        
        return emptyPanel;
    }
    
    private JLabel renderImgLabel(Product product, int width, int height) {
        JLabel imgLabel = new JLabel();
        imgLabel.setHorizontalAlignment(JLabel.CENTER);
        imgLabel.setVerticalAlignment(JLabel.CENTER);
        imgLabel.setPreferredSize(new Dimension(width, height));
        imgLabel.setMinimumSize(new Dimension(width, height));
        imgLabel.setMaximumSize(new Dimension(width, height));

        String imgPath = product.getImagePath();

        if (imgPath != null && !imgPath.isEmpty()) {
            ImageIcon icon = new ImageIcon(imgPath);
            Image scaled = icon.getImage()
                    .getScaledInstance(width, height, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaled));
        } else {
            imgLabel.setText("🖼️");
            imgLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        }

        return imgLabel;
    }


private JPanel productCardsPanel(Product product){
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // IMAGE
        int IMG_HEIGHT = 180;
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setPreferredSize(new Dimension(0, IMG_HEIGHT));
        imagePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, IMG_HEIGHT));
        imagePanel.setMinimumSize(new Dimension(0, IMG_HEIGHT));
        imagePanel.setBackground(new Color(249, 250, 251));

        imagePanel.add(renderImgLabel(product, -1, IMG_HEIGHT), BorderLayout.CENTER);
        card.add(imagePanel);


        // CONTENT SECTION
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // NAME
        JLabel nameLabel = new JLabel(product.getProductName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        nameLabel.setForeground(new Color(17, 24, 39));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(nameLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // CATEGORY & BRAND ONE ROW
        JPanel metaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        metaPanel.setOpaque(false);
        metaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        metaPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        
        JLabel categoryLabel = new JLabel(product.getCategoryName());
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        categoryLabel.setForeground(new Color(107, 114, 128));

        JLabel separator = new JLabel(" • ");
        separator.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        separator.setForeground(new Color(107, 114, 128));

        JLabel brandLabel = new JLabel(product.getBrandName());
        brandLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        brandLabel.setForeground(new Color(107, 114, 128));
        
        metaPanel.add(categoryLabel);
        metaPanel.add(separator);
        metaPanel.add(brandLabel);

        contentPanel.add(metaPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // STOCK BADGE
        JPanel stockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        stockPanel.setOpaque(false);
        stockPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        stockPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        
        JLabel stockBadge = new JLabel(product.getStock() + " in stock");
        stockBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        stockBadge.setForeground(new Color(5, 150, 105));
        stockBadge.setOpaque(true);
        stockBadge.setBackground(new Color(209, 250, 229));
        stockBadge.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        
        stockPanel.add(stockBadge);
        contentPanel.add(stockPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 18)));

        // PRICE AND BUTTON ROW
        JPanel actionPanel = new JPanel(new BorderLayout(10, 0));
        actionPanel.setOpaque(false);
        actionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel priceLabel = new JLabel("₱" + String.format("%,.2f", product.getSellingPrice()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        priceLabel.setForeground(new Color(17, 24, 39));

        JButton addToCartBtn = createCartButton(product, card);

        actionPanel.add(priceLabel, BorderLayout.WEST);
        actionPanel.add(addToCartBtn, BorderLayout.EAST);

        contentPanel.add(actionPanel);
        card.add(contentPanel);

        // CARD HOVER EFFECT
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(249, 250, 251));
                card.setBorder(BorderFactory.createLineBorder(new Color(59, 130, 246), 1));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
            }
        });

        return card;
    }

    private JButton createCartButton(Product product, JPanel card){
        JButton addToCartBtn = new JButton("Add to Cart");
        addToCartBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addToCartBtn.setForeground(Color.WHITE);
        addToCartBtn.setBackground(new Color(59, 130, 246));
        addToCartBtn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        addToCartBtn.setFocusPainted(false);
        addToCartBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        addToCartBtn.addActionListener(e -> {
            System.out.println("Add to Cart clicked for: " + product.getProductName());
            addToCart(product);
        });
        
        addToCartBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                addToCartBtn.setBackground(new Color(37, 99, 235));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                addToCartBtn.setBackground(new Color(59, 130, 246));
            }
        });

        return addToCartBtn;
    }
}