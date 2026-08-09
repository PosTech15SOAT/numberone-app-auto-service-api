package br.com.fiap.numberone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class NumberoneApplication {

	public static void main(String[] args) {
		SpringApplication.run(NumberoneApplication.class, args);
	}

}

