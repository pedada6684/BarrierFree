package com.fullship.hBAF;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class HBaFApplication {

	public static void main(String[] args) {
		SpringApplication.run(HBaFApplication.class, args);
	}

}
