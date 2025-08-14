package edu.icet.ecom.model.entity;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"orderItems"})
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_number", unique = true, nullable = false, length = 50)
    private String orderNumber;

    @Column(name = "customer_name", length = 100)
    private String customerName;

    @Column(name = "customer_email", length = 100)
    private String customerEmail;

    @Column(name = "customer_phone", length = 15)
    private String customerPhone;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "amount_paid", precision = 10, scale = 2)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Column(name = "change_amount", precision = 10, scale = 2)
    private BigDecimal changeAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "receipt_sent")
    private Boolean receiptSent = false;

    @Column(name = "is_return")
    private Boolean isReturn = false;

    @Column(name = "original_order_id")
    private Long originalOrderId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Many-to-one relationship with employee
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeEntity employee;

    // One-to-many relationship with order items
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    // Enums
    public enum PaymentMethod {
        CASH, CREDIT_CARD, DEBIT_CARD, MOBILE_PAYMENT, BANK_TRANSFER
    }

    public enum OrderStatus {
        PENDING, COMPLETED, CANCELLED, REFUNDED, PARTIALLY_REFUNDED
    }

    // Custom constructor for basic order creation
    public OrderEntity(String customerName, String customerEmail, PaymentMethod paymentMethod, EmployeeEntity employee) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.paymentMethod = paymentMethod;
        this.employee = employee;
        this.orderStatus = OrderStatus.PENDING;
        this.isReturn = false;
        this.receiptSent = false;
    }

    // Generate order number automatically
    @PrePersist
    private void generateOrderNumber() {
        if (this.orderNumber == null || this.orderNumber.isEmpty()) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            this.orderNumber = "ORD-" + timestamp.substring(timestamp.length() - 8);
        }
    }

    // Add order item
    public void addOrderItem(OrderItemEntity orderItem) {
        if (orderItems == null) {
            orderItems = new ArrayList<>();
        }
        orderItems.add(orderItem);
        orderItem.setOrder(this);
        calculateTotals();
    }

    // Remove order item
    public void removeOrderItem(OrderItemEntity orderItem) {
        if (orderItems != null) {
            orderItems.remove(orderItem);
            orderItem.setOrder(null);
            calculateTotals();
        }
    }

    // Calculate totals
    public void calculateTotals() {
        if (orderItems != null && !orderItems.isEmpty()) {
            subtotal = orderItems.stream()
                    .map(OrderItemEntity::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            subtotal = BigDecimal.ZERO;
        }

        // Calculate tax (assuming 8% tax rate)
        taxAmount = subtotal.multiply(BigDecimal.valueOf(0.08)).setScale(2, BigDecimal.ROUND_HALF_UP);

        // Calculate total amount
        totalAmount = subtotal.add(taxAmount).subtract(discountAmount);

        // Ensure total is not negative
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            totalAmount = BigDecimal.ZERO;
        }
    }

    // Calculate change
    public void calculateChange() {
        if (amountPaid != null && totalAmount != null) {
            changeAmount = amountPaid.subtract(totalAmount);
            if (changeAmount.compareTo(BigDecimal.ZERO) < 0) {
                changeAmount = BigDecimal.ZERO;
            }
        }
    }

    // Check if order is fully paid
    public boolean isFullyPaid() {
        return amountPaid != null && totalAmount != null &&
                amountPaid.compareTo(totalAmount) >= 0;
    }

    // Get total items count
    public int getTotalItems() {
        if (orderItems != null) {
            return orderItems.stream()
                    .mapToInt(OrderItemEntity::getQuantity)
                    .sum();
        }
        return 0;
    }

    // Get unique products count
    public int getUniqueProductsCount() {
        return orderItems != null ? orderItems.size() : 0;
    }

    // Mark as completed
    public void markAsCompleted() {
        this.orderStatus = OrderStatus.COMPLETED;
    }

    // Mark as cancelled
    public void markAsCancelled() {
        this.orderStatus = OrderStatus.CANCELLED;
    }

    // Check if order can be returned
    public boolean canBeReturned() {
        return orderStatus == OrderStatus.COMPLETED && !isReturn &&
                createdAt.isAfter(LocalDateTime.now().minusDays(30)); // 30 days return policy
    }

    // Get formatted order number
    public String getFormattedOrderNumber() {
        return orderNumber != null ? orderNumber : "N/A";
    }
}