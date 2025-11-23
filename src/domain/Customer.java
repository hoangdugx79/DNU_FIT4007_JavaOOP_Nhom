package domain;

import java.util.UUID;

public class Customer {
    private String id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private CustomerType type;

    // Constructor khi tạo mới
    public Customer(String name, String phone, String email, String address, CustomerType type) {
        this.id = "CUS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.type = type;
    }

    // Constructor khi load từ CSV
    public Customer(String id, String name, String phone, String email, String address, CustomerType type) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.type = type;
    }

    // Convert to CSV
    public String toCSV() {
        return String.format("%s,%s,%s,%s,%s,%s",
                id, name, phone, email, address, type);
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public CustomerType getType() { return type; }
    public void setType(CustomerType type) { this.type = type; }

    @Override
    public String toString() {
        return String.format("%-12s | %-25s | %-12s | %-30s | %s",
                id, name, phone, email, type);
    }
}
