package wizzybox.IDCard_Backend.model;

import jakarta.persistence.*;
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

    @Column(name = "date_of_joining", nullable = false)
    private LocalDate doj;

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

    @Column(name = "freezed_by" , nullable = false)
    private String freezedBy;

    @Column(name = "is_fresher", nullable = false)
    private boolean isFresher;

    @Column(name = "previous_company", nullable = false)
    private String previousCompany;

    @Column(name = "photo_path")
    private String photoPath;

    @Column(name = "qr_code_path")
    private String qrCodePath;

    @Column(name = "action_type")
    private String actionType; // "FROZEN" or "DELETED"

    @Column(name = "action_by")
    private String actionBy; // Name of the person who performed the action

    @Column(name = "action_date")
    private LocalDateTime actionDate; // When the action was performed

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
