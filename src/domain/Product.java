package domain;

import java.util.UUID;

public abstract class Product {
    protected String id;
    protected String name;
    protected String category;
    protected double importPrice;
    protected double salePrice;
    protected int stockQuantity;

    // Constructor khi tạo mới
    public Product(String name, String category, double importPrice, double salePrice, int stockQuantity) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.name = name;
        this.category = category;
        this.importPrice = importPrice;
        this.salePrice = salePrice;
        this.stockQuantity = stockQuantity;
    }

    // Constructor khi load từ CSV
    public Product(String id, String name, String category, double importPrice, double salePrice, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.importPrice = importPrice;
        this.salePrice = salePrice;
        this.stockQuantity = stockQuantity;
    }

    // Abstract method - mỗi loại sản phẩm tính lợi nhuận khác nhau
    public abstract double calculateProfit();

    // Abstract method - lấy loại sản phẩm
    public abstract String getProductType();

    // Tăng tồn kho (khi nhập hàng)
    public void increaseStock(int quantity) {
        this.stockQuantity += quantity;
    }

    // Giảm tồn kho (khi xuất hàng)
    public void decreaseStock(int quantity) {
        this.stockQuantity -= quantity;
    }

    // Kiểm tra còn đủ hàng không
    public boolean hasEnoughStock(int quantity) {
        return this.stockQuantity >= quantity;
    }

    // Convert to CSV string
    public abstract String toCSV();

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getImportPrice() { return importPrice; }
    public void setImportPrice(double importPrice) { this.importPrice = importPrice; }
    public double getSalePrice() { return salePrice; }
    public void setSalePrice(double salePrice) { this.salePrice = salePrice; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    @Override
    public String toString() {
        return String.format("%-10s | %-25s | %-15s | %,15.0f | %,15.0f | %8d",
                id, name, category, importPrice, salePrice, stockQuantity);
    }
}
