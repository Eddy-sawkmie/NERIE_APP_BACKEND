package com.nic.nerie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class NerieApplication {

	public static void main(String[] args) {
		// Load .env file
		Dotenv dotenv = Dotenv.load();
		
		// Set system properties from .env variables
        // This makes them accessible via @Value or Environment
		dotenv.entries().forEach(entry -> 
			System.setProperty(entry.getKey(), entry.getValue())
		);
		
		SpringApplication.run(NerieApplication.class, args);
	}

}
