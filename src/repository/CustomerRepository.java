package repository;

import domain.Customer;
import domain.CustomerType;
import interfaces.Persistable;
import interfaces.Searchable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerRepository implements Persistable, Searchable<Customer> {
    private List<Customer> customers;
    private final String filePath;

    public CustomerRepository(String filePath) {
        this.filePath = filePath;
        this.customers = new ArrayList<>();
    }

    @Override
    public void save() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Header
            writer.write("id,name,phone,email,address,type");
            writer.newLine();

            // Data
            for (Customer customer : customers) {
                writer.write(customer.toCSV());
                writer.newLine();
            }
        }
    }

    @Override
    public void load() throws IOException {
        customers.clear();

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
                CustomerType type = CustomerType.valueOf(parts[5]);

                Customer customer = new Customer(id, name, phone, email, address, type);
                customers.add(customer);
            }
        }
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public void clear() {
        customers.clear();
    }

    @Override
    public Customer findById(String id) {
        return customers.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Customer> findByName(String name) {
        String lowerName = name.toLowerCase();
        return customers.stream()
                .filter(c -> c.getName().toLowerCase().contains(lowerName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(customers);
    }

    @Override
    public List<Customer> search(String criteria) {
        String lower = criteria.toLowerCase();
        return customers.stream()
                .filter(c -> c.getName().toLowerCase().contains(lower) ||
                        c.getPhone().contains(lower) ||
                        c.getEmail().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    // CRUD operations
    public void add(Customer customer) {
        customers.add(customer);
    }

    public void update(Customer customer) {
        Customer existing = findById(customer.getId());
        if (existing != null) {
            customers.remove(existing);
            customers.add(customer);
        }
    }

    public void delete(String id) {
        Customer customer = findById(id);
        if (customer != null) {
            customers.remove(customer);
        }
    }

    public int count() {
        return customers.size();
    }

    // Tìm theo loại khách hàng
    public List<Customer> findByType(CustomerType type) {
        return customers.stream()
                .filter(c -> c.getType() == type)
                .collect(Collectors.toList());
    }
}
