package br.com.fiap.numberone.shared.security.integration.infrastructure;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "app.security.identity.provider=gateway")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIT {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldExposePublicHealthEndpointWithoutIdentity() throws Exception {
		mockMvc.perform(get("/api/public/health"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("UP"));
	}

	@Test
	void shouldRejectAdminEndpointWithoutIdentity() throws Exception {
		mockMvc.perform(get("/api/admin/session"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void shouldRejectPartialIdentityContext() throws Exception {
		mockMvc.perform(get("/api/admin/session")
				.header("X-Authenticated-Subject", "partial-subject"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void shouldRejectInactiveUser() throws Exception {
		mockMvc.perform(get("/api/admin/session")
				.headers(identityHeaders("ADMIN", "", "INACTIVE", null)))
			.andExpect(status().isForbidden());
	}

	@Test
	void shouldRejectCustomerFromAdminEndpoint() throws Exception {
		mockMvc.perform(get("/api/admin/session")
				.headers(identityHeaders("CUSTOMER", "SERVICE_ORDER_TRACK_OWN", "ACTIVE", UUID.randomUUID())))
			.andExpect(status().isForbidden());
	}

	@Test
	void shouldExposeAuthenticatedAdminContext() throws Exception {
		mockMvc.perform(get("/api/admin/session")
				.headers(identityHeaders("ADMIN", "SERVICE_ORDER_MANAGE", "ACTIVE", null)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.subject").value("authenticated-subject"))
			.andExpect(jsonPath("$.status").value("ACTIVE"))
			.andExpect(jsonPath("$.roles[0]").value("ADMIN"))
			.andExpect(jsonPath("$.permissions[0]").value("SERVICE_ORDER_MANAGE"))
			.andExpect(jsonPath("$.correlationId").value("correlation-123"))
			.andExpect(jsonPath("$.authenticated").value(true));
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
