package wizzybox.IDCard_Backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private int employeeId;

    @Column(name = "employee_name", nullable = false)
    private String employeeName;

    @Column(name = "contact_number", nullable = false)
    private String contactNumber;

    @Column(name = "alternate_contact_number")
    private String alternateContactNumber;

    @Column(name = "personal_email", nullable = false)
    private String personalEmail;

    @Column(name = "office_email", nullable = false)
    private String officeEmail;

    @Column(name = "blood_group", nullable = false)
    private String bloodGroup;

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "father_name", nullable = false)
    private String fatherName;

    @Column(name = "mother_name", nullable = false)
    private String motherName;

    @Column(name = "current_address", nullable = false)
    private String currentAddress;

    @Column(name = "permanent_address", nullable = false)
    private String permanentAddress;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "date_of_joining", nullable = false)
    private LocalDate doj;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dob;

    @Column(name = "marital_status", nullable = false)
    private String maritalStatus;

    @Column(name = "date_of_marriage")
    private LocalDate dateOfMarriage;

    @Column(name = "citizenship", nullable = false)
    private String citizenship;

    @Column(name = "job_role", nullable = false)
    private String jobRole;

    @Column(name = "is_fresher", nullable = false)
    private boolean fresher;

    @Column(name = "previous_company", nullable = false)
    private String previousCompany;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "photo_path")
    private String photoPath;

    @Column(name = "qr_code_path")
    private String qrCodePath;
}