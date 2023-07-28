package id.co.gemastik.web.webtool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class WebtoolApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebtoolApplication.class, args);
	}

}
