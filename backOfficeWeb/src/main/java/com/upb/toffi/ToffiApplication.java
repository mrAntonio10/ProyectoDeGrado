package com.upb.toffi;

import com.upb.toffi.config.ApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@Import(ApplicationConfig.class)
@EnableJpaRepositories(basePackages = "com.upb.repositories")
@EntityScan(basePackages = "com.upb.models")
@ComponentScan(basePackages = {"com.upb.toffi", "com.upb.cores", "com.upb.repositories"})
public class ToffiApplication {
	public static void main(String[] args) {
		SpringApplication.run(ToffiApplication.class, args);
	}

}