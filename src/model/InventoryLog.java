package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InventoryLog {
    private int id;
    private int productId;
    private String productName;
    private int userId;
    private String username;
    private int changeQty;
    private String action; // "IN" or "OUT"
    private String reason;
    private LocalDateTime createdAt;

    public InventoryLog() {}

    public InventoryLog(int productId, int userId, int changeQty, String action, String reason) {
        this.productId = productId;
        this.userId = userId;
        this.changeQty = changeQty;
        this.action = action;
        this.reason = reason;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getChangeQty() {
        return changeQty;
    }

    public void setChangeQty(int changeQty) {
        this.changeQty = changeQty;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getFormattedDate() {
        if (createdAt != null) {
            return createdAt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
        }
        return "";
    }

    public boolean isIncoming() {
        return "IN".equals(action);
    }

    public boolean isOutgoing() {
        return "OUT".equals(action);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s%d units - %s",
            getFormattedDate(),
            productName != null ? productName : "Product #" + productId,
            isIncoming() ? "+" : "-",
            changeQty,
            reason != null ? reason : "No reason"
        );
    }
}