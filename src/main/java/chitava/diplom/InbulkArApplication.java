package chitava.diplom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
@SpringBootApplication(scanBasePackages="chitava.diplom.*")
@ComponentScan(basePackages = "chitava.diplom.*")

public class InbulkArApplication {

	public static void main(String[] args) {
		SpringApplication.run(InbulkArApplication.class, args);
	}

}
