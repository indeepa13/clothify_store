package edu.icet.ecom.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SupplierDto {

    private Long supplierId;
    private String supplierName;
    private String company;
    private String email;
    private String phone;
    private String address;
    private String contactPerson;
    private String taxNumber;
    private String paymentTerms;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer productCount; // For display purposes

    // Custom constructor for basic supplier creation
    public SupplierDto(String supplierName, String company, String email) {
        this.supplierName = supplierName;
        this.company = company;
        this.email = email;
        this.isActive = true;
    }

    // Constructor with contact details
    public SupplierDto(String supplierName, String company, String email, String phone, String address) {
        this.supplierName = supplierName;
        this.company = company;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.isActive = true;
    }

    // Constructor without ID (for creation)
    public SupplierDto(String supplierName, String company, String email, String phone,
                       String address, String contactPerson, String taxNumber, String paymentTerms) {
        this.supplierName = supplierName;
        this.company = company;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.contactPerson = contactPerson;
        this.taxNumber = taxNumber;
        this.paymentTerms = paymentTerms;
        this.isActive = true;
    }

    // Utility methods
    public String getDisplayName() {
        return supplierName + " (" + company + ")";
    }

    public String getFullDisplayName() {
        StringBuilder display = new StringBuilder(getDisplayName());
        if (email != null && !email.isEmpty()) {
            display.append(" - ").append(email);
        }
        return display.toString();
    }

    public boolean isValid() {
        return supplierName != null && !supplierName.trim().isEmpty() &&
                company != null && !company.trim().isEmpty() &&
                email != null && !email.trim().isEmpty() && isValidEmail(email);
    }

    // Email validation
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }

    // Phone validation
    public boolean isValidPhone() {
        if (phone == null || phone.trim().isEmpty()) return true; // Optional field
        return phone.matches("^[+]?[0-9\\s\\-\\(\\)]{7,20}$");
    }

    public String getStatus() {
        return isActive != null && isActive ? "Active" : "Inactive";
    }

    // Format supplier name and company
    public void formatNames() {
        if (supplierName != null) {
            this.supplierName = capitalizeWords(supplierName.trim());
        }
        if (company != null) {
            this.company = capitalizeWords(company.trim());
        }
        if (contactPerson != null && !contactPerson.trim().isEmpty()) {
            this.contactPerson = capitalizeWords(contactPerson.trim());
        }
    }

    private String capitalizeWords(String text) {
        if (text == null || text.isEmpty()) return text;
        String[] words = text.toLowerCase().split("\\s+");
        StringBuilder capitalized = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                capitalized.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return capitalized.toString().trim();
    }

    // Get contact information
    public String getContactInfo() {
        StringBuilder contact = new StringBuilder();
        if (email != null && !email.isEmpty()) {
            contact.append("Email: ").append(email);
        }
        if (phone != null && !phone.isEmpty()) {
            if (contact.length() > 0) contact.append(" | ");
            contact.append("Phone: ").append(phone);
        }
        return contact.toString();
    }

    // Get product count display text
    public String getProductCountText() {
        if (productCount == null) return "N/A";
        return productCount == 1 ? productCount + " product" : productCount + " products";
    }
}