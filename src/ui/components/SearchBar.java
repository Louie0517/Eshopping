package ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.table.DefaultTableModel;

import dao.SearchDAO;
import model.Product;
import util.FontUtil;

public class SearchBar {
    private JWindow suggestionWindow;
    private JTable suggestionTable;
    private JTable mainTable;
    private JPanel mainPanel;
    private Runnable reloadDataCallback; 

    
    public SearchBar(JTable mainTable, JPanel mainPanel, Runnable reloadDataCallback) {
        this.mainTable = mainTable;
        this.mainPanel = mainPanel;
        this.reloadDataCallback = reloadDataCallback;
    }

 
    public SearchBar() {
    }

    public void searching(JPanel panel, String key) {
        if (key == null || key.trim().isEmpty()) {
            
            restoreOriginalData();
            return;
        }

        try {
            SearchDAO searchDAO = new SearchDAO();
            List<Product> query = searchDAO.search(key.trim());

            if (query.isEmpty()) {
                showNoResultsInTable(key);
            } else {
                updateTableWithResults(query);
            }
        } catch (Exception e) {
            System.out.print(e);
            JOptionPane.showMessageDialog(panel, "Search failed: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

   
    public void setupAutocomplete(JTextField searchField) {
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText().trim();
                
             
                if (text.isEmpty()) {
                    hideSuggestions();
                    restoreOriginalData();
                    return;
                }
                
                if (e.getKeyCode() != KeyEvent.VK_ENTER) {
                    showSuggestions(searchField, text);
                }
            }
        });
    }

    private void restoreOriginalData() {
        if (reloadDataCallback != null) {
            reloadDataCallback.run();
        }
        removeNoResultsMessage();
    }

    private void showSuggestions(JTextField searchField, String keyword) {
        try {
            SearchDAO searchDAO = new SearchDAO();
            List<Product> suggestions = searchDAO.search(keyword);

            if (suggestions.isEmpty()) {
                hideSuggestions();
                return;
            }

          
            if (suggestionWindow == null) {
                suggestionWindow = new JWindow();
                suggestionWindow.setFocusableWindowState(false);
            }

          
            String[] columnNames = {"Name", "Category", "Brand", "Supplier"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

        
            int limit = Math.min(suggestions.size(), 5);
            for (int i = 0; i < limit; i++) {
                Product p = suggestions.get(i);
                Object[] rowData = {p.getProductName(), p.getCategoryName(), 
                                   p.getBrandName(), p.getSupplierName()};
                model.addRow(rowData);
            }

            suggestionTable = new JTable(model);
            suggestionTable.setFont(FontUtil.loadFontUtil().deriveFont(Font.PLAIN, 12f));
            suggestionTable.setRowHeight(25);
            suggestionTable.setShowGrid(false);
            suggestionTable.setSelectionBackground(new Color(230, 240, 255));

          
            suggestionTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int row = suggestionTable.getSelectedRow();
                    if (row >= 0) {
                        String productName = suggestionTable.getValueAt(row, 0).toString();
                        searchField.setText(productName);
                        hideSuggestions();
                       
                        searching(mainPanel, productName);
                    }
                }
            });

            JScrollPane scrollPane = new JScrollPane(suggestionTable);
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 210)));
            ScrollBar.styleScrollBar(scrollPane);

            suggestionWindow.getContentPane().removeAll();
            suggestionWindow.getContentPane().add(scrollPane);
            
            int width = searchField.getWidth();
            suggestionWindow.setSize(width + 100, Math.min(150, limit * 25 + 25));
            
            java.awt.Point location = searchField.getLocationOnScreen();
            suggestionWindow.setLocation(location.x, location.y + searchField.getHeight());
            suggestionWindow.setVisible(true);

        } catch (Exception e) {
            hideSuggestions();
        }
    }

    private void hideSuggestions() {
        if (suggestionWindow != null) {
            suggestionWindow.setVisible(false);
        }
    }

    private void showNoResultsInTable(String searchKey) {
        if (mainTable == null || mainPanel == null) {
            JOptionPane.showMessageDialog(mainPanel, "No results found for: \"" + searchKey + "\"",
                "Search Results", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) mainTable.getModel();
        model.setRowCount(0);

        
        showNoResultsMessage(searchKey);
    }

    private void showNoResultsMessage(String searchKey) {
      
        removeNoResultsMessage();

        JPanel noResultPanel = new JPanel(new BorderLayout());
        noResultPanel.setName("noResultPanel");
        noResultPanel.setBackground(Color.WHITE);
        noResultPanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));

        JLabel noResultLabel = new JLabel("No results found for: \"" + searchKey + "\"", JLabel.CENTER);
        noResultLabel.setFont(FontUtil.loadFontUtil().deriveFont(Font.PLAIN, 14f));
        noResultLabel.setForeground(new Color(100, 100, 100));

        noResultPanel.add(noResultLabel, BorderLayout.CENTER);

        mainPanel.add(noResultPanel, BorderLayout.SOUTH);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void removeNoResultsMessage() {
        if (mainPanel != null) {
            for (int i = 0; i < mainPanel.getComponentCount(); i++) {
                if (mainPanel.getComponent(i).getName() != null && 
                    mainPanel.getComponent(i).getName().equals("noResultPanel")) {
                    mainPanel.remove(i);
                    mainPanel.revalidate();
                    mainPanel.repaint();
                    break;
                }
            }
        }
    }

    private void updateTableWithResults(List<Product> results) {
        if (mainTable == null) {
           
            JOptionPane.showMessageDialog(mainPanel, 
                "Found " + results.size() + " result(s)", 
                "Search Results", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        removeNoResultsMessage();

      
        DefaultTableModel model = (DefaultTableModel) mainTable.getModel();
        model.setRowCount(0); 

        for (Product p : results) {
            Object[] rowData = {
                p.getId(),
                p.getProductName(), 
                p.getCategoryName(), 
                p.getBrandName(), 
                p.getSupplierName(),
                p.getUnit(),
                String.format("₱%,.2f", p.getPrice()),
                p.getStock(),
                p.getMarkUp(),
                String.format("₱%,.2f", p.getSellingPrice()),
                p.getImagePath(),
                p.getDateAdded()
            };
            model.addRow(rowData);
        }
        if (mainTable.getRowCount() > 0) {
            mainTable.scrollRectToVisible(mainTable.getCellRect(0, 0, true));
        }
    }
}