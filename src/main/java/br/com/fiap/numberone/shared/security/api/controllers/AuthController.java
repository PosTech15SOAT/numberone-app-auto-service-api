package br.com.fiap.numberone.shared.security.api.controllers;

import br.com.fiap.numberone.shared.security.application.services.AdminAuthenticationService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/public/auth")
public class AuthController {

	private final AdminAuthenticationService adminAuthenticationService;

	public AuthController(AdminAuthenticationService adminAuthenticationService) {
		this.adminAuthenticationService = adminAuthenticationService;
	}

	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody AuthRequest request) {
		AdminAuthenticationService.AuthResponse response = adminAuthenticationService.authenticate(
			new AdminAuthenticationService.AuthRequest(request.username(), request.password())
		);
		return new AuthResponse(response.accessToken(), response.tokenType(), response.expiresInSeconds());
	}

	public record AuthRequest(@NotBlank String username, @NotBlank String password) {
	}

	public record AuthResponse(String accessToken, String tokenType, long expiresInSeconds) {
	}
}
