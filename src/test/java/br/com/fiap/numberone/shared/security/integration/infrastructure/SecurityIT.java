package br.com.fiap.numberone.shared.security.integration.infrastructure;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
	webEnvironment = WebEnvironment.RANDOM_PORT,
	properties = {
		"app.security.identity.provider=gateway",
		"management.health.db.enabled=false",
		"management.health.mail.enabled=false"
	}
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIT {

	@Autowired
	private MockMvc mockMvc;

	@LocalServerPort
	private int port;

	@Test
	void shouldExposePublicHealthEndpointWithoutIdentity() throws Exception {
		mockMvc.perform(get("/api/public/health")
				.header("X-Correlation-Id", "health-correlation"))
			.andExpect(status().isOk())
			.andExpect(header().doesNotExist("X-Correlation-Id"))
			.andExpect(jsonPath("$.status").value("UP"));
	}

	@Test
	void shouldExposeActuatorHealthEndpointWithoutIdentityWhenCorrelationIdIsPresent() throws Exception {
		mockMvc.perform(get("/actuator/health")
				.header("X-Correlation-Id", "actuator-health-correlation"))
			.andExpect(status().isOk())
			.andExpect(header().doesNotExist("X-Correlation-Id"))
			.andExpect(jsonPath("$.status").value("UP"));
	}

	@Test
	void shouldExposeActuatorLivenessEndpointWithoutIdentityWhenCorrelationIdIsPresent() throws Exception {
		mockMvc.perform(get("/actuator/health/liveness")
				.header("X-Correlation-Id", "actuator-liveness-correlation"))
			.andExpect(status().isOk())
			.andExpect(header().doesNotExist("X-Correlation-Id"))
			.andExpect(jsonPath("$.status").value("UP"));
	}

	@Test
	void shouldExposeActuatorReadinessEndpointWithoutIdentityWhenCorrelationIdIsPresent() throws Exception {
		mockMvc.perform(get("/actuator/health/readiness")
				.header("X-Correlation-Id", "actuator-readiness-correlation"))
			.andExpect(status().isOk())
			.andExpect(header().doesNotExist("X-Correlation-Id"))
			.andExpect(jsonPath("$.status").value("UP"));
	}

	@Test
	void shouldExposeActuatorHealthEndpointWithoutCorrelationId() throws Exception {
		mockMvc.perform(get("/actuator/health"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("UP"));
	}

	@Test
	void shouldExposeActuatorLivenessEndpointWithoutCorrelationId() throws Exception {
		mockMvc.perform(get("/actuator/health/liveness"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("UP"));
	}

	@Test
	void shouldExposeActuatorReadinessEndpointWithoutCorrelationId() throws Exception {
		mockMvc.perform(get("/actuator/health/readiness"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("UP"));
	}

	@Test
	void shouldNotExposeOtherActuatorEndpoints() throws Exception {
		mockMvc.perform(get("/actuator/env")
				.header("X-Correlation-Id", "actuator-env-correlation"))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.status").value(401))
			.andExpect(jsonPath("$.message").value("Autenticacao obrigatoria para acessar este recurso."))
			.andExpect(jsonPath("$.errors").isEmpty());
	}

	@Test
	void shouldRejectAdminEndpointWithoutIdentity() throws Exception {
		mockMvc.perform(get("/api/admin/session")
				.header("X-Correlation-Id", "unauthorized-correlation"))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.status").value(401))
			.andExpect(jsonPath("$.message").value("Autenticacao obrigatoria para acessar este recurso."))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(header().doesNotExist("X-Correlation-Id"));
	}

	@Test
	void shouldRejectProtectedApiEndpointWithoutIdentity() throws Exception {
		mockMvc.perform(get("/api/admin/servicos")
				.header("X-Correlation-Id", "protected-without-identity"))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.status").value(401))
			.andExpect(jsonPath("$.message").value("Autenticacao obrigatoria para acessar este recurso."))
			.andExpect(jsonPath("$.errors").isEmpty());
	}

	@Test
	void shouldRejectRequestWithoutCorrelationId() throws Exception {
		mockMvc.perform(get("/api/public/health"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.message").value("X-Correlation-Id header is required"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(header().doesNotExist("X-Correlation-Id"));
	}

	@Test
	void shouldRejectRequestWithBlankCorrelationId() throws Exception {
		mockMvc.perform(get("/api/public/health")
				.header("X-Correlation-Id", " "))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.message").value("X-Correlation-Id header is required"))
			.andExpect(jsonPath("$.errors").isEmpty())
			.andExpect(header().doesNotExist("X-Correlation-Id"));
	}

	@Test
	void shouldRejectRuntimeHttpRequestWithoutCorrelationIdBeforeSecurityErrorHandling() throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create("http://localhost:" + port + "/api/public/health"))
			.GET()
			.build();

		HttpResponse<String> response = HttpClient.newHttpClient()
			.send(request, HttpResponse.BodyHandlers.ofString());

		org.junit.jupiter.api.Assertions.assertEquals(400, response.statusCode());
		org.junit.jupiter.api.Assertions.assertTrue(response.body().contains("\"status\":400"));
		org.junit.jupiter.api.Assertions.assertTrue(response.body().contains("\"message\":\"X-Correlation-Id header is required\""));
		org.junit.jupiter.api.Assertions.assertFalse(response.body().contains("Autenticacao obrigatoria para acessar este recurso."));
	}

	@Test
	void shouldRejectPartialIdentityContext() throws Exception {
		mockMvc.perform(get("/api/admin/session")
				.header("X-Correlation-Id", "partial-correlation")
				.header("X-Authenticated-Subject", "partial-subject"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void shouldRejectInactiveUserAsInvalidAuthenticationContext() throws Exception {
		mockMvc.perform(get("/api/admin/session")
				.headers(identityHeaders("ADMIN", "", "INACTIVE", null)))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.status").value(401))
			.andExpect(jsonPath("$.message").value("Autenticacao obrigatoria para acessar este recurso."))
			.andExpect(jsonPath("$.errors").isEmpty());
	}

	@Test
	void shouldRejectCustomerFromAdminEndpoint() throws Exception {
		mockMvc.perform(get("/api/admin/session")
				.headers(identityHeaders("CUSTOMER", "SERVICE_ORDER_TRACK_OWN", "ACTIVE", UUID.randomUUID())))
			.andExpect(status().isForbidden());
	}

	@Test
	void shouldRejectAuthenticatedRequestWithoutCorrelationId() throws Exception {
		HttpHeaders headers = identityHeaders("ADMIN", "SERVICE_ORDER_MANAGE", "ACTIVE", null);
		headers.remove("X-Correlation-Id");

		mockMvc.perform(get("/api/admin/session")
				.headers(headers))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.message").value("X-Correlation-Id header is required"))
			.andExpect(jsonPath("$.errors").isEmpty());
	}

	@Test
	void shouldExposeAuthenticatedAdminContext() throws Exception {
		mockMvc.perform(get("/api/admin/session")
				.headers(identityHeaders("ADMIN", "SERVICE_ORDER_MANAGE", "ACTIVE", null)))
			.andExpect(status().isOk())
			.andExpect(header().doesNotExist("X-Correlation-Id"))
			.andExpect(jsonPath("$.subject").value("authenticated-subject"))
			.andExpect(jsonPath("$.status").value("ACTIVE"))
			.andExpect(jsonPath("$.roles[0]").value("ADMIN"))
			.andExpect(jsonPath("$.permissions[0]").value("SERVICE_ORDER_MANAGE"))
			.andExpect(jsonPath("$.correlationId").value("correlation-123"))
			.andExpect(jsonPath("$.authenticated").value(true));
	}

	@Test
	void shouldAllowProtectedApiEndpointWithValidAdminIdentity() throws Exception {
		mockMvc.perform(get("/api/admin/servicos")
				.headers(identityHeaders("ADMIN", "SERVICE_ORDER_MANAGE", "ACTIVE", null)))
			.andExpect(status().isOk());
	}

	private HttpHeaders identityHeaders(String roles, String permissions, String userStatus, UUID customerId) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Authenticated-Subject", "authenticated-subject");
		if (customerId != null) {
			headers.add("X-Authenticated-Customer-Id", customerId.toString());
		}
		headers.add("X-Authenticated-Status", userStatus);
		headers.add("X-Authenticated-Roles", roles);
		headers.add("X-Authenticated-Permissions", permissions);
		headers.add("X-Correlation-Id", "correlation-123");
		return headers;
	}
}
