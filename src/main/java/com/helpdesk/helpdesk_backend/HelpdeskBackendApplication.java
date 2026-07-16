package com.helpdesk.helpdesk_backend;

import java.util.TimeZone;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HelpdeskBackendApplication {

	// forzar hora local en peru , America.

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
	}

  

	public static void main(String[] args) {
		SpringApplication.run(HelpdeskBackendApplication.class, args);
	}

}
