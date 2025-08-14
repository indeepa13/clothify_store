package edu.icet.ecom.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderDto {

    private Long orderId;
    private String orderNumber;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private BigDecimal amountPaid;
    private BigDecimal changeAmount;
    private String paymentMethod; // String representation of PaymentMethod enum
    private String orderStatus; // String representation of OrderStatus enum
    private String notes;
    private Boolean receiptSent;
    private Boolean isReturn;
    private Long originalOrderId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Employee information
    private Long employeeId;
    private String employeeName;
    private String employeeCode;

    // Order items
    private List<OrderItemDto> orderItems;

    // Display fields
    private Integer totalItems;
    private Integer uniqueProductsCount;

    // Custom constructor for basic order creation
    public OrderDto(String customerName, String customerEmail, String paymentMethod, Long employeeId) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.paymentMethod = paymentMethod;
        this.employeeId = employeeId;
        this.orderStatus = "PENDING";
        this.isReturn = false;
        this.receiptSent = false;
        this.subtotal = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
        this.amountPaid = BigDecimal.ZERO;
        this.changeAmount = BigDecimal.ZERO;
    }

    // Constructor for order processing
    public OrderDto(String customerName, String customerEmail, String customerPhone,
                    String paymentMethod, Long employeeId, String notes) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.paymentMethod = paymentMethod;
        this.employeeId = employeeId;
        this.notes = notes;
        this.orderStatus = "PENDING";
        this.isReturn = false;
        this.receiptSent = false;
        this.subtotal = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
        this.amountPaid = BigDecimal.ZERO;
        this.changeAmount = BigDecimal.ZERO;
    }

    // Utility methods
    public boolean isValid() {
        return paymentMethod != null && !paymentMethod.trim().isEmpty() &&
                employeeId != null && totalAmount != null &&
                totalAmount.compareTo(BigDecimal.ZERO) >= 0;
    }

    public String getDisplayOrderNumber() {
        return orderNumber != null ? orderNumber : "N/A";
    }

    public String getCustomerDisplayName() {
        StringBuilder display = new StringBuilder();
        if (customerName != null && !customerName.isEmpty()) {
            display.append(customerName);
        } else {
            display.append("Walk-in Customer");
        }

        if (customerEmail != null && !customerEmail.isEmpty()) {
            display.append(" (").append(customerEmail).append(")");
        }

        return display.toString();
    }

    public String getEmployeeDisplayName() {
        StringBuilder display = new StringBuilder();
        if (employeeName != null && !employeeName.isEmpty()) {
            display.append(employeeName);
        }
        if (employeeCode != null && !employeeCode.isEmpty()) {
            display.append(" (").append(employeeCode).append(")");
        }
        return display.toString();
    }

    // Status display methods
    public String getOrderStatusDisplay() {
        if (orderStatus == null) return "Unknown";
        return capitalizeWords(orderStatus.replace("_", " ").toLowerCase());
    }

    public String getPaymentMethodDisplay() {
        if (paymentMethod == null) return "Unknown";
        return capitalizeWords(paymentMethod.replace("_", " ").toLowerCase());
    }

    public String getOrderStatusColor() {
        if (orderStatus == null) return "gray";
        switch (orderStatus.toUpperCase()) {
            case "COMPLETED": return "green";
            case "PENDING": return "orange";
            case "CANCELLED": return "red";
            case "REFUNDED": case "PARTIALLY_REFUNDED": return "blue";
            default: return "gray";
        }
    }

    // Financial calculations
    public void calculateTotals() {
        if (orderItems != null && !orderItems.isEmpty()) {
            subtotal = orderItems.stream()
                    .map(OrderItemDto::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            subtotal = BigDecimal.ZERO;
        }

        // Calculate tax (assuming 8% tax rate)
        taxAmount = subtotal.multiply(BigDecimal.valueOf(0.08)).setScale(2, BigDecimal.ROUND_HALF_UP);

        // Calculate total amount
        totalAmount = subtotal.add(taxAmount);
        if (discountAmount != null) {
            totalAmount = totalAmount.subtract(discountAmount);
        }

        // Ensure total is not negative
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            totalAmount = BigDecimal.ZERO;
        }
    }

    public void calculateChange() {
        if (amountPaid != null && totalAmount != null) {
            changeAmount = amountPaid.subtract(totalAmount);
            if (changeAmount.compareTo(BigDecimal.ZERO) < 0) {
                changeAmount = BigDecimal.ZERO;
            }
        }
    }

    public boolean isFullyPaid() {
        return amountPaid != null && totalAmount != null &&
                amountPaid.compareTo(totalAmount) >= 0;
    }

    public BigDecimal getBalanceDue() {
        if (totalAmount != null && amountPaid != null) {
            BigDecimal balance = totalAmount.subtract(amountPaid);
            return balance.max(BigDecimal.ZERO);
        }
        return totalAmount != null ? totalAmount : BigDecimal.ZERO;
    }

    // Item calculations
    public int getCalculatedTotalItems() {
        if (orderItems != null) {
            return orderItems.stream()
                    .mapToInt(OrderItemDto::getQuantity)
                    .sum();
        }
        return 0;
    }

    public int getCalculatedUniqueProductsCount() {
        return orderItems != null ? orderItems.size() : 0;
    }

    // Formatting methods
    public String getFormattedSubtotal() {
        return "Rs. " + (subtotal != null ? subtotal.setScale(2, BigDecimal.ROUND_HALF_UP) : "0.00");
    }

    public String getFormattedTaxAmount() {
        return "Rs. " + (taxAmount != null ? taxAmount.setScale(2, BigDecimal.ROUND_HALF_UP) : "0.00");
    }

    public String getFormattedDiscountAmount() {
        return "Rs. " + (discountAmount != null ? discountAmount.setScale(2, BigDecimal.ROUND_HALF_UP) : "0.00");
    }

    public String getFormattedTotalAmount() {
        return "Rs. " + (totalAmount != null ? totalAmount.setScale(2, BigDecimal.ROUND_HALF_UP) : "0.00");
    }

    public String getFormattedAmountPaid() {
        return "Rs. " + (amountPaid != null ? amountPaid.setScale(2, BigDecimal.ROUND_HALF_UP) : "0.00");
    }

    public String getFormattedChangeAmount() {
        return "Rs. " + (changeAmount != null ? changeAmount.setScale(2, BigDecimal.ROUND_HALF_UP) : "0.00");
    }

    public String getFormattedBalanceDue() {
        return "Rs. " + getBalanceDue().setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    // Validation methods
    public boolean isCustomerEmailValid() {
        if (customerEmail == null || customerEmail.trim().isEmpty()) return true; // Optional
        return customerEmail.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }

    public boolean isCustomerPhoneValid() {
        if (customerPhone == null || customerPhone.trim().isEmpty()) return true; // Optional
        return customerPhone.matches("^[+]?[0-9\\s\\-\\(\\)]{7,20}$");
    }

    public boolean isAmountPaidValid() {
        return amountPaid == null || amountPaid.compareTo(BigDecimal.ZERO) >= 0;
    }

    public boolean isDiscountAmountValid() {
        return discountAmount == null ||
                (discountAmount.compareTo(BigDecimal.ZERO) >= 0 &&
                        (subtotal == null || discountAmount.compareTo(subtotal) <= 0));
    }

    // Return policy check
    public boolean canBeReturned() {
        return "COMPLETED".equals(orderStatus) && !isReturn &&
                createdAt != null && createdAt.isAfter(LocalDateTime.now().minusDays(30));
    }

    // Utility method for capitalizing words
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

    // Get order summary
    public String getOrderSummary() {
        return String.format("Order %s - %s - %s items - %s",
                getDisplayOrderNumber(),
                getFormattedTotalAmount(),
                getCalculatedTotalItems(),
                getOrderStatusDisplay());
    }
}