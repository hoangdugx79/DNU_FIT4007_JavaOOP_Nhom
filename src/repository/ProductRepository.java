package repository;

import domain.*;
import exception.ProductNotFoundException;
import interfaces.Persistable;
import interfaces.Searchable;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductRepository implements Persistable, Searchable<Product> {
    private List<Product> products;
    private final String filePath;

    public ProductRepository(String filePath) {
        this.filePath = filePath;
        this.products = new ArrayList<>();
    }

    @Override
    public void save() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Header
            writer.write("id,productType,name,category,importPrice,salePrice,stockQuantity,extra1,extra2");
            writer.newLine();

            // Data
            for (Product product : products) {
                writer.write(product.toCSV());
                writer.newLine();
            }
        }
    }

    @Override
    public void load() throws IOException {
        products.clear();

        File file = new File(filePath);
        if (!file.exists()) {
            return; // File chưa tồn tại, return empty list
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", -1); // -1 để giữ empty strings
                if (parts.length < 9) continue;

                String id = parts[0];
                String productType = parts[1];
                String name = parts[2];
                String category = parts[3];
                double importPrice = Double.parseDouble(parts[4]);
                double salePrice = Double.parseDouble(parts[5]);
                int stockQuantity = Integer.parseInt(parts[6]);

                Product product = null;

                switch (productType) {
                    case "ELECTRONICS":
                        int warrantyMonths = Integer.parseInt(parts[7]);
                        product = new Electronics(id, name, category, importPrice,
                                salePrice, stockQuantity, warrantyMonths);
                        break;

                    case "CLOTHING":
                        String size = parts[7];
                        String material = parts[8];
                        product = new Clothing(id, name, category, importPrice,
                                salePrice, stockQuantity, size, material);
                        break;

                    case "FOOD":
                        LocalDate expiryDate = LocalDate.parse(parts[7]);
                        product = new Food(id, name, category, importPrice,
                                salePrice, stockQuantity, expiryDate);
                        break;

                    case "FURNITURE":
                        String dimensions = parts[7];
                        double weight = Double.parseDouble(parts[8]);
                        product = new Furniture(id, name, category, importPrice,
                                salePrice, stockQuantity, dimensions, weight);
                        break;
                }

                if (product != null) {
                    products.add(product);
                }
            }
        }
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public void clear() {
        products.clear();
    }

    @Override
    public Product findById(String id) {
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Product> findByName(String name) {
        String lowerName = name.toLowerCase();
        return products.stream()
                .filter(p -> p.getName().toLowerCase().contains(lowerName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products);
    }

    @Override
    public List<Product> search(String criteria) {
        String lower = criteria.toLowerCase();
        return products.stream()
                .filter(p -> p.getName().toLowerCase().contains(lower) ||
                        p.getCategory().toLowerCase().contains(lower) ||
                        p.getId().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    // CRUD operations
    public void add(Product product) {
        products.add(product);
    }

    public void update(Product product) throws ProductNotFoundException {
        Product existing = findById(product.getId());
        if (existing == null) {
            throw new ProductNotFoundException(product.getId(), "ID");
        }
        products.remove(existing);
        products.add(product);
    }

    public void delete(String id) throws ProductNotFoundException {
        Product product = findById(id);
        if (product == null) {
            throw new ProductNotFoundException(id, "ID");
        }
        products.remove(product);
    }

    public int count() {
        return products.size();
    }

    // Lấy sản phẩm theo loại
    public List<Product> findByType(String type) {
        return products.stream()
                .filter(p -> p.getProductType().equals(type))
                .collect(Collectors.toList());
    }

    // Lấy sản phẩm sắp hết hàng (stock < threshold)
    public List<Product> getLowStockProducts(int threshold) {
        return products.stream()
                .filter(p -> p.getStockQuantity() < threshold)
                .collect(Collectors.toList());
    }
}
