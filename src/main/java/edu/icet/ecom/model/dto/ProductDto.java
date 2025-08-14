package edu.icet.ecom.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductDto {

    private Long productId;
    private String productName;
    private String description;
    private String size;
    private String color;
    private BigDecimal price;
    private BigDecimal costPrice;
    private Integer quantityOnHand;
    private Integer reorderLevel;
    private Integer maxStockLevel;
    private String productCode;
    private String barcode;
    private String imagePath;
    private Boolean isActive;
    private String status; // String representation of ProductStatus enum
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Category and Supplier information (for display)
    private Long categoryId;
    private String categoryName;
    private Long supplierId;
    private String supplierName;
    private String supplierCompany;

    // Custom constructor for basic product creation
    public ProductDto(String productName, String description, String size, String color,
                      BigDecimal price, Integer quantityOnHand, Long categoryId, Long supplierId) {
        this.productName = productName;
        this.description = description;
        this.size = size;
        this.color = color;
        this.price = price;
        this.quantityOnHand = quantityOnHand;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.isActive = true;
        this.reorderLevel = 10;
        this.maxStockLevel = 100;
    }

    // Constructor for product creation with all details
    public ProductDto(String productName, String description, String size, String color,
                      BigDecimal price, BigDecimal costPrice, Integer quantityOnHand,
                      Integer reorderLevel, Integer maxStockLevel, String barcode,
                      Long categoryId, Long supplierId) {
        this.productName = productName;
        this.description = description;
        this.size = size;
        this.color = color;
        this.price = price;
        this.costPrice = costPrice;
        this.quantityOnHand = quantityOnHand;
        this.reorderLevel = reorderLevel;
        this.maxStockLevel = maxStockLevel;
        this.barcode = barcode;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.isActive = true;
    }

    // Utility methods
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

    public String getFullDisplayName() {
        return getDisplayName() + " [" + (productCode != null ? productCode : "N/A") + "]";
    }

    public boolean isValid() {
        return productName != null && !productName.trim().isEmpty() &&
                price != null && price.compareTo(BigDecimal.ZERO) > 0 &&
                quantityOnHand != null && quantityOnHand >= 0 &&
                categoryId != null && supplierId != null;
    }

    // Stock management methods
    public boolean needsReorder() {
        return quantityOnHand != null && reorderLevel != null && quantityOnHand <= reorderLevel;
    }

    public boolean isOutOfStock() {
        return quantityOnHand == null || quantityOnHand <= 0;
    }

    public boolean isLowStock() {
        return quantityOnHand != null && reorderLevel != null &&
                quantityOnHand > 0 && quantityOnHand <= reorderLevel;
    }

    public String getStockStatus() {
        if (isOutOfStock()) return "Out of Stock";
        if (isLowStock()) return "Low Stock";
        return "In Stock";
    }

    public String getStockStatusColor() {
        if (isOutOfStock()) return "red";
        if (isLowStock()) return "orange";
        return "green";
    }

    // Price calculations
    public BigDecimal getProfitMargin() {
        if (price != null && costPrice != null && costPrice.compareTo(BigDecimal.ZERO) > 0) {
            return price.subtract(costPrice).divide(costPrice, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getProfitAmount() {
        if (price != null && costPrice != null) {
            return price.subtract(costPrice);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getStockValue() {
        if (price != null && quantityOnHand != null) {
            return price.multiply(BigDecimal.valueOf(quantityOnHand));
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getCostValue() {
        if (costPrice != null && quantityOnHand != null) {
            return costPrice.multiply(BigDecimal.valueOf(quantityOnHand));
        }
        return BigDecimal.ZERO;
    }

    // Format product name and description
    public void formatText() {
        if (productName != null) {
            this.productName = capitalizeWords(productName.trim());
        }
        if (size != null && !size.trim().isEmpty()) {
            this.size = size.trim().toUpperCase();
        }
        if (color != null && !color.trim().isEmpty()) {
            this.color = capitalizeWords(color.trim());
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

    // Category and supplier display information
    public String getCategorySupplierInfo() {
        StringBuilder info = new StringBuilder();
        if (categoryName != null) {
            info.append("Category: ").append(categoryName);
        }
        if (supplierName != null) {
            if (info.length() > 0) info.append(" | ");
            info.append("Supplier: ").append(supplierName);
            if (supplierCompany != null && !supplierCompany.equals(supplierName)) {
                info.append(" (").append(supplierCompany).append(")");
            }
        }
        return info.toString();
    }

    // Price formatting
    public String getFormattedPrice() {
        return price != null ? "Rs. " + price.setScale(2, BigDecimal.ROUND_HALF_UP) : "N/A";
    }

    public String getFormattedCostPrice() {
        return costPrice != null ? "Rs. " + costPrice.setScale(2, BigDecimal.ROUND_HALF_UP) : "N/A";
    }

    public String getFormattedStockValue() {
        return "Rs. " + getStockValue().setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    // Validation methods
    public boolean isPriceValid() {
        return price != null && price.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isCostPriceValid() {
        return costPrice == null || costPrice.compareTo(BigDecimal.ZERO) >= 0;
    }

    public boolean isQuantityValid() {
        return quantityOnHand != null && quantityOnHand >= 0;
    }

    public boolean isReorderLevelValid() {
        return reorderLevel == null || reorderLevel >= 0;
    }
}