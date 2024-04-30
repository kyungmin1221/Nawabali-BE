package com.nawabali.nawabali;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;

@OpenAPIDefinition(servers = {
		@Server(url = "https://hhboard.shop", description = "백엔드 서버"),
		@Server(url = "http://localhost:8080", description = "로컬 서버")})
@SpringBootApplication
@EnableJpaAuditing
public class NawabaliApplication {

	public static void main(String[] args) {
		SpringApplication.run(NawabaliApplication.class, args);
	}

}
