package app.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ServletComponentScan
@PropertySource("classpath:database.properties")
@SpringBootApplication(scanBasePackages = "app")
@EnableJpaRepositories("app.persistence.repo")
@EntityScan("app.persistence.model")
public class LaunchApplication {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LaunchApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(LaunchApplication.class, args);
	}

}
