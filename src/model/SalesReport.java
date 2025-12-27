package model;

import java.time.LocalDate;

public class SalesReport {
    private LocalDate reportDate;
    private double totalRevenue;
    private int totalTransactions;
    private int totalItemsSold;
    private String reportType;
    
    public SalesReport() {}
    
    public SalesReport(LocalDate reportDate, double totalRevenue, int totalTransactions, 
                      int totalItemsSold, String reportType) {
        this.reportDate = reportDate;
        this.totalRevenue = totalRevenue;
        this.totalTransactions = totalTransactions;
        this.totalItemsSold = totalItemsSold;
        this.reportType = reportType;
    }
    
   
    public LocalDate getReportDate() {
        return reportDate;
    }
    
    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }
    
    public double getTotalRevenue() {
        return totalRevenue;
    }
    
    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    
    public int getTotalTransactions() {
        return totalTransactions;
    }
    
    public void setTotalTransactions(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }
    
    public int getTotalItemsSold() {
        return totalItemsSold;
    }
    
    public void setTotalItemsSold(int totalItemsSold) {
        this.totalItemsSold = totalItemsSold;
    }
    
    public String getReportType() {
        return reportType;
    }
    
    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
    
    @Override
    public String toString() {
        return String.format(
            "Sales Report [%s]\nDate: %s\nTotal Revenue: $%.2f\nTotal Transactions: %d\nTotal Items Sold: %d",
            reportType, reportDate, totalRevenue, totalTransactions, totalItemsSold
        );
    }
}