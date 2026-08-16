package br.com.fiap.numberone.shared.security.api.controllers;

import br.com.fiap.numberone.shared.security.domain.valueobjects.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/session")
public class AdminSessionController {

	@GetMapping
	public SessionResponse currentSession(Authentication authentication) {
		AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
		return new SessionResponse(
			principal.subject(),
			principal.customerId(),
			principal.status(),
			principal.roles(),
			principal.permissions(),
			principal.correlationId(),
			true
		);
	}

	public record SessionResponse(
		String subject,
		UUID customerId,
		String status,
		Set<String> roles,
		Set<String> permissions,
		String correlationId,
		boolean authenticated
	) {
	}
}
