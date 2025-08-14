package edu.icet.ecom.model.dto;
import edu.icet.ecom.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"password", "passwordResetToken"})
public class UserDTO {

    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role; // String representation of UserRole enum
    private String phone;
    private String address;
    private Boolean isActive;
    private LocalDateTime lastLogin;
    private String passwordResetToken;
    private LocalDateTime passwordResetExpires;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;

    // Custom constructor for basic user creation
    public UserDTO(String firstName, String lastName, String email, String password, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isActive = true;
    }

    // Constructor for user login
    public UserDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Constructor for user registration (without ID)
    public UserDTO(String firstName, String lastName, String email, String password, String role,
                   String phone, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phone = phone;
        this.address = address;
        this.isActive = true;
    }

    // Utility methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public boolean isEmployee() {
        return "EMPLOYEE".equals(role);
    }

    // Convert enum to string for role
    public void setRoleFromEnum(UserEntity.UserRole userRole) {
        this.role = userRole != null ? userRole.name() : null;
    }

    // Get enum from string role
    public UserEntity.UserRole getRoleAsEnum() {
        try {
            return role != null ? UserEntity.UserRole.valueOf(role) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // Display name for UI
    public String getDisplayName() {
        return getFullName() + " (" + email + ")";
    }

    // Check if user account is valid
    public boolean isAccountValid() {
        return isActive != null && isActive &&
                firstName != null && !firstName.trim().isEmpty() &&
                lastName != null && !lastName.trim().isEmpty() &&
                email != null && !email.trim().isEmpty() &&
                password != null && !password.trim().isEmpty() &&
                role != null && !role.trim().isEmpty();
    }
}
