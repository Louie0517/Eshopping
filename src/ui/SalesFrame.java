package ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import dao.SalesDAO;
import model.CreateAccount;
import model.SalesReport;
import model.TopProduct;
import ui.components.TopBar;
import util.FrameUtil;

public class SalesFrame extends JFrame {
    // Modern Color Palette
    private static final Color BACKGROUND = new Color(248, 250, 252);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color PRIMARY_COLOR = new Color(99, 102, 241);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color INFO_COLOR = new Color(59, 130, 246);
    private static final Color PURPLE_COLOR = new Color(168, 85, 247);
    private static final Color ORANGE_COLOR = new Color(249, 115, 22);
    private static final Color TEXT_PRIMARY = new Color(30, 41, 59);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    
    private CreateAccount user;
    private JPanel mainPanel;
    private SalesDAO salesDAO;
    private JComboBox<String> periodSelector;
    private JPanel statsPanel;
    private JPanel chartsPanel;
    private JFreeChart revenueChart;
    private JFreeChart productsChart;
    private SalesReport currentReport;
    private List<TopProduct> currentTopProducts;
    private String currentPeriod;

    public SalesFrame(CreateAccount user) {
        this.user = user;
        this.salesDAO = new SalesDAO();
        setTitle("Eshopping - Sales Dashboard");
        setSize(1400, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND);

        initializeFrame();
        
        FrameUtil.addCloseConfirmation(this);
        setVisible(true);
    }

    // iconLabel
    private void initializeFrame() {
        createMainFrame();
        loadDashboard("Today");
    }

    private void createMainFrame() {
        mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        TopBar topBar = new TopBar("Eshopping", mainPanel, TopBar.Mode.SALES);
        add(topBar, BorderLayout.NORTH);

        // Control Panel with Download Button
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        // Stats Cards Panel
        statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setBackground(BACKGROUND);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        mainPanel.add(statsPanel, BorderLayout.CENTER);

        // Charts Panel
        chartsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsPanel.setBackground(BACKGROUND);
        chartsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        mainPanel.add(chartsPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setBackground(CARD_BG);

        JLabel label = new JLabel("View Period:");
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_PRIMARY);

        periodSelector = new JComboBox<>(new String[]{"Today", "This Month", "This Year", "All Time"});
        periodSelector.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        periodSelector.setPreferredSize(new Dimension(150, 38));
        periodSelector.setBackground(Color.WHITE);
        periodSelector.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        periodSelector.addActionListener(e -> {
            String selected = (String) periodSelector.getSelectedItem();
            loadDashboard(selected);
        });

        JButton refreshBtn = createButton("Refresh", INFO_COLOR);
        refreshBtn.addActionListener(e -> {
            String selected = (String) periodSelector.getSelectedItem();
            loadDashboard(selected);
        });

        leftPanel.add(label);
        leftPanel.add(periodSelector);
        leftPanel.add(refreshBtn);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(CARD_BG);

        JButton downloadCsvBtn = createButton("Download CSV", ORANGE_COLOR);
        downloadCsvBtn.addActionListener(e -> downloadReportAsCSV());

        JButton downloadChartsBtn = createButton("Save Charts", PURPLE_COLOR);
        downloadChartsBtn.addActionListener(e -> downloadCharts());

        
        rightPanel.add(downloadCsvBtn);
        rightPanel.add(downloadChartsBtn);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(160, 38));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private void loadDashboard(String period) {
        currentPeriod = period;
        statsPanel.removeAll();
        chartsPanel.removeAll();

        currentReport = loadSalesReport(period);
        currentTopProducts = loadTopProducts(period);

        if (currentReport != null) {
            statsPanel.add(createStatCards("Total Revenue", 
                String.format("₱%,.2f", currentReport.getTotalRevenue()), 
                "💰", SUCCESS_COLOR));
            statsPanel.add(createStatCards("Total Transactions", 
                String.valueOf(currentReport.getTotalTransactions()), 
                "🛒", INFO_COLOR));
            statsPanel.add(createStatCards("Items Sold", 
                String.valueOf(currentReport.getTotalItemsSold()), 
                "📦", PURPLE_COLOR));
            
            double avgTransaction = currentReport.getTotalTransactions() > 0 
                ? currentReport.getTotalRevenue() / currentReport.getTotalTransactions() 
                : 0;
            statsPanel.add(createStatCards("Avg Transaction", 
                String.format("₱%,.2f", avgTransaction), 
                "💳", ORANGE_COLOR));
        } else {
            statsPanel.add(createStatCards("Total Revenue", "₱0.00", "💰", SUCCESS_COLOR));
            statsPanel.add(createStatCards("Total Transactions", "0", "🛒", INFO_COLOR));
            statsPanel.add(createStatCards("Items Sold", "0", "📦", PURPLE_COLOR));
            statsPanel.add(createStatCards("Avg Transaction", "₱0.00", "💳", ORANGE_COLOR));
        }

        chartsPanel.add(createModernRevenueChart(period));
        chartsPanel.add(createModernTopProductsChart(currentTopProducts, period));

        statsPanel.revalidate();
        statsPanel.repaint();
        chartsPanel.revalidate();
        chartsPanel.repaint();
    }

    private SalesReport loadSalesReport(String period) {
        LocalDate now = LocalDate.now();
        
        switch (period) {
            case "Today":
                return salesDAO.getDailySalesReport(now);
            case "This Month":
                return salesDAO.getMonthlySalesReport(now.getYear(), now.getMonthValue());
            case "This Year":
                return salesDAO.getYearlySalesReport(now.getYear());
            case "All Time":
                return salesDAO.getTotalSalesReport();
            default:
                return null;
        }
    }

    private List<TopProduct> loadTopProducts(String period) {
        switch (period) {
            case "Today":
                return salesDAO.getTopSellingProductsToday(10);
            case "This Month":
                return salesDAO.getTopSellingProductsThisMonth(10);
            case "This Year":
                return salesDAO.getTopSellingProductsThisYear(10);
            case "All Time":
                return salesDAO.getTopSellingProductsAllTime(10);
            default:
                return List.of();
        }
    }

    private JPanel createStatCards(String title, String value, String icon, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(25, 20, 25, 20)
        ));

        JPanel topSection = new JPanel(new BorderLayout(10, 0));
        topSection.setBackground(CARD_BG);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setForeground(accentColor);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(TEXT_SECONDARY);

        topSection.add(iconLabel, BorderLayout.WEST);
        topSection.add(titleLabel, BorderLayout.CENTER);

        // Value label
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(TEXT_PRIMARY);

        card.add(topSection, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(accentColor, 2, true),
                    BorderFactory.createEmptyBorder(24, 19, 24, 19)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(25, 20, 25, 20)
                ));
            }
        });

        return card;
    }

    private JPanel createModernRevenueChart(String period) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        LocalDate now = LocalDate.now();

        switch (period) {
            case "Today":
                SalesReport todayReport = salesDAO.getDailySalesReport(now);
                dataset.addValue(todayReport != null ? todayReport.getTotalRevenue() : 0, 
                    "Revenue", "Today");
                break;
                
            case "This Month":
                for (int i = 29; i >= 0; i--) {
                    LocalDate date = now.minusDays(i);
                    SalesReport dayReport = salesDAO.getDailySalesReport(date);
                    double revenue = dayReport != null ? dayReport.getTotalRevenue() : 0;
                    dataset.addValue(revenue, "Revenue", 
                        date.format(DateTimeFormatter.ofPattern("MM/dd")));
                }
                break;
                
            case "This Year":
                for (int month = 1; month <= now.getMonthValue(); month++) {
                    SalesReport monthReport = salesDAO.getMonthlySalesReport(now.getYear(), month);
                    double revenue = monthReport != null ? monthReport.getTotalRevenue() : 0;
                    dataset.addValue(revenue, "Revenue", 
                        LocalDate.of(now.getYear(), month, 1)
                            .format(DateTimeFormatter.ofPattern("MMM")));
                }
                break;
                
            case "All Time":
                for (int year = now.getYear() - 4; year <= now.getYear(); year++) {
                    SalesReport yearReport = salesDAO.getYearlySalesReport(year);
                    double revenue = yearReport != null ? yearReport.getTotalRevenue() : 0;
                    dataset.addValue(revenue, "Revenue", String.valueOf(year));
                }
                break;
        }

        revenueChart = ChartFactory.createBarChart(
            "Revenue Trend - " + period,
            "Period",
            "Revenue (₱)",
            dataset,
            PlotOrientation.VERTICAL,
            false,
            true,
            false
        );

        styleChart(revenueChart, INFO_COLOR);

        ChartPanel chartPanel = new ChartPanel(revenueChart);
        chartPanel.setPreferredSize(new Dimension(650, 350));
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(CARD_BG);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        wrapper.add(chartPanel, BorderLayout.CENTER);
        
        return wrapper;
    }

    private JPanel createModernTopProductsChart(List<TopProduct> topProducts, String period) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        int limit = Math.min(10, topProducts.size());
        for (int i = 0; i < limit; i++) {
            TopProduct product = topProducts.get(i);
            String productName = product.getProductName();
            if (productName.length() > 15) {
                productName = productName.substring(0, 12) + "...";
            }
            dataset.addValue(product.getTotalQuantitySold(), "Quantity", productName);
        }

        if (topProducts.isEmpty()) {
            dataset.addValue(0, "Quantity", "No Data");
        }

        productsChart = ChartFactory.createBarChart(
            "Top Selling Products - " + period,
            "Product",
            "Quantity Sold",
            dataset,
            PlotOrientation.VERTICAL,
            false,
            true,
            false
        );

        styleChart(productsChart, SUCCESS_COLOR);

        ChartPanel chartPanel = new ChartPanel(productsChart);
        chartPanel.setPreferredSize(new Dimension(650, 350));
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(CARD_BG);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        wrapper.add(chartPanel, BorderLayout.CENTER);
        
        return wrapper;
    }

    private void styleChart(JFreeChart chart, Color barColor) {
        // Chart background
        chart.setBackgroundPaint(CARD_BG);
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));
        chart.getTitle().setPaint(TEXT_PRIMARY);
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(CARD_BG);
        plot.setRangeGridlinePaint(BORDER_COLOR);
        plot.setOutlineVisible(false);
        
        // Bar renderer with gradient effect
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, barColor);
        renderer.setShadowVisible(false);
        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
        
        // Axis styling
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        domainAxis.setTickLabelPaint(TEXT_SECONDARY);
        domainAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 12));
        domainAxis.setLabelPaint(TEXT_PRIMARY);
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        rangeAxis.setTickLabelPaint(TEXT_SECONDARY);
        rangeAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 12));
        rangeAxis.setLabelPaint(TEXT_PRIMARY);
    }
    
    private JPanel createPrintableReport() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        panel.setSize(800, 1100);
        
        // Title
        JLabel titleLabel = new JLabel("SALES REPORT - " + currentPeriod);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        
        panel.add(Box.createVerticalStrut(10));
        
        // Date
        JLabel dateLabel = new JLabel("Generated: " + LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm a")));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(TEXT_SECONDARY);
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(dateLabel);
        
        panel.add(Box.createVerticalStrut(30));
        
        // Summary Section
        JLabel summaryTitle = new JLabel("SUMMARY");
        summaryTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        summaryTitle.setForeground(TEXT_PRIMARY);
        panel.add(summaryTitle);
        
        panel.add(Box.createVerticalStrut(15));
        
        if (currentReport != null) {
            panel.add(createReportRow("Total Revenue:", String.format("₱%.2f", currentReport.getTotalRevenue())));
            panel.add(createReportRow("Total Transactions:", String.valueOf(currentReport.getTotalTransactions())));
            panel.add(createReportRow("Items Sold:", String.valueOf(currentReport.getTotalItemsSold())));
            double avg = currentReport.getTotalTransactions() > 0 
                ? currentReport.getTotalRevenue() / currentReport.getTotalTransactions() 
                : 0;
            panel.add(createReportRow("Average Transaction:", String.format("₱%.2f", avg)));
        }
        
        panel.add(Box.createVerticalStrut(30));
        
        // Top Products Section
        JLabel productsTitle = new JLabel("TOP SELLING PRODUCTS");
        productsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        productsTitle.setForeground(TEXT_PRIMARY);
        panel.add(productsTitle);
        
        panel.add(Box.createVerticalStrut(15));
        
        // Products table header
        JPanel headerPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        headerPanel.setBackground(BACKGROUND);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setMaximumSize(new Dimension(720, 40));
        
        String[] headers = {"Rank", "Product Name", "Qty Sold", "Revenue"};
        for (String header : headers) {
            JLabel label = new JLabel(header);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setForeground(TEXT_PRIMARY);
            headerPanel.add(label);
        }
        panel.add(headerPanel);
        
        // Products data
        int rank = 1;
        for (TopProduct product : currentTopProducts) {
            JPanel rowPanel = new JPanel(new GridLayout(1, 4, 10, 0));
            rowPanel.setBackground(Color.WHITE);
            rowPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            rowPanel.setMaximumSize(new Dimension(720, 40));
            
            rowPanel.add(createDataLabel(String.valueOf(rank++)));
            rowPanel.add(createDataLabel(product.getProductName()));
            rowPanel.add(createDataLabel(String.valueOf(product.getTotalQuantitySold())));
            rowPanel.add(createDataLabel(String.format("₱%.2f", product.getTotalRevenue())));
            
            panel.add(rowPanel);
        }
        
        panel.add(Box.createVerticalStrut(40));
        
        // Charts
        if (revenueChart != null) {
            JLabel chartTitle = new JLabel("REVENUE TREND");
            chartTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
            chartTitle.setForeground(TEXT_PRIMARY);
            panel.add(chartTitle);
            
            panel.add(Box.createVerticalStrut(10));
            
            BufferedImage chartImage = revenueChart.createBufferedImage(700, 300);
            JLabel chartLabel = new JLabel(new ImageIcon(chartImage));
            chartLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(chartLabel);
        }
        
        panel.setPreferredSize(new Dimension(800, 1100));
        
        return panel;
    }
    
    private JPanel createReportRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(720, 35));
        row.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelComp.setForeground(TEXT_SECONDARY);
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueComp.setForeground(TEXT_PRIMARY);
        
        row.add(labelComp, BorderLayout.WEST);
        row.add(valueComp, BorderLayout.EAST);
        
        return row;
    }
    
    private JLabel createDataLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private void downloadReportAsCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Sales Report as CSV");
        fileChooser.setSelectedFile(new File("Sales_Report_" + currentPeriod.replace(" ", "_") + "_" 
            + LocalDate.now() + ".csv"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                File fileToSave = fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();
                if (!filePath.endsWith(".csv")) {
                    filePath += ".csv";
                    fileToSave = new File(filePath);
                }
                
                FileWriter writer = new FileWriter(fileToSave);
                
                // Write header
                writer.write("SALES REPORT - " + currentPeriod + "\n");
                writer.write("Generated: " + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n\n");
                
                // Write summary
                writer.write("SUMMARY\n");
                writer.write("Metric,Value\n");
                if (currentReport != null) {
                    writer.write(String.format("Total Revenue,%.2f\n", currentReport.getTotalRevenue()));
                    writer.write(String.format("Total Transactions,%d\n", currentReport.getTotalTransactions()));
                    writer.write(String.format("Total Items Sold,%d\n", currentReport.getTotalItemsSold()));
                    double avg = currentReport.getTotalTransactions() > 0 
                        ? currentReport.getTotalRevenue() / currentReport.getTotalTransactions() 
                        : 0;
                    writer.write(String.format("Average Transaction,%.2f\n", avg));
                }
                
                // Write top products
                writer.write("\n\nTOP SELLING PRODUCTS\n");
                writer.write("Rank,Product Name,Quantity Sold,Total Revenue\n");
                
                int rank = 1;
                for (TopProduct product : currentTopProducts) {
                    writer.write(String.format("%d,%s,%d,%.2f\n", 
                        rank++,
                        product.getProductName().replace(",", ";"),
                        product.getTotalQuantitySold(),
                        product.getTotalRevenue()));
                }
                
                writer.close();
                
                JOptionPane.showMessageDialog(this, 
                    "CSV report saved successfully!\n" + fileToSave.getAbsolutePath(), 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error saving CSV: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void downloadCharts() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Charts as Images");
        fileChooser.setSelectedFile(new File("Charts_" + currentPeriod.replace(" ", "_") + "_" 
            + LocalDate.now()));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                File directory = fileChooser.getSelectedFile();
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                
                // Save revenue chart
                BufferedImage revenueImage = revenueChart.createBufferedImage(800, 600);
                File revenueFile = new File(directory, "Revenue_Chart.png");
                ImageIO.write(revenueImage, "png", revenueFile);
                
                // Save products chart
                BufferedImage productsImage = productsChart.createBufferedImage(800, 600);
                File productsFile = new File(directory, "Top_Products_Chart.png");
                ImageIO.write(productsImage, "png", productsFile);
                
                JOptionPane.showMessageDialog(this, 
                    "Charts saved successfully!\n" + directory.getAbsolutePath(), 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error saving charts: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}