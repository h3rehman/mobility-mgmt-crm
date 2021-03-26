package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import config.AppConfig;
import config.SecurityConfig;

@SpringBootApplication
//@EnableTransactionManagement
@Import({AppConfig.class, SecurityConfig.class})
@EnableJpaRepositories("repository")
@EntityScan("repository")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
