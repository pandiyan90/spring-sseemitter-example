package com.rkalyankumar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ResponsebodyemittersampleApplication {


	@Bean
	public AsyncResponseController asyncResponseController() {
		return new AsyncResponseController();
	}

	public static void main(String[] args) {
		SpringApplication.run(ResponsebodyemittersampleApplication.class, args);
	}
}
