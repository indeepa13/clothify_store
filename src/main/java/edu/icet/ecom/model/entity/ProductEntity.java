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
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"orderItems"})
@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "size", length = 10)
    private String size;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "cost_price", precision = 10, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "quantity_on_hand", nullable = false)
    private Integer quantityOnHand = 0;

    @Column(name = "reorder_level")
    private Integer reorderLevel = 10;

    @Column(name = "max_stock_level")
    private Integer maxStockLevel = 100;

    @Column(name = "product_code", unique = true, length = 50)
    private String productCode;

    @Column(name = "barcode", length = 100)
    private String barcode;

    @Column(name = "image_path", length = 500)
    private String imagePath;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProductStatus status = ProductStatus.AVAILABLE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Many-to-one relationship with category
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    // Many-to-one relationship with supplier
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id", nullable = false)
    private SupplierEntity supplier;

    // One-to-many relationship with order items
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItemEntity> orderItems;

    // Enum for product status
    public enum ProductStatus {
        AVAILABLE, OUT_OF_STOCK, DISCONTINUED, LOW_STOCK
    }

    // Custom constructor for basic product creation
    public ProductEntity(String productName, String description, String size, String color,
                         BigDecimal price, Integer quantityOnHand, CategoryEntity category,
                         SupplierEntity supplier) {
        this.productName = productName;
        this.description = description;
        this.size = size;
        this.color = color;
        this.price = price;
        this.quantityOnHand = quantityOnHand;
        this.category = category;
        this.supplier = supplier;
        this.isActive = true;
        this.status = ProductStatus.AVAILABLE;
    }

    // Generate product code automatically
    @PrePersist
    private void generateProductCode() {
        if (this.productCode == null || this.productCode.isEmpty()) {
            String categoryCode = category != null ?
                    category.getCategoryName().substring(0, Math.min(3, category.getCategoryName().length())).toUpperCase() :
                    "PRD";
            this.productCode = categoryCode + "-" + System.currentTimeMillis();
        }
    }

    // Check if product needs reordering
    public boolean needsReorder() {
        return quantityOnHand != null && reorderLevel != null && quantityOnHand <= reorderLevel;
    }

    // Check if product is out of stock
    public boolean isOutOfStock() {
        return quantityOnHand == null || quantityOnHand <= 0;
    }

    // Check if product is low stock
    public boolean isLowStock() {
        return quantityOnHand != null && reorderLevel != null &&
                quantityOnHand > 0 && quantityOnHand <= reorderLevel;
    }

    // Calculate profit margin
    public BigDecimal getProfitMargin() {
        if (price != null && costPrice != null && costPrice.compareTo(BigDecimal.ZERO) > 0) {
            return price.subtract(costPrice).divide(costPrice, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }

    // Get stock value
    public BigDecimal getStockValue() {
        if (price != null && quantityOnHand != null) {
            return price.multiply(BigDecimal.valueOf(quantityOnHand));
        }
        return BigDecimal.ZERO;
    }

    // Update stock quantity
    public void updateStock(Integer quantity) {
        if (quantity != null) {
            this.quantityOnHand = quantity;
            updateStatus();
        }
    }

    // Add stock
    public void addStock(Integer quantity) {
        if (quantity != null && quantity > 0) {
            this.quantityOnHand = (this.quantityOnHand != null ? this.quantityOnHand : 0) + quantity;
            updateStatus();
        }
    }

    // Reduce stock
    public boolean reduceStock(Integer quantity) {
        if (quantity != null && quantity > 0 && this.quantityOnHand != null && this.quantityOnHand >= quantity) {
            this.quantityOnHand -= quantity;
            updateStatus();
            return true;
        }
        return false;
    }

    // Update status based on quantity
    private void updateStatus() {
        if (isOutOfStock()) {
            this.status = ProductStatus.OUT_OF_STOCK;
        } else if (isLowStock()) {
            this.status = ProductStatus.LOW_STOCK;
        } else {
            this.status = ProductStatus.AVAILABLE;
        }
    }

    // Get display name with size and color
    public String getDisplayName() {
        StringBuilder displayName = new StringBuilder(productName);
        if (size != null && !size.isEmpty()) {
            displayName.append(" - ").append(size);
        }
        if (color != null && !color.isEmpty()) {
            displayName.append(" (").append(color).append(")");
        }
        return displayName.toString();
    }
}