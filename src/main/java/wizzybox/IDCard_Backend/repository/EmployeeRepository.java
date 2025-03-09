package wizzybox.IDCard_Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wizzybox.IDCard_Backend.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee , Integer> {
}
