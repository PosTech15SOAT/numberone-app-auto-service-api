package br.com.fiap.numberone.shared.security.unit.infrastructure;

import br.com.fiap.numberone.shared.security.domain.valueobjects.AuthenticatedUser;
import br.com.fiap.numberone.shared.security.infrastructure.identity.AuthenticatedUserProperties;
import br.com.fiap.numberone.shared.security.infrastructure.identity.LocalAuthenticatedUserProvider;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalAuthenticatedUserProviderTest {

	@Test
	void shouldBuildConfiguredLocalUserAndReuseIncomingCorrelationId() {
		UUID customerId = UUID.randomUUID();
		AuthenticatedUserProperties properties = properties(customerId);
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("X-Correlation-Id", "correlation-123");

		AuthenticatedUser user = new LocalAuthenticatedUserProvider(request, properties)
			.currentUser()
			.orElseThrow();

		assertEquals("local-customer", user.subject());
		assertEquals(customerId, user.customerId());
		assertEquals("correlation-123", user.correlationId());
		assertTrue(user.hasRole("CUSTOMER"));
		assertTrue(user.hasPermission("SERVICE_ORDER_READ"));
	}

	@Test
	void shouldGenerateCorrelationIdWhenHeaderIsAbsent() {
		AuthenticatedUser user = new LocalAuthenticatedUserProvider(
			new MockHttpServletRequest(),
			properties(null)
		).currentUser().orElseThrow();

		assertNotNull(UUID.fromString(user.correlationId()));
	}

	private AuthenticatedUserProperties properties(UUID customerId) {
		AuthenticatedUserProperties properties = new AuthenticatedUserProperties();
		properties.getLocal().setSubject("local-customer");
		properties.getLocal().setCustomerId(customerId);
		properties.getLocal().setStatus("ACTIVE");
		properties.getLocal().setRoles(Set.of("CUSTOMER"));
		properties.getLocal().setPermissions(Set.of("SERVICE_ORDER_READ"));
		return properties;
	}
}
