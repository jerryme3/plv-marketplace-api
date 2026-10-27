package com.marketplace.plv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ComMarketplacePlvApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComMarketplacePlvApplication.class, args);
	}

	@GetMapping
	public String helloWorld() {
		return "HELLO WORLDDDDD";
	}
}
