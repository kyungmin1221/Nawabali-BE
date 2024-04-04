package com.nawabali.nawabali;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@OpenAPIDefinition(servers = {@Server(url = "https://hhborad.shop", description = "백엔드 서버")})
@SpringBootApplication
@EnableJpaAuditing
public class NawabaliApplication {

	public static void main(String[] args) {
		SpringApplication.run(NawabaliApplication.class, args);
	}

}
