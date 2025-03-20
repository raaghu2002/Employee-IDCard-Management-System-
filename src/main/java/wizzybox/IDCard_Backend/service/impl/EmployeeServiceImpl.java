package wizzybox.IDCard_Backend.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import wizzybox.IDCard_Backend.exception.ResourceNotFoundException;
import wizzybox.IDCard_Backend.model.Employee;
import wizzybox.IDCard_Backend.model.OldEmployee;
import wizzybox.IDCard_Backend.repository.EmployeeRepository;
import wizzybox.IDCard_Backend.repository.OldEmployeeRepository;
import wizzybox.IDCard_Backend.service.EmployeeService;
import wizzybox.IDCard_Backend.util.QRCodeGenerator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class EmployeeServiceImpl implements EmployeeService {



    private final EmployeeRepository employeeRepository;
    private final OldEmployeeRepository oldEmployeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, OldEmployeeRepository oldEmployeeRepository) {
        this.employeeRepository = employeeRepository;
        this.oldEmployeeRepository = oldEmployeeRepository;

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
            String qrData = "https://sincere-learning-production.up.railway.app/info/" + savedEmployee.getEmployeeId();
            byte[] qrCodeBytes = QRCodeGenerator.generateQRCode(qrData);

            // Store QR code bytes in the employee entity
            savedEmployee.setQrCodedata(qrCodeBytes);

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



    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    @Override
    public Employee updateEmployee(int id, Employee updatedEmployee) {
        Employee employee = getEmployeeById(id);


        // Copy non-null properties from updatedEmployee to employee
        if (updatedEmployee.getEmployeeName() != null)
            employee.setEmployeeName(updatedEmployee.getEmployeeName());
        if (updatedEmployee.getContactNumber() != null)
            employee.setContactNumber(updatedEmployee.getContactNumber());
        if (updatedEmployee.getAlternateContactNumber() != null)
            employee.setAlternateContactNumber(updatedEmployee.getAlternateContactNumber());
        if (updatedEmployee.getPersonalEmail() != null)
            employee.setPersonalEmail(updatedEmployee.getPersonalEmail());
        if (updatedEmployee.getOfficeEmail() != null)
            employee.setOfficeEmail(updatedEmployee.getOfficeEmail());
        if (updatedEmployee.getBloodGroup() != null)
            employee.setBloodGroup(updatedEmployee.getBloodGroup());
        if (updatedEmployee.getGender() != null)
            employee.setGender(updatedEmployee.getGender());
        if (updatedEmployee.getFatherName() != null)
            employee.setFatherName(updatedEmployee.getFatherName());
        if (updatedEmployee.getMotherName() != null)
            employee.setMotherName(updatedEmployee.getMotherName());
        if (updatedEmployee.getCurrentAddress() != null)
            employee.setCurrentAddress(updatedEmployee.getCurrentAddress());
        if (updatedEmployee.getPermanentAddress() != null)
            employee.setPermanentAddress(updatedEmployee.getPermanentAddress());
        if (updatedEmployee.getDoj() != null)
            employee.setDoj(updatedEmployee.getDoj());
        if (updatedEmployee.getDob() != null)
            employee.setDob(updatedEmployee.getDob());
        if (updatedEmployee.getMaritalStatus() != null)
            employee.setMaritalStatus(updatedEmployee.getMaritalStatus());
        if (updatedEmployee.getDateOfMarriage() != null)
            employee.setDateOfMarriage(updatedEmployee.getDateOfMarriage());
        if (updatedEmployee.getCitizenship() != null)
            employee.setCitizenship(updatedEmployee.getCitizenship());
        if (updatedEmployee.getJobRole() != null)
            employee.setJobRole(updatedEmployee.getJobRole());
        employee.setFresher(updatedEmployee.isFresher());
        if (updatedEmployee.getPreviousCompany() != null)
            employee.setPreviousCompany(updatedEmployee.getPreviousCompany());

        // Save the updated employee
        return employeeRepository.save(employee);
    }

    @Override
    public void freezeEmployee(int id) {
        Employee employee = getEmployeeById(id);

        // Move employee to OldEmployees table before marking as frozen
        moveToOldEmployees(employee, "FROZEN");

        // Save the updated employee (if needed, update its status)
        employeeRepository.save(employee);

    }



    @Transactional
    public void deleteEmployee(int id) {
        Employee employee = getEmployeeById(id);
        if (employee == null) {
            throw new RuntimeException("Employee not found");
        }
        moveToOldEmployees(employee, "DELETED");
        employeeRepository.deleteById(id);
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

    @Override
    public void saveEmployeePhoto(int id, MultipartFile photo) throws IOException {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
        employee.setPhotodata(photo.getBytes());
        employee.setPhotoType(photo.getContentType());
        employeeRepository.save(employee);
    }


    @Override
    public byte[] getEmployeePhoto(int id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return employee.getPhotodata();
    }

}
