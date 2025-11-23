package repository;

import domain.*;
import exception.OrderNotFoundException;
import interfaces.Persistable;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderRepository implements Persistable {
    private List<ImportOrder> importOrders;
    private List<ExportOrder> exportOrders;
    private Map<String, List<OrderItem>> orderItems; // orderId -> List<OrderItem>

    private final String importFilePath;
    private final String exportFilePath;
    private final String itemsFilePath;

    private CustomerRepository customerRepository;
    private SupplierRepository supplierRepository;
    private ProductRepository productRepository;

    public OrderRepository(String importFilePath, String exportFilePath, String itemsFilePath) {
        this.importFilePath = importFilePath;
        this.exportFilePath = exportFilePath;
        this.itemsFilePath = itemsFilePath;
        this.importOrders = new ArrayList<>();
        this.exportOrders = new ArrayList<>();
        this.orderItems = new HashMap<>();
    }

    // Inject dependencies
    public void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void setSupplierRepository(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void save() throws IOException {
        saveImportOrders();
        saveExportOrders();
        saveOrderItems();
    }

    private void saveImportOrders() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(importFilePath))) {
            // Header
            writer.write("orderId,supplierId,orderDate,totalAmount,status,warehouseLocation");
            writer.newLine();

            // Data
            for (ImportOrder order : importOrders) {
                writer.write(order.toCSV());
                writer.newLine();
            }
        }
    }

    private void saveExportOrders() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(exportFilePath))) {
            // Header
            writer.write("orderId,customerId,orderDate,totalAmount,status,deliveryAddress");
            writer.newLine();

            // Data
            for (ExportOrder order : exportOrders) {
                writer.write(order.toCSV());
                writer.newLine();
            }
        }
    }

    private void saveOrderItems() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(itemsFilePath))) {
            // Header
            writer.write("orderId,productId,quantity,unitPrice");
            writer.newLine();

            // Data
            for (Map.Entry<String, List<OrderItem>> entry : orderItems.entrySet()) {
                String orderId = entry.getKey();
                for (OrderItem item : entry.getValue()) {
                    writer.write(item.toCSV(orderId));
                    writer.newLine();
                }
            }
        }
    }

    @Override
    public void load() throws IOException {
        loadImportOrders();
        loadExportOrders();
        loadOrderItems();
    }

    private void loadImportOrders() throws IOException {
        importOrders.clear();

        File file = new File(importFilePath);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(importFilePath))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", -1);
                if (parts.length < 6) continue;

                String orderId = parts[0];
                String supplierId = parts[1];
                LocalDate orderDate = LocalDate.parse(parts[2]);
                double totalAmount = Double.parseDouble(parts[3]);
                OrderStatus status = OrderStatus.valueOf(parts[4]);
                String warehouseLocation = parts[5];

                Supplier supplier = supplierRepository != null ?
                        supplierRepository.findById(supplierId) : null;

                ImportOrder order = new ImportOrder(orderId, orderDate, totalAmount,
                        status, supplier, warehouseLocation);
                importOrders.add(order);
            }
        }
    }

    private void loadExportOrders() throws IOException {
        exportOrders.clear();

        File file = new File(exportFilePath);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(exportFilePath))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", -1);
                if (parts.length < 6) continue;

                String orderId = parts[0];
                String customerId = parts[1];
                LocalDate orderDate = LocalDate.parse(parts[2]);
                double totalAmount = Double.parseDouble(parts[3]);
                OrderStatus status = OrderStatus.valueOf(parts[4]);
                String deliveryAddress = parts[5];

                Customer customer = customerRepository != null ?
                        customerRepository.findById(customerId) : null;

                ExportOrder order = new ExportOrder(orderId, orderDate, totalAmount,
                        status, customer, deliveryAddress);
                exportOrders.add(order);
            }
        }
    }

    private void loadOrderItems() throws IOException {
        orderItems.clear();

        File file = new File(itemsFilePath);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(itemsFilePath))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", -1);
                if (parts.length < 4) continue;

                String orderId = parts[0];
                String productId = parts[1];
                int quantity = Integer.parseInt(parts[2]);
                double unitPrice = Double.parseDouble(parts[3]);

                Product product = productRepository != null ?
                        productRepository.findById(productId) : null;

                if (product != null) {
                    OrderItem item = new OrderItem(product, quantity, unitPrice);

                    orderItems.computeIfAbsent(orderId, k -> new ArrayList<>()).add(item);

                    // Gán items vào order tương ứng
                    ImportOrder importOrder = findImportOrderById(orderId);
                    if (importOrder != null) {
                        importOrder.setItems(orderItems.get(orderId));
                    }

                    ExportOrder exportOrder = findExportOrderById(orderId);
                    if (exportOrder != null) {
                        exportOrder.setItems(orderItems.get(orderId));
                    }
                }
            }
        }
    }

    @Override
    public String getFilePath() {
        return importFilePath + "," + exportFilePath + "," + itemsFilePath;
    }

    @Override
    public void clear() {
        importOrders.clear();
        exportOrders.clear();
        orderItems.clear();
    }

    // Import Order operations
    public void addImportOrder(ImportOrder order) {
        importOrders.add(order);
        if (!order.getItems().isEmpty()) {
            orderItems.put(order.getId(), order.getItems());
        }
    }

    public ImportOrder findImportOrderById(String id) {
        return importOrders.stream()
                .filter(o -> o.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<ImportOrder> findAllImportOrders() {
        return new ArrayList<>(importOrders);
    }

    public void deleteImportOrder(String id) throws OrderNotFoundException {
        ImportOrder order = findImportOrderById(id);
        if (order == null) {
            throw new OrderNotFoundException(id, "IMPORT");
        }
        importOrders.remove(order);
        orderItems.remove(id);
    }

    // Export Order operations
    public void addExportOrder(ExportOrder order) {
        exportOrders.add(order);
        if (!order.getItems().isEmpty()) {
            orderItems.put(order.getId(), order.getItems());
        }
    }

    public ExportOrder findExportOrderById(String id) {
        return exportOrders.stream()
                .filter(o -> o.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<ExportOrder> findAllExportOrders() {
        return new ArrayList<>(exportOrders);
    }

    public void deleteExportOrder(String id) throws OrderNotFoundException {
        ExportOrder order = findExportOrderById(id);
        if (order == null) {
            throw new OrderNotFoundException(id, "EXPORT");
        }
        exportOrders.remove(order);
        orderItems.remove(id);
    }

    // Statistics
    public List<ImportOrder> getImportOrdersByDateRange(LocalDate from, LocalDate to) {
        return importOrders.stream()
                .filter(o -> !o.getOrderDate().isBefore(from) && !o.getOrderDate().isAfter(to))
                .collect(Collectors.toList());
    }

    public List<ExportOrder> getExportOrdersByDateRange(LocalDate from, LocalDate to) {
        return exportOrders.stream()
                .filter(o -> !o.getOrderDate().isBefore(from) && !o.getOrderDate().isAfter(to))
                .collect(Collectors.toList());
    }

    public int countImportOrders() {
        return importOrders.size();
    }

    public int countExportOrders() {
        return exportOrders.size();
    }

    public double getTotalImportAmount() {
        return importOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .mapToDouble(ImportOrder::getTotalAmount)
                .sum();
    }

    public double getTotalExportAmount() {
        return exportOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .mapToDouble(ExportOrder::getTotalAmount)
                .sum();
    }
}
