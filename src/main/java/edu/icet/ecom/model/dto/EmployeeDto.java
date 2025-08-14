package edu.icet.ecom.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmployeeDto {

    private Long employeeId;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private LocalDate dateOfBirth;
    private LocalDate hireDate;
    private BigDecimal salary;
    private String position; // String representation of Position enum
    private String department; // String representation of Department enum
    private String employmentStatus; // String representation of EmploymentStatus enum
    private Long managerId;
    private String emergencyContact;
    private String emergencyPhone;
    private String nationalId;
    private String bankAccount;
    private String notes;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Display fields
    private String managerName;
    private Integer totalOrders;
    private BigDecimal totalSales;
    private Integer yearsOfService;

    // Custom constructor for basic employee creation
    public EmployeeDto(String firstName, String lastName, String email, LocalDate hireDate, String position) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hireDate = hireDate;
        this.position = position;
        this.isActive = true;
        this.employmentStatus = "ACTIVE";
    }

    // Constructor with all basic details
    public EmployeeDto(String firstName, String lastName, String email, String phone, String address,
                       LocalDate dateOfBirth, LocalDate hireDate, BigDecimal salary, String position,
                       String department) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.hireDate = hireDate;
        this.salary = salary;
        this.position = position;
        this.department = department;
        this.isActive = true;
        this.employmentStatus = "ACTIVE";
    }

    // Constructor with emergency details
    public EmployeeDto(String firstName, String lastName, String email, String phone, String address,
                       LocalDate dateOfBirth, LocalDate hireDate, BigDecimal salary, String position,
                       String department, String emergencyContact, String emergencyPhone, String nationalId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.hireDate = hireDate;
        this.salary = salary;
        this.position = position;
        this.department = department;
        this.emergencyContact = emergencyContact;
        this.emergencyPhone = emergencyPhone;
        this.nationalId = nationalId;
        this.isActive = true;
        this.employmentStatus = "ACTIVE";
    }

    // Utility methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getDisplayName() {
        return getFullName() + " (" + (employeeCode != null ? employeeCode : "N/A") + ")";
    }

    public String getFullDisplayName() {
        StringBuilder display = new StringBuilder(getDisplayName());
        if (position != null) {
            display.append(" - ").append(position);
        }
        if (department != null) {
            display.append(" (").append(department).append(")");
        }
        return display.toString();
    }

    public boolean isValid() {
        return firstName != null && !firstName.trim().isEmpty() &&
                lastName != null && !lastName.trim().isEmpty() &&
                email != null && !email.trim().isEmpty() && isValidEmail(email) &&
                hireDate != null && position != null && !position.trim().isEmpty();
    }

    // Email validation
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }

    // Phone validation
    public boolean isValidPhone() {
        if (phone == null || phone.trim().isEmpty()) return true; // Optional field
        return phone.matches("^[+]?[0-9\\s\\-\\(\\)]{7,20}$");
    }

    // Emergency phone validation
    public boolean isValidEmergencyPhone() {
        if (emergencyPhone == null || emergencyPhone.trim().isEmpty()) return true; // Optional field
        return emergencyPhone.matches("^[+]?[0-9\\s\\-\\(\\)]{7,20}$");
    }

    public boolean isManager() {
        return "STORE_MANAGER".equals(position) || "ASSISTANT_MANAGER".equals(position);
    }

    public int getCalculatedYearsOfService() {
        if (hireDate != null) {
            return LocalDate.now().getYear() - hireDate.getYear();
        }
        return 0;
    }

    public String getStatus() {
        return isActive != null && isActive ? "Active" : "Inactive";
    }

    public String getEmploymentStatusDisplay() {
        if (employmentStatus == null) return "Unknown";
        return capitalizeWords(employmentStatus.replace("_", " ").toLowerCase());
    }

    public String getPositionDisplay() {
        if (position == null) return "Unknown";
        return capitalizeWords(position.replace("_", " ").toLowerCase());
    }

    public String getDepartmentDisplay() {
        if (department == null) return "Unknown";
        return capitalizeWords(department.replace("_", " ").toLowerCase());
    }

    // Format names
    public void formatNames() {
        if (firstName != null) {
            this.firstName = capitalizeWords(firstName.trim());
        }
        if (lastName != null) {
            this.lastName = capitalizeWords(lastName.trim());
        }
        if (emergencyContact != null && !emergencyContact.trim().isEmpty()) {
            this.emergencyContact = capitalizeWords(emergencyContact.trim());
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

    // Age calculation
    public Integer getAge() {
        if (dateOfBirth != null) {
            return LocalDate.now().getYear() - dateOfBirth.getYear();
        }
        return null;
    }

    // Salary formatting
    public String getFormattedSalary() {
        return salary != null ? "Rs. " + salary.setScale(2, BigDecimal.ROUND_HALF_UP) : "N/A";
    }

    // Contact information
    public String getContactInfo() {
        StringBuilder contact = new StringBuilder();
        if (email != null && !email.isEmpty()) {
            contact.append("Email: ").append(email);
        }
        if (phone != null && !phone.isEmpty()) {
            if (contact.length() > 0) contact.append(" | ");
            contact.append("Phone: ").append(phone);
        }
        return contact.toString();
    }

    // Emergency contact information
    public String getEmergencyContactInfo() {
        StringBuilder emergency = new StringBuilder();
        if (emergencyContact != null && !emergencyContact.isEmpty()) {
            emergency.append(emergencyContact);
        }
        if (emergencyPhone != null && !emergencyPhone.isEmpty()) {
            if (emergency.length() > 0) emergency.append(" - ");
            emergency.append(emergencyPhone);
        }
        return emergency.toString();
    }

    // Performance metrics display
    public String getPerformanceMetrics() {
        StringBuilder metrics = new StringBuilder();
        if (totalOrders != null) {
            metrics.append("Orders: ").append(totalOrders);
        }
        if (totalSales != null) {
            if (metrics.length() > 0) metrics.append(" | ");
            metrics.append("Sales: Rs. ").append(totalSales.setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        return metrics.toString();
    }

    // Validation methods
    public boolean isHireDateValid() {
        return hireDate != null && !hireDate.isAfter(LocalDate.now());
    }

    public boolean isDateOfBirthValid() {
        if (dateOfBirth == null) return true; // Optional field
        return !dateOfBirth.isAfter(LocalDate.now().minusYears(16)) &&
                !dateOfBirth.isBefore(LocalDate.now().minusYears(80));
    }

    public boolean isSalaryValid() {
        return salary == null || salary.compareTo(BigDecimal.ZERO) >= 0;
    }

    public boolean isNationalIdValid() {
        if (nationalId == null || nationalId.trim().isEmpty()) return true; // Optional field
        // Basic validation - can be customized based on country format
        return nationalId.matches("^[A-Za-z0-9]{5,20}$");
    }

    // Get years of service display
    public String getYearsOfServiceDisplay() {
        int years = yearsOfService != null ? yearsOfService : getCalculatedYearsOfService();
        return years == 1 ? years + " year" : years + " years";
    }
}