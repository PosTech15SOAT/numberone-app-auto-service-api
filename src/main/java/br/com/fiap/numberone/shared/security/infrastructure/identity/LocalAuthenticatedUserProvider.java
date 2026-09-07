package br.com.fiap.numberone.shared.security.infrastructure.identity;

import br.com.fiap.numberone.shared.infrastructure.correlation.CorrelationIdResolver;
import br.com.fiap.numberone.shared.security.application.gateways.AuthenticatedUserProvider;
import br.com.fiap.numberone.shared.security.domain.valueobjects.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Optional;

@Component
@RequestScope
@ConditionalOnProperty(prefix = "app.security.identity", name = "provider", havingValue = "local")
public class LocalAuthenticatedUserProvider implements AuthenticatedUserProvider {

	private final AuthenticatedUser authenticatedUser;

	public LocalAuthenticatedUserProvider(HttpServletRequest request, AuthenticatedUserProperties properties) {
		AuthenticatedUserProperties.Local local = properties.getLocal();
		String correlationId = CorrelationIdResolver.resolve(request, properties.getHeaders().getCorrelationId());

		this.authenticatedUser = new AuthenticatedUser(
			local.getSubject(),
			local.getCustomerId(),
			local.getStatus(),
			local.getRoles(),
			local.getPermissions(),
			correlationId
		);
	}

	@Override
	public Optional<AuthenticatedUser> currentUser() {
		return Optional.of(authenticatedUser);
	}
}
