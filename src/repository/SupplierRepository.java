package repository;

import domain.Supplier;
import interfaces.Persistable;
import interfaces.Searchable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SupplierRepository implements Persistable, Searchable<Supplier> {
    private List<Supplier> suppliers;
    private final String filePath;

    public SupplierRepository(String filePath) {
        this.filePath = filePath;
        this.suppliers = new ArrayList<>();
    }

    @Override
    public void save() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Header
            writer.write("id,name,phone,email,address,productCategories");
            writer.newLine();

            // Data
            for (Supplier supplier : suppliers) {
                writer.write(supplier.toCSV());
                writer.newLine();
            }
        }
    }

    @Override
    public void load() throws IOException {
        suppliers.clear();

        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", -1);
                if (parts.length < 6) continue;

                String id = parts[0];
                String name = parts[1];
                String phone = parts[2];
                String email = parts[3];
                String address = parts[4];
                String productCategories = parts[5];

                Supplier supplier = new Supplier(id, name, phone, email, address, productCategories);
                suppliers.add(supplier);
            }
        }
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public void clear() {
        suppliers.clear();
    }

    @Override
    public Supplier findById(String id) {
        return suppliers.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Supplier> findByName(String name) {
        String lowerName = name.toLowerCase();
        return suppliers.stream()
                .filter(s -> s.getName().toLowerCase().contains(lowerName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Supplier> findAll() {
        return new ArrayList<>(suppliers);
    }

    @Override
    public List<Supplier> search(String criteria) {
        String lower = criteria.toLowerCase();
        return suppliers.stream()
                .filter(s -> s.getName().toLowerCase().contains(lower) ||
                        s.getPhone().contains(lower) ||
                        s.getProductCategories().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    // CRUD operations
    public void add(Supplier supplier) {
        suppliers.add(supplier);
    }

    public void update(Supplier supplier) {
        Supplier existing = findById(supplier.getId());
        if (existing != null) {
            suppliers.remove(existing);
            suppliers.add(supplier);
        }
    }

    public void delete(String id) {
        Supplier supplier = findById(id);
        if (supplier != null) {
            suppliers.remove(supplier);
        }
    }

    public int count() {
        return suppliers.size();
    }
}
