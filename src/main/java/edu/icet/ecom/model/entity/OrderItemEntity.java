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

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"order"})
@Entity
@Table(name = "order_items")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "notes", length = 500)
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Many-to-one relationship with order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    // Many-to-one relationship with product
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    // Custom constructor for basic order item creation
    public OrderItemEntity(ProductEntity product, Integer quantity, BigDecimal unitPrice) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discountAmount = BigDecimal.ZERO;
        calculateSubtotal();
    }

    // Custom constructor with discount
    public OrderItemEntity(ProductEntity product, Integer quantity, BigDecimal unitPrice, BigDecimal discountAmount) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discountAmount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        calculateSubtotal();
    }

    // Calculate subtotal automatically
    @PrePersist
    @PreUpdate
    private void calculateSubtotal() {
        if (quantity != null && unitPrice != null) {
            BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantity));
            if (discountAmount != null) {
                total = total.subtract(discountAmount);
            }
            this.subtotal = total.max(BigDecimal.ZERO); // Ensure subtotal is not negative
        }
    }

    // Manual subtotal calculation
    public void updateSubtotal() {
        calculateSubtotal();
    }

    // Get total without discount
    public BigDecimal getTotalBeforeDiscount() {
        if (quantity != null && unitPrice != null) {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }

    // Get discount percentage
    public BigDecimal getDiscountPercentage() {
        BigDecimal totalBeforeDiscount = getTotalBeforeDiscount();
        if (totalBeforeDiscount.compareTo(BigDecimal.ZERO) > 0 && discountAmount != null) {
            return discountAmount.divide(totalBeforeDiscount, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }

    // Apply percentage discount
    public void applyPercentageDiscount(BigDecimal discountPercentage) {
        if (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) >= 0 &&
                discountPercentage.compareTo(BigDecimal.valueOf(100)) <= 100) {
            BigDecimal totalBeforeDiscount = getTotalBeforeDiscount();
            this.discountAmount = totalBeforeDiscount.multiply(discountPercentage)
                    .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
            calculateSubtotal();
        }
    }

    // Apply fixed discount
    public void applyFixedDiscount(BigDecimal discountAmount) {
        if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) >= 0) {
            BigDecimal totalBeforeDiscount = getTotalBeforeDiscount();
            // Ensure discount doesn't exceed total
            this.discountAmount = discountAmount.min(totalBeforeDiscount);
            calculateSubtotal();
        }
    }

    // Update quantity and recalculate
    public void updateQuantity(Integer newQuantity) {
        if (newQuantity != null && newQuantity > 0) {
            this.quantity = newQuantity;
            calculateSubtotal();
        }
    }

    // Update unit price and recalculate
    public void updateUnitPrice(BigDecimal newUnitPrice) {
        if (newUnitPrice != null && newUnitPrice.compareTo(BigDecimal.ZERO) >= 0) {
            this.unitPrice = newUnitPrice;
            calculateSubtotal();
        }
    }

    // Check if item has discount
    public boolean hasDiscount() {
        return discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    // Get product name for display
    public String getProductDisplayName() {
        return product != null ? product.getDisplayName() : "Unknown Product";
    }

    // Get product code for display
    public String getProductCode() {
        return product != null ? product.getProductCode() : "N/A";
    }
}