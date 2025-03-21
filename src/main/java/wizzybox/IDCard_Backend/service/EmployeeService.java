package wizzybox.IDCard_Backend.service;

import org.springframework.web.multipart.MultipartFile;
import wizzybox.IDCard_Backend.model.Employee;
import wizzybox.IDCard_Backend.model.OldEmployee;
import java.io.IOException;
import java.util.List;

public interface EmployeeService {
    OldEmployee getOldEmployeeById(int id);

    List<Employee> getAllEmployees();

    Employee getEmployeeById(int id);

    Employee createEmployee(Employee employee);

    Employee updateEmployee(int id, Employee employee);

    void deleteEmployee(int id);

    void freezeEmployee(int id);

    List<OldEmployee> getAllOldEmployees();

    boolean existsByContactNumber(String contactNumber);

    boolean existsByOfficeEmail(String officeEmail);
    boolean existsByPersonalEmail(String personalEmail);

    void saveEmployeePhoto(int id, MultipartFile photo) throws IOException;
    byte[] getEmployeePhoto(int id);

}
