package br.com.fiap.numberone.shared.api.controllers;

import java.time.Instant;
import java.util.List;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/health")
public class HealthController {

	private final Environment environment;

	public HealthController(Environment environment) {
		this.environment = environment;
	}

	@GetMapping
	public HealthResponse health() {
		return new HealthResponse(
			"UP",
			environment.getProperty("spring.application.name", "numberone"),
			List.of(environment.getActiveProfiles()),
			Instant.now()
		);
	}

	public record HealthResponse(String status, String application, List<String> activeProfiles, Instant timestamp) {
	}
}
