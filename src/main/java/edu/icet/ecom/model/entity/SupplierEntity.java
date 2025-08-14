package edu.icet.ecom.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"products"})
@Entity
@Table(name = "suppliers")
public class SupplierEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private Long supplierId;

    @Column(name = "supplier_name", nullable = false, length = 100)
    private String supplierName;

    @Column(name = "company", nullable = false, length = 100)
    private String company;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Column(name = "tax_number", length = 50)
    private String taxNumber;

    @Column(name = "payment_terms", length = 200)
    private String paymentTerms;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // One-to-many relationship with products
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductEntity> products;

    // Custom constructor
    public SupplierEntity(String supplierName, String company, String email) {
        this.supplierName = supplierName;
        this.company = company;
        this.email = email;
        this.isActive = true;
    }

    // Custom constructor with all basic fields
    public SupplierEntity(String supplierName, String company, String email, String phone, String address) {
        this.supplierName = supplierName;
        this.company = company;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.isActive = true;
    }

    // Utility method to get supplier display name
    public String getDisplayName() {
        return supplierName + " (" + company + ")";
    }

    // Utility method to count supplied products
    public int getProductCount() {
        return products != null ? products.size() : 0;
    }
}