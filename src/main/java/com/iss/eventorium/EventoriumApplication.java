package com.iss.eventorium;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EventoriumApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventoriumApplication.class, args);
	}

}
