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
public class OrderItemDto {

    private Long orderItemId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountAmount;
    private BigDecimal subtotal;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Order information
    private Long orderId;
    private String orderNumber;

    // Product information
    private Long productId;
    private String productName;
    private String productCode;
    private String size;
    private String color;
    private String categoryName;

    // Custom constructor for basic order item creation
    public OrderItemDto(Long productId, Integer quantity, BigDecimal unitPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discountAmount = BigDecimal.ZERO;
        calculateSubtotal();
    }

    // Constructor with discount
    public OrderItemDto(Long productId, Integer quantity, BigDecimal unitPrice, BigDecimal discountAmount) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discountAmount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        calculateSubtotal();
    }

    // Constructor with product details
    public OrderItemDto(Long productId, String productName, String productCode, String size, String color,
                        Integer quantity, BigDecimal unitPrice, BigDecimal discountAmount) {
        this.productId = productId;
        this.productName = productName;
        this.productCode = productCode;
        this.size = size;
        this.color = color;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discountAmount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        calculateSubtotal();
    }

    // Utility methods
    public boolean isValid() {
        return productId != null && quantity != null && quantity > 0 &&
                unitPrice != null && unitPrice.compareTo(BigDecimal.ZERO) >= 0;
    }

    public void calculateSubtotal() {
        if (quantity != null && unitPrice != null) {
            BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantity));
            if (discountAmount != null) {
                total = total.subtract(discountAmount);
            }
            this.subtotal = total.max(BigDecimal.ZERO); // Ensure subtotal is not negative
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }

    // Get total before discount
    public BigDecimal getTotalBeforeDiscount() {
        if (quantity != null && unitPrice != null) {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }

    // Discount calculations
    public BigDecimal getDiscountPercentage() {
        BigDecimal totalBeforeDiscount = getTotalBeforeDiscount();
        if (totalBeforeDiscount.compareTo(BigDecimal.ZERO) > 0 &&
                discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            return discountAmount.divide(totalBeforeDiscount, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }

    public void applyPercentageDiscount(BigDecimal discountPercentage) {
        if (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) >= 0 &&
                discountPercentage.compareTo(BigDecimal.valueOf(100)) <= 0) {
            BigDecimal totalBeforeDiscount = getTotalBeforeDiscount();
            this.discountAmount = totalBeforeDiscount.multiply(discountPercentage)
                    .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
            calculateSubtotal();
        }
    }

    public void applyFixedDiscount(BigDecimal discountAmount) {
        if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) >= 0) {
            BigDecimal totalBeforeDiscount = getTotalBeforeDiscount();
            // Ensure discount doesn't exceed total
            this.discountAmount = discountAmount.min(totalBeforeDiscount);
            calculateSubtotal();
        }
    }

    // Update methods
    public void updateQuantity(Integer newQuantity) {
        if (newQuantity != null && newQuantity > 0) {
            this.quantity = newQuantity;
            calculateSubtotal();
        }
    }

    public void updateUnitPrice(BigDecimal newUnitPrice) {
        if (newUnitPrice != null && newUnitPrice.compareTo(BigDecimal.ZERO) >= 0) {
            this.unitPrice = newUnitPrice;
            calculateSubtotal();
        }
    }

    // Display methods
    public String getProductDisplayName() {
        StringBuilder displayName = new StringBuilder();
        if (productName != null && !productName.isEmpty()) {
            displayName.append(productName);
        } else {
            displayName.append("Unknown Product");
        }

        if (size != null && !size.isEmpty()) {
            displayName.append(" - ").append(size);
        }
        if (color != null && !color.isEmpty()) {
            displayName.append(" (").append(color).append(")");
        }

        return displayName.toString();
    }

    public String getProductCodeDisplay() {
        return productCode != null ? productCode : "N/A";
    }

    public String getFullProductDisplay() {
        return getProductDisplayName() + " [" + getProductCodeDisplay() + "]";
    }

    // Formatting methods
    public String getFormattedUnitPrice() {
        return "Rs. " + (unitPrice != null ? unitPrice.setScale(2, BigDecimal.ROUND_HALF_UP) : "0.00");
    }

    public String getFormattedDiscountAmount() {
        return "Rs. " + (discountAmount != null ? discountAmount.setScale(2, BigDecimal.ROUND_HALF_UP) : "0.00");
    }

    public String getFormattedSubtotal() {
        return "Rs. " + (subtotal != null ? subtotal.setScale(2, BigDecimal.ROUND_HALF_UP) : "0.00");
    }

    public String getFormattedTotalBeforeDiscount() {
        return "Rs. " + getTotalBeforeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public String getFormattedDiscountPercentage() {
        BigDecimal percentage = getDiscountPercentage();
        return percentage.setScale(1, BigDecimal.ROUND_HALF_UP) + "%";
    }

    // Discount status
    public boolean hasDiscount() {
        return discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public String getDiscountDisplay() {
        if (!hasDiscount()) return "No Discount";
        return getFormattedDiscountAmount() + " (" + getFormattedDiscountPercentage() + ")";
    }

    // Validation methods
    public boolean isQuantityValid() {
        return quantity != null && quantity > 0;
    }

    public boolean isUnitPriceValid() {
        return unitPrice != null && unitPrice.compareTo(BigDecimal.ZERO) >= 0;
    }

    public boolean isDiscountValid() {
        if (discountAmount == null) return true;
        BigDecimal totalBeforeDiscount = getTotalBeforeDiscount();
        return discountAmount.compareTo(BigDecimal.ZERO) >= 0 &&
                discountAmount.compareTo(totalBeforeDiscount) <= 0;
    }

    // Summary information
    public String getItemSummary() {
        return String.format("%s x %d @ %s = %s",
                getProductDisplayName(),
                quantity,
                getFormattedUnitPrice(),
                getFormattedSubtotal());
    }

    public String getDetailedSummary() {
        StringBuilder summary = new StringBuilder(getItemSummary());
        if (hasDiscount()) {
            summary.append(" (Discount: ").append(getDiscountDisplay()).append(")");
        }
        if (notes != null && !notes.trim().isEmpty()) {
            summary.append(" - ").append(notes);
        }
        return summary.toString();
    }

    // Calculate line total for multiple items
    public static BigDecimal calculateLineTotals(java.util.List<OrderItemDto> items) {
        if (items == null || items.isEmpty()) return BigDecimal.ZERO;
        return items.stream()
                .map(OrderItemDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Calculate total quantity for multiple items
    public static int calculateTotalQuantity(java.util.List<OrderItemDto> items) {
        if (items == null || items.isEmpty()) return 0;
        return items.stream()
                .mapToInt(OrderItemDto::getQuantity)
                .sum();
    }
}