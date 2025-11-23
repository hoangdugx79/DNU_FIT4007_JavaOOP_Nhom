package domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Food extends Product {
    private LocalDate expiryDate;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Food(String name, String category, double importPrice, double salePrice,
                int stockQuantity, LocalDate expiryDate) {
        super(name, category, importPrice, salePrice, stockQuantity);
        this.expiryDate = expiryDate;
    }

    public Food(String id, String name, String category, double importPrice,
                double salePrice, int stockQuantity, LocalDate expiryDate) {
        super(id, name, category, importPrice, salePrice, stockQuantity);
        this.expiryDate = expiryDate;
    }

    @Override
    public double calculateProfit() {
        // Food: lợi nhuận 10-15% (thấp hơn vì dễ hỏng)
        double baseProfit = (salePrice - importPrice) * stockQuantity;
        // Nếu gần hết hạn (< 30 ngày) thì giảm lợi nhuận
        if (isNearExpiry()) {
            return baseProfit * 0.7;
        }
        return baseProfit * 0.9;
    }

    @Override
    public String getProductType() {
        return "FOOD";
    }

    @Override
    public String toCSV() {
        return String.format("%s,%s,%s,%s,%.0f,%.0f,%d,%s,",
                id, getProductType(), name, category, importPrice, salePrice, stockQuantity,
                expiryDate.format(DATE_FORMAT));
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    public boolean isNearExpiry() {
        return LocalDate.now().plusDays(30).isAfter(expiryDate);
    }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    @Override
    public String toString() {
        String status = isExpired() ? "HẾT HẠN" : (isNearExpiry() ? "GẦN HẾT HẠN" : "CÒN HẠN");
        return super.toString() + String.format(" | %s | HSD: %s [%s]",
                getProductType(), expiryDate.format(DATE_FORMAT), status);
    }
}
