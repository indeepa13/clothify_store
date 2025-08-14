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
public class CategoryDto {

    private Long categoryId;
    private String categoryName;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer productCount; // For display purposes

    // Custom constructor for basic category creation
    public CategoryDto(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
        this.isActive = true;
    }

    // Constructor without ID (for creation)
    public CategoryDto(String categoryName, String description, Boolean isActive) {
        this.categoryName = categoryName;
        this.description = description;
        this.isActive = isActive != null ? isActive : true;
    }

    // Utility methods
    public String getDisplayName() {
        return categoryName + (description != null && !description.isEmpty() ?
                " - " + description : "");
    }

    public boolean isValid() {
        return categoryName != null && !categoryName.trim().isEmpty();
    }

    public String getStatus() {
        return isActive != null && isActive ? "Active" : "Inactive";
    }

    // Format category name (capitalize first letter of each word)
    public void formatCategoryName() {
        if (categoryName != null && !categoryName.trim().isEmpty()) {
            String[] words = categoryName.trim().toLowerCase().split("\\s+");
            StringBuilder formatted = new StringBuilder();
            for (String word : words) {
                if (word.length() > 0) {
                    formatted.append(Character.toUpperCase(word.charAt(0)))
                            .append(word.substring(1))
                            .append(" ");
                }
            }
            this.categoryName = formatted.toString().trim();
        }
    }

    // Get product count display text
    public String getProductCountText() {
        if (productCount == null) return "N/A";
        return productCount == 1 ? productCount + " product" : productCount + " products";
    }
}
