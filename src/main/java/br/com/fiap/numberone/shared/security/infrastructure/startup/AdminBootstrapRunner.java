package br.com.fiap.numberone.shared.security.infrastructure.startup;

import br.com.fiap.numberone.shared.security.application.services.AdminAuthenticationService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrapRunner implements ApplicationRunner {

	private final AdminAuthenticationService adminAuthenticationService;

	public AdminBootstrapRunner(AdminAuthenticationService adminAuthenticationService) {
		this.adminAuthenticationService = adminAuthenticationService;
	}

	@Override
	public void run(ApplicationArguments args) {
		adminAuthenticationService.bootstrapAdminIfNeeded();
	}
}
