package wizzybox.IDCard_Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wizzybox.IDCard_Backend.model.OldEmployee;

public interface OldEmployeeRepository extends JpaRepository<OldEmployee, Integer> {
}
