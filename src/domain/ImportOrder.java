package domain;

import java.time.LocalDate;

public class ImportOrder extends Order {
    private Supplier supplier;
    private String warehouseLocation;

    // Constructor khi tạo mới
    public ImportOrder(LocalDate orderDate, Supplier supplier, String warehouseLocation) {
        super(orderDate);
        this.supplier = supplier;
        this.warehouseLocation = warehouseLocation;
    }

    // Constructor khi load từ CSV
    public ImportOrder(String id, LocalDate orderDate, double totalAmount,
                       OrderStatus status, Supplier supplier, String warehouseLocation) {
        super(id, orderDate, totalAmount, status);
        this.supplier = supplier;
        this.warehouseLocation = warehouseLocation;
    }

    @Override
    public String getOrderType() {
        return "IMPORT";
    }

    @Override
    protected String generateOrderId() {
        return "IMP-" + System.currentTimeMillis();
    }

    @Override
    public void calculateTotal() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getSubtotal();
        }
        this.totalAmount = total;
    }

    @Override
    public String toCSV() {
        return String.format("%s,%s,%s,%.0f,%s,%s",
                id,
                supplier != null ? supplier.getId() : "",
                orderDate.format(DATE_FORMAT),
                totalAmount,
                status,
                warehouseLocation);
    }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
    public String getWarehouseLocation() { return warehouseLocation; }
    public void setWarehouseLocation(String warehouseLocation) { this.warehouseLocation = warehouseLocation; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | NCC: %s | Kho: %s",
                supplier != null ? supplier.getName() : "N/A", warehouseLocation);
    }
}
