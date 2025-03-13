package wizzybox.IDCard_Backend.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import wizzybox.IDCard_Backend.exception.ResourceNotFoundException;
import wizzybox.IDCard_Backend.model.Employee;
import wizzybox.IDCard_Backend.model.OldEmployee;
import wizzybox.IDCard_Backend.repository.EmployeeRepository;
import wizzybox.IDCard_Backend.repository.OldEmployeeRepository;
import wizzybox.IDCard_Backend.service.EmployeeService;
import wizzybox.IDCard_Backend.util.QRCodeGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final String IMAGES_DIRECTORY = "src/main/resources/static/images/";
    private static final String QR_CODES_DIRECTORY = "src/main/resources/static/qrcodes/";

    private final EmployeeRepository employeeRepository;
    private final OldEmployeeRepository oldEmployeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, OldEmployeeRepository oldEmployeeRepository) {
        this.employeeRepository = employeeRepository;
        this.oldEmployeeRepository = oldEmployeeRepository;
        createDirectories();
    }

    private void createDirectories() {
        try {
            Files.createDirectories(Paths.get(IMAGES_DIRECTORY));
            Files.createDirectories(Paths.get(QR_CODES_DIRECTORY));
        } catch (IOException e) {
            throw new RuntimeException("Could not create directories", e);
        }
    }

    @Override
    public Employee createEmployee(Employee employee) {
        try {
            // Generate a unique ID if not set
            if (employee.getEmployeeId() == 0) {
                int uniqueId;
                Random random = new Random();
                do {
                    uniqueId = random.nextInt(900000) + 100000;
                } while (employeeRepository.existsById(uniqueId));
                employee.setEmployeeId(uniqueId);
            }

            // Save the employee to get the ID
            Employee savedEmployee = employeeRepository.save(employee);

            // Generate QR Code
            String qrData = "http://localhost:9191/info/" + savedEmployee.getEmployeeId();
            String qrFileName = savedEmployee.getEmployeeName() + ".png";
            String qrFilePath = QR_CODES_DIRECTORY + qrFileName;

            QRCodeGenerator.generateQRCode(qrData, qrFilePath);
            savedEmployee.setQrCodePath("/qrcodes/" + qrFileName);

            return employeeRepository.save(savedEmployee);
        } catch (Exception e) {
            throw new RuntimeException("Error generating QR code", e);
        }
    }

    // @Override
    // public void saveEmployeePhoto(int id, MultipartFile photo) throws IOException
    // {
    // Employee employee = getEmployeeById(id);
    // String fileName = employee.getEmployeeName() +
    // getFileExtension(photo.getOriginalFilename());
    // Path filePath = Paths.get(IMAGES_DIRECTORY + fileName);

    // // Delete existing photo if it exists
    // if (employee.getPhotoPath() != null &&
    // !employee.getPhotoPath().equals("N/A")) {
    // try {
    // Files.deleteIfExists(Paths.get(IMAGES_DIRECTORY
    // + employee.getPhotoPath().substring(employee.getPhotoPath().lastIndexOf("/")
    // + 1)));
    // } catch (IOException e) {
    // // Log but continue if old file couldn't be deleted
    // System.err.println("Could not delete old photo: " + e.getMessage());
    // }
    // }

    // // Save the new file
    // Files.copy(photo.getInputStream(), filePath,
    // StandardCopyOption.REPLACE_EXISTING);

    // // Update employee with photo path
    // employee.setPhotoPath("/images/" + fileName);
    // employeeRepository.save(employee);
    // }

    @Override
    public void saveEmployeePhoto(int id, MultipartFile photo) {
        try {
            Employee employee = getEmployeeById(id);
            String extension = getFileExtension(photo.getOriginalFilename());

            // Save photo with employee name
            String photoName = employee.getEmployeeName() + extension;
            Path photoPath = Paths.get(IMAGES_DIRECTORY + photoName);
            Files.copy(photo.getInputStream(), photoPath, StandardCopyOption.REPLACE_EXISTING);

            // Update photo path in database
            employee.setPhotoPath("/images/" + photoName);
            employeeRepository.save(employee);

        } catch (IOException e) {
            throw new RuntimeException("Could not save photo file", e);
        }
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    @Override
    public Employee updateEmployee(int id, Employee updatedEmployee) {
        Employee employee = getEmployeeById(id);

        // If name has changed, update QR code file name
        if (!employee.getEmployeeName().equals(updatedEmployee.getEmployeeName())) {
            try {
                // Update QR code file
                if (employee.getQrCodePath() != null && !employee.getQrCodePath().equals("N/A")) {
                    String oldQrName = employee.getQrCodePath()
                            .substring(employee.getQrCodePath().lastIndexOf("/") + 1);
                    String newQrName = updatedEmployee.getEmployeeName() + ".png";
                    Path oldQrPath = Paths.get(QR_CODES_DIRECTORY + oldQrName);
                    Path newQrPath = Paths.get(QR_CODES_DIRECTORY + newQrName);
                    if (Files.exists(oldQrPath)) {
                        Files.move(oldQrPath, newQrPath, StandardCopyOption.REPLACE_EXISTING);
                        updatedEmployee.setQrCodePath("/qrcodes/" + newQrName);
                    }
                }

                // Update photo file
                if (employee.getPhotoPath() != null && !employee.getPhotoPath().equals("N/A")) {
                    String oldPhotoName = employee.getPhotoPath()
                            .substring(employee.getPhotoPath().lastIndexOf("/") + 1);
                    String newPhotoName = updatedEmployee.getEmployeeName()
                            + oldPhotoName.substring(oldPhotoName.lastIndexOf("."));
                    Path oldPhotoPath = Paths.get(IMAGES_DIRECTORY + oldPhotoName);
                    Path newPhotoPath = Paths.get(IMAGES_DIRECTORY + newPhotoName);
                    if (Files.exists(oldPhotoPath)) {
                        Files.move(oldPhotoPath, newPhotoPath, StandardCopyOption.REPLACE_EXISTING);
                        updatedEmployee.setPhotoPath("/images/" + newPhotoName);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error updating file names", e);
            }
        } else {
            // Preserve the paths if name hasn't changed
            updatedEmployee.setPhotoPath(employee.getPhotoPath());
            updatedEmployee.setQrCodePath(employee.getQrCodePath());
        }

        // Copy non-null properties from updatedEmployee to employee
        if (updatedEmployee.getEmployeeName() != null) employee.setEmployeeName(updatedEmployee.getEmployeeName());
        if (updatedEmployee.getContactNumber() != null) employee.setContactNumber(updatedEmployee.getContactNumber());
        if (updatedEmployee.getAlternateContactNumber() != null) employee.setAlternateContactNumber(updatedEmployee.getAlternateContactNumber());
        if (updatedEmployee.getPersonalEmail() != null) employee.setPersonalEmail(updatedEmployee.getPersonalEmail());
        if (updatedEmployee.getOfficeEmail() != null) employee.setOfficeEmail(updatedEmployee.getOfficeEmail());
        if (updatedEmployee.getBloodGroup() != null) employee.setBloodGroup(updatedEmployee.getBloodGroup());
        if (updatedEmployee.getGender() != null) employee.setGender(updatedEmployee.getGender());
        if (updatedEmployee.getFatherName() != null) employee.setFatherName(updatedEmployee.getFatherName());
        if (updatedEmployee.getMotherName() != null) employee.setMotherName(updatedEmployee.getMotherName());
        if (updatedEmployee.getCurrentAddress() != null) employee.setCurrentAddress(updatedEmployee.getCurrentAddress());
        if (updatedEmployee.getPermanentAddress() != null) employee.setPermanentAddress(updatedEmployee.getPermanentAddress());
        if (updatedEmployee.getDoj() != null) employee.setDoj(updatedEmployee.getDoj());
        if (updatedEmployee.getDob() != null) employee.setDob(updatedEmployee.getDob());
        if (updatedEmployee.getMaritalStatus() != null) employee.setMaritalStatus(updatedEmployee.getMaritalStatus());
        if (updatedEmployee.getDateOfMarriage() != null) employee.setDateOfMarriage(updatedEmployee.getDateOfMarriage());
        if (updatedEmployee.getCitizenship() != null) employee.setCitizenship(updatedEmployee.getCitizenship());
        if (updatedEmployee.getJobRole() != null) employee.setJobRole(updatedEmployee.getJobRole());
        employee.setFresher(updatedEmployee.isFresher()); 
        if (updatedEmployee.getPreviousCompany() != null) employee.setPreviousCompany(updatedEmployee.getPreviousCompany());

        // Save the updated employee
        return employeeRepository.save(employee);
    }

    @Override
    public void freezeEmployee(int id) {
        Employee oldEmployee = getEmployeeById(id);

        try {
            // Handle photo file - rename with Freezed_ prefix for old employee
            if (oldEmployee.getPhotoPath() != null && !oldEmployee.getPhotoPath().equals("N/A")) {
                String oldPhotoName = oldEmployee.getPhotoPath()
                        .substring(oldEmployee.getPhotoPath().lastIndexOf("/") + 1);
                String extension = oldPhotoName.substring(oldPhotoName.lastIndexOf("."));
                String newPhotoName = "Freezed_" + oldEmployee.getEmployeeName() + extension;
                Path oldPhotoPath = Paths.get(IMAGES_DIRECTORY + oldPhotoName);
                Path newPhotoPath = Paths.get(IMAGES_DIRECTORY + newPhotoName);
                if (Files.exists(oldPhotoPath)) {
                    // Copy the file for old employee
                    Files.copy(oldPhotoPath, newPhotoPath, StandardCopyOption.REPLACE_EXISTING);
                }

                // Update old employee's photo path for oldemployees table
                Employee oldEmployeeCopy = new Employee();
                BeanUtils.copyProperties(oldEmployee, oldEmployeeCopy);
                oldEmployeeCopy.setPhotoPath("/images/" + newPhotoName);

                // Move to OldEmployees table with renamed files
                moveToOldEmployees(oldEmployeeCopy, "FROZEN");
            }

            // Delete old employee's photo since it will be replaced by new employee
            if (oldEmployee.getPhotoPath() != null && !oldEmployee.getPhotoPath().equals("N/A")) {
                String oldPhotoName = oldEmployee.getPhotoPath()
                        .substring(oldEmployee.getPhotoPath().lastIndexOf("/") + 1);
                Files.deleteIfExists(Paths.get(IMAGES_DIRECTORY + oldPhotoName));
            }

            // Reset photo path for current employee so new file can be uploaded
            oldEmployee.setPhotoPath(null);
            employeeRepository.save(oldEmployee);

        } catch (IOException e) {
            throw new RuntimeException("Error handling files during freeze operation", e);
        }
    }

//    @Override
//    public void deleteEmployee(int id) {
//        Employee employee = getEmployeeById(id);
//        moveToOldEmployees(employee, "DELETED");
//
//        employeeRepository.deleteById(id);
//    }

    @Override
    public void deleteEmployee(int id) {
        Employee employee = getEmployeeById(id);

        try {
            // Handle photo file - rename with Deleted_ prefix for old employee
            if (employee.getPhotoPath() != null && !employee.getPhotoPath().equals("N/A")) {
                String oldPhotoName = employee.getPhotoPath()
                        .substring(employee.getPhotoPath().lastIndexOf("/") + 1);
                String extension = oldPhotoName.substring(oldPhotoName.lastIndexOf("."));
                String newPhotoName = "Deleted_" + employee.getEmployeeName() + extension;
                Path oldPhotoPath = Paths.get(IMAGES_DIRECTORY + oldPhotoName);
                Path newPhotoPath = Paths.get(IMAGES_DIRECTORY + newPhotoName);

                if (Files.exists(oldPhotoPath)) {
                    // Rename (move) the file to reflect deletion
                    Files.move(oldPhotoPath, newPhotoPath, StandardCopyOption.REPLACE_EXISTING);
                }

                // Update employee's photo path before moving to OldEmployees table
                employee.setPhotoPath("/images/" + newPhotoName);
            }

            // Handle QR Code deletion
            if (employee.getQrCodePath() != null && !employee.getQrCodePath().equals("N/A")) {
                String qrCodeName = employee.getQrCodePath()
                        .substring(employee.getQrCodePath().lastIndexOf("/") + 1);
                Path qrCodePath = Paths.get(QR_CODES_DIRECTORY + qrCodeName);

                // Delete the QR code file if it exists
                Files.deleteIfExists(qrCodePath);

                // Reset QR code path before moving to OldEmployees table
                employee.setQrCodePath(null);
            }

            // Move employee to OldEmployees table before deletion
            moveToOldEmployees(employee, "DELETED");

            // Delete employee from the main table
            employeeRepository.deleteById(id);

        } catch (IOException e) {
            throw new RuntimeException("Error handling files during delete operation", e);
        }
    }


    private void moveToOldEmployees(Employee employee, String actionType) {
        try {
            OldEmployee oldEmployee = new OldEmployee();
            BeanUtils.copyProperties(employee, oldEmployee);

            // Set all required fields
            oldEmployee.setActionType(actionType);
            oldEmployee.setActionBy(actionType);
            oldEmployee.setFreezedBy(actionType);
            oldEmployee.setActionDate(LocalDateTime.now());

            // Handle dates properly
            if (employee.getDob() != null) {
                oldEmployee.setDob(employee.getDob());
            }
            if (employee.getDoj() != null) {
                oldEmployee.setDoj(employee.getDoj());
            }
            if (employee.getDateOfMarriage() != null) {
                oldEmployee.setDateOfMarriage(employee.getDateOfMarriage());
            }

            // Handle nullable fields with defaults
            oldEmployee
                    .setPreviousCompany(employee.getPreviousCompany() != null ? employee.getPreviousCompany() : "N/A");
            oldEmployee.setAlternateContactNumber(
                    employee.getAlternateContactNumber() != null ? employee.getAlternateContactNumber() : "N/A");
            oldEmployee.setPhotoPath(employee.getPhotoPath() != null ? employee.getPhotoPath() : "N/A");
            oldEmployee.setQrCodePath(employee.getQrCodePath() != null ? employee.getQrCodePath() : "N/A");

            oldEmployeeRepository.save(oldEmployee);
        } catch (Exception e) {
            throw new RuntimeException("Failed to move employee to OldEmployees table", e);
        }
    }

    @Override
    public Employee getEmployeeById(int id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
    }

    @Override
    public OldEmployee getOldEmployeeById(int id) {
        return oldEmployeeRepository.findById(id).orElse(null);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public List<OldEmployee> getAllOldEmployees() {
        return oldEmployeeRepository.findAll();
    }

    @Override
    public boolean existsByContactNumber(String contactNumber) {
        return employeeRepository.existsByContactNumber(contactNumber);
    }

    @Override
    public boolean existsByOfficeEmail(String officeEmail) {
        return employeeRepository.existsByOfficeEmail(officeEmail);
    }

    @Override
    public boolean existsByPersonalEmail(String personalEmail) {
        return employeeRepository.existsByPersonalEmail(personalEmail);
    }
}
