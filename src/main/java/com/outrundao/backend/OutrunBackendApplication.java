package com.outrundao.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:env.properties")
public class OutrunBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(OutrunBackendApplication.class, args);
	}

}
