package wizzybox.IDCard_Backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import wizzybox.IDCard_Backend.model.Employee;
import wizzybox.IDCard_Backend.model.OldEmployee;
import wizzybox.IDCard_Backend.repository.EmployeeRepository;
import wizzybox.IDCard_Backend.service.EmployeeService;

import java.io.IOException;
import java.util.List;

@Controller
@CrossOrigin(origins = "https://sincere-learning-production.up.railway.app")
public class EmployeeController {


    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeService employeeService , EmployeeRepository employeeRepository) {
        this.employeeService = employeeService;
        this.employeeRepository = employeeRepository;
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

    @PostMapping(value = "/api/employees/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadPhoto(@PathVariable int id, @RequestParam("photo") MultipartFile photo)
            throws IOException {

        System.out.println("Received file: " + photo.getOriginalFilename() + ", Size: " + photo.getSize());

        if (photo.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded");
        }

        employeeService.saveEmployeePhoto(id, photo);
        return ResponseEntity.ok("Photo uploaded successfully");
    }

    @GetMapping("/api/employees/{id}/photo")
    public ResponseEntity<byte[]> getEmployeePhoto(@PathVariable int id) {
        byte[] photoData = employeeService.getEmployeePhoto(id);
        if (photoData == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // Adjust as needed
        return new ResponseEntity<>(photoData, headers, HttpStatus.OK);
    }

    @GetMapping("/{id}/qrcode")
    public ResponseEntity<byte[]> getQRCode(@PathVariable int id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        byte[] qrCode = employee.getQrCodedata();
        if (qrCode == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(qrCode, headers, HttpStatus.OK);
    }


}
