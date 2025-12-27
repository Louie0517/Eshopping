package ui.components;

import dao.ProductDAO;
import model.Product;
import java.util.List;

public class Sorting {
    private final ProductDAO productDAO;

    public enum SortingCriteria {
        PRICE_ASC("Price: Low to High"),
        PRICE_DESC("Price: High to Low"),
        NAME_ASC("Name: A to Z"),
        NAME_DESC("Name: Z to A"),
        STOCK_ASC("Stock: Low to High"),
        STOCK_DESC("Stock: High to Low");

        private final String displayName;

        SortingCriteria(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public Sorting() {
        this.productDAO = new ProductDAO();
    }

    public List<Product> sortProducts(SortingCriteria criteria) {
        String orderBy;

        switch (criteria) {
            case PRICE_ASC:
                orderBy = "p.selling_price ASC";  
                break;
            case PRICE_DESC:
                orderBy = "p.selling_price DESC";
                break;
            case NAME_ASC:
                orderBy = "p.product_name ASC";
                break;
            case NAME_DESC:
                orderBy = "p.product_name DESC";
                break;
            case STOCK_ASC:
                orderBy = "p.stock ASC";
                break;
            case STOCK_DESC:
                orderBy = "p.stock DESC";
                break;
            default:
                orderBy = "p.id ASC";
        }

        return productDAO.getProductsSorted(orderBy);
    }
}