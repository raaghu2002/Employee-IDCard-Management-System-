package wizzybox.IDCard_Backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @Column(name = "employee_id", unique = true, nullable = false)
    @Min(value = 1, message = "Employee ID must be greater than 0")
    private int employeeId;

    @Column(name = "employee_name", nullable = false)
    @NotBlank(message = "Employee name is required")
    @Size(min = 2, max = 100, message = "Employee name must be between 2 and 100 characters")
    private String employeeName;

    @Column(name = "contact_number", nullable = false)
    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "\\d{10}", message = "Contact number must be 10 digits")
    // @Pattern(regexp = "\\d{10,15}", message = "Contact number must be between 10
    // and 15 digits")
    private String contactNumber;

    @Column(name = "alternate_contact_number")
    @Pattern(regexp = "\\d{10}", message = "Alternate contact number must be 10 digits")
    // @Pattern(regexp = "\\d{10,15}", message = "Contact number must be between 10
    // and 15 digits")
    private String alternateContactNumber;

    @Column(name = "personal_email", nullable = false)
    @NotBlank(message = "Personal email is required")
    @Email(message = "Invalid personal email format")
    private String personalEmail;

    @Column(name = "office_email", nullable = false)
    @NotBlank(message = "Office email is required")
    @Email(message = "Invalid office email format")
    private String officeEmail;

    @Column(name = "blood_group", nullable = false)
    @NotBlank(message = "Blood group is required")
    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood group format")
    private String bloodGroup;

    @Column(name = "gender", nullable = false)
    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "Male|Female|Other", message = "Gender must be Male, Female, or Other")
    private String gender;

    @Column(name = "father_name", nullable = false)
    @NotBlank(message = "Father's name is required")
    private String fatherName;

    @Column(name = "mother_name", nullable = false)
    @NotBlank(message = "Mother's name is required")
    private String motherName;

    @Column(name = "current_address", nullable = false)
    @NotBlank(message = "Current address is required")
    private String currentAddress;

    @Column(name = "permanent_address", nullable = false)
    @NotBlank(message = "Permanent address is required")
    private String permanentAddress;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "date_of_joining", nullable = false)
    @NotNull(message = "Date of joining is required")
    private LocalDate doj;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "date_of_birth", nullable = false)
    @NotNull(message = "Date of birth is required")
    private LocalDate dob;

    @Column(name = "marital_status", nullable = false)
    @NotBlank(message = "Marital status is required")
    @Pattern(regexp = "Single|Married", message = "Invalid marital status")
    private String maritalStatus;

    @Column(name = "date_of_marriage")
    private LocalDate dateOfMarriage;

    @Column(name = "citizenship", nullable = false)
    @NotBlank(message = "Citizenship is required")
    private String citizenship;

    @Column(name = "job_role", nullable = false)
    @NotBlank(message = "Job role is required")
    private String jobRole;

    @Column(name = "is_fresher", nullable = false)
    private boolean fresher;

    @Column(name = "previous_company", nullable = true)
    private String previousCompany;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "photo_data")
    @Lob
    private byte[] photodata;

    @Column(name = "qr_code_data")
    @Lob
    private byte[] qrCodedata;


}
