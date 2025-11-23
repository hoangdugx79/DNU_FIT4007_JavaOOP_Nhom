package domain;

public class Furniture extends Product {
    private String dimensions; // e.g., "200x100x50"
    private double weight; // kg

    public Furniture(String name, String category, double importPrice, double salePrice,
                     int stockQuantity, String dimensions, double weight) {
        super(name, category, importPrice, salePrice, stockQuantity);
        this.dimensions = dimensions;
        this.weight = weight;
    }

    public Furniture(String id, String name, String category, double importPrice,
                     double salePrice, int stockQuantity, String dimensions, double weight) {
        super(id, name, category, importPrice, salePrice, stockQuantity);
        this.dimensions = dimensions;
        this.weight = weight;
    }

    @Override
    public double calculateProfit() {
        // Furniture: lợi nhuận 20-25%
        return (salePrice - importPrice) * stockQuantity;
    }

    @Override
    public String getProductType() {
        return "FURNITURE";
    }

    @Override
    public String toCSV() {
        return String.format("%s,%s,%s,%s,%.0f,%.0f,%d,%s,%.1f",
                id, getProductType(), name, category, importPrice, salePrice, stockQuantity,
                dimensions, weight);
    }

    public String getDimensions() { return dimensions; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | %s | Kích thước: %s cm | Trọng lượng: %.1f kg",
                getProductType(), dimensions, weight);
    }
}
