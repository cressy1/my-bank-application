package com.martins.martinsbank;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "My Bank",
				description = "Backend REST API for my_bank",
				version = "v1.0",
				contact = @Contact(
						name = "Cressy",
						email = "martgabril@gmail.com",
						url = "https://github.com/cressy1/my-bank-application"
				),
				license = @License(
						name = "Martins The CoderSniper",
						url = "https://github.com/cressy1/my-bank-application"
				)

		),
		externalDocs = @ExternalDocumentation(
				description = "Martins The CodeSniper my_bank app documentation",
				url = "https://github.com/cressy1/my-bank-application"
		)
)
public class MartinsBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(MartinsBankApplication.class, args);
	}

}
