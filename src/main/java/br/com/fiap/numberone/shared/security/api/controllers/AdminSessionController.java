package br.com.fiap.numberone.shared.security.api.controllers;

import br.com.fiap.numberone.shared.security.infrastructure.http.JwtAuthenticationFilter.AuthenticatedAdmin;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/session")
public class AdminSessionController {

	@GetMapping
	public SessionResponse currentSession(Authentication authentication) {
		AuthenticatedAdmin principal = (AuthenticatedAdmin) authentication.getPrincipal();
		return new SessionResponse(principal.username(), principal.role(), true);
	}

	public record SessionResponse(String username, String role, boolean authenticated) {
	}
}
