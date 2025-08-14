package edu.icet.ecom.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"orders"})
@Entity
@Table(name = "employees")
public class EmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "employee_code", unique = true, length = 20)
    private String employeeCode;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column(name = "salary", precision = 10, scale = 2)
    private BigDecimal salary;

    @Enumerated(EnumType.STRING)
    @Column(name = "position")
    private Position position;

    @Enumerated(EnumType.STRING)
    @Column(name = "department")
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status")
    private EmploymentStatus employmentStatus = EmploymentStatus.ACTIVE;

    @Column(name = "manager_id")
    private Long managerId;

    @Column(name = "emergency_contact", length = 100)
    private String emergencyContact;

    @Column(name = "emergency_phone", length = 15)
    private String emergencyPhone;

    @Column(name = "national_id", unique = true, length = 20)
    private String nationalId;

    @Column(name = "bank_account", length = 50)
    private String bankAccount;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // One-to-many relationship with orders
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderEntity> orders;

    // Enums
    public enum Position {
        CASHIER, SALES_ASSOCIATE, STORE_MANAGER, ASSISTANT_MANAGER, INVENTORY_MANAGER, SECURITY_GUARD
    }

    public enum Department {
        SALES, INVENTORY, MANAGEMENT, SECURITY, CUSTOMER_SERVICE
    }

    public enum EmploymentStatus {
        ACTIVE, INACTIVE, TERMINATED, ON_LEAVE, PROBATION
    }

    // Custom constructor for basic employee creation
    public EmployeeEntity(String firstName, String lastName, String email, LocalDate hireDate, Position position) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hireDate = hireDate;
        this.position = position;
        this.isActive = true;
        this.employmentStatus = EmploymentStatus.ACTIVE;
    }

    // Generate employee code automatically
    @PrePersist
    private void generateEmployeeCode() {
        if (this.employeeCode == null || this.employeeCode.isEmpty()) {
            String positionCode = position != null ?
                    position.name().substring(0, Math.min(3, position.name().length())) :
                    "EMP";
            this.employeeCode = "E" + positionCode + "-" + String.format("%04d", System.currentTimeMillis() % 10000);
        }
    }

    // Utility methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getDisplayName() {
        return getFullName() + " (" + employeeCode + ")";
    }

    public boolean isManager() {
        return position == Position.STORE_MANAGER || position == Position.ASSISTANT_MANAGER;
    }

    public int getYearsOfService() {
        if (hireDate != null) {
            return LocalDate.now().getYear() - hireDate.getYear();
        }
        return 0;
    }

    public int getTotalOrders() {
        return orders != null ? orders.size() : 0;
    }

    // Calculate total sales amount for this employee
    public BigDecimal getTotalSales() {
        if (orders != null && !orders.isEmpty()) {
            return orders.stream()
                    .map(OrderEntity::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return BigDecimal.ZERO;
    }
}