package wizzybox.IDCard_Backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import wizzybox.IDCard_Backend.model.Employee;
import wizzybox.IDCard_Backend.model.OldEmployee;
import wizzybox.IDCard_Backend.service.EmployeeService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@CrossOrigin(origins = "https://sincere-learning-production.up.railway.app")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/add-employee")
    public String addEmployee() {
        return "add-employee";
    }

    @GetMapping("/employee-info")
    public String employeeInfo() {
        return "employee-info";
    }

    @GetMapping("/employee-list")
    public String employeeList() {
        return "employee-list";
    }

    @GetMapping("/old-employee-list")
    public String showOldEmployeeList(Model model) {
        List<OldEmployee> oldEmployees = employeeService.getAllOldEmployees();
        model.addAttribute("oldEmployees", oldEmployees);
        return "old-employee-list";
    }

    @GetMapping("/info/{id}")
    public String showEmployeeInfo(@PathVariable String id, Model model) {
        try {
            int employeeId = Integer.parseInt(id);
            Employee employee = employeeService.getEmployeeById(employeeId);
            model.addAttribute("employee", employee);
            return "QRCode-Info";
        } catch (NumberFormatException e) {
            return "error";
        } catch (Exception e) {
            return "error";
        }
    }

    @PostMapping("/employees/{id}/photo")
    public ResponseEntity<?> uploadEmployeePhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
        }
        try {
            String fileName = id + "_" + file.getOriginalFilename();
            Path filePath = Paths.get("uploads", fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.ok("File uploaded successfully: " + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        }
    }


    @GetMapping("/api/employees")
    @ResponseBody
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/api/employees/{id}")
    @ResponseBody
    public Employee getEmployee(@PathVariable int id) {
        return employeeService.getEmployeeById(id);
    }

    @PostMapping("/api/employees")
    @ResponseBody
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeService.createEmployee(employee);
    }

    @PutMapping("/api/employees/{id}")
    @ResponseBody
    public Employee updateEmployee(@PathVariable int id, @RequestBody Employee employee) {
        employee.setEmployeeId(id);
        return employeeService.updateEmployee(id, employee);
    }

    @DeleteMapping("/api/employees/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteEmployee(@PathVariable int id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("Employee deleted successfully");
    }

    @PutMapping("/api/employees/{id}/freeze")
    @ResponseBody
    public ResponseEntity<String> freezeEmployee(@PathVariable int id) {
        employeeService.freezeEmployee(id);
        return ResponseEntity.ok("Employee frozen successfully");
    }

    @GetMapping("/api/old-employees")
    @ResponseBody
    public List<OldEmployee> getAllOldEmployees() {
        return employeeService.getAllOldEmployees();
    }

    // Updated API: Check if a field value already exists
    @GetMapping("/api/employees/check/{field}")
    @ResponseBody
    public ResponseEntity<Boolean> checkFieldExists(
            @PathVariable String field,
            @RequestParam String value) {

        boolean exists = false;

        switch (field) {
            case "contactNumber":
                exists = employeeService.existsByContactNumber(value);
                break;
            case "officeEmail":
                exists = employeeService.existsByOfficeEmail(value);
                break;
            case "alternateContactNumber":
                exists = employeeService.existsByContactNumber(value);
                break;
            case "personalEmail":
                exists = employeeService.existsByPersonalEmail(value);
                break;
            default:
                return ResponseEntity.badRequest().build(); // Invalid field
        }

        return ResponseEntity.ok(exists);
    }

}
