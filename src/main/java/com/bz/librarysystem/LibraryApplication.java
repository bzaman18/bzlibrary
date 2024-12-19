package com.bz.librarysystem;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "com.bz.librarysystem")
@EntityScan("com.bz.librarysystem.entity")
@EnableJpaRepositories("com.bz.librarysystem.repository")
@EnableTransactionManagement
@EnableAspectJAutoProxy
@EnableRetry
@OpenAPIDefinition(
		info = @Info(
				title = "Library Management API",
				version = "1.0",
				description = "API for managing library resources"
		)
)
public class LibraryApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryApplication.class, args);
	}

}
