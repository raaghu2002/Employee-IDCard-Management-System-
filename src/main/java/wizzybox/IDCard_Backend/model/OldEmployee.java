package wizzybox.IDCard_Backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "oldemployees")
public class OldEmployee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @NotBlank(message = "Employee name is required")
    @Size(min = 2, max = 32, message = "Employee name must be between 2 and 32 characters")
    @Column(name = "employee_name", nullable = false)
    private String employeeName;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "\\d{10}", message = "Contact number must be 10 digits")
    @Column(name = "contact_number", nullable = false)
    private String contactNumber;

    @Pattern(regexp = "\\d{10}", message = "Alternate contact number must be 10 digits")
    @Column(name = "alternate_contact_number")
    private String alternateContactNumber;

    @NotBlank(message = "Personal email is required")
    @Email(message = "Invalid personal email format")
    @Column(name = "personal_email", nullable = false)
    private String personalEmail;

    @NotBlank(message = "Office email is required")
    @Email(message = "Invalid office email format")
    @Column(name = "office_email", nullable = false)
    private String officeEmail;

    @NotBlank(message = "Blood group is required")
    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood group format")
    @Column(name = "blood_group", nullable = false)
    private String bloodGroup;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "Male|Female|Other", message = "Gender must be Male, Female, or Other")
    @Column(name = "gender", nullable = false)
    private String gender;

    @NotBlank(message = "Father's name is required")
    @Column(name = "father_name", nullable = false)
    private String fatherName;

    @NotBlank(message = "Mother's name is required")
    @Column(name = "mother_name", nullable = false)
    private String motherName;

    @NotBlank(message = "Current address is required")
    @Column(name = "current_address", nullable = false)
    private String currentAddress;

    @NotBlank(message = "Permanent address is required")
    @Column(name = "permanent_address", nullable = false)
    private String permanentAddress;

    @NotNull(message = "Date of joining is required")
    @PastOrPresent(message = "Date of joining cannot be in the future")
    @Column(name = "date_of_joining", nullable = false)
    private LocalDate doj;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dob;

    @NotBlank(message = "Marital status is required")
    @Pattern(regexp = "Single|Married", message = "Invalid marital status")
    @Column(name = "marital_status", nullable = false)
    private String maritalStatus;

    @Past(message = "Date of marriage must be in the past")
    @Column(name = "date_of_marriage")
    private LocalDate dateOfMarriage;

    @NotBlank(message = "Citizenship is required")
    @Column(name = "citizenship", nullable = false)
    private String citizenship;

    @NotBlank(message = "Job role is required")
    @Column(name = "job_role", nullable = false)
    private String jobRole;

    @NotBlank(message = "Freezed by is required")
    @Column(name = "freezed_by", nullable = false)
    private String freezedBy;

    @Column(name = "is_fresher", nullable = false)
    private boolean isFresher;

    @Column(name = "previous_company")
    private String previousCompany;

    @Column(name = "photo_data")
    @Lob
    private byte[] photodata;

    @Column(name = "qr_code_data")
    @Lob
    private byte[] qrCodedata;

    @Pattern(regexp = "FROZEN|DELETED", message = "Action type must be FROZEN or DELETED")
    @Column(name = "action_type")
    private String actionType;

    @Column(name = "action_by")
    private String actionBy;

    @Column(name = "action_date")
    private LocalDateTime actionDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
