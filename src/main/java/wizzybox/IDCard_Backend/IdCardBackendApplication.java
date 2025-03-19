package wizzybox.IDCard_Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class IdCardBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdCardBackendApplication.class, args);
	}

}
