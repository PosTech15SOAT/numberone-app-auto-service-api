package br.com.fiap.numberone.shared.security.integration.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
	webEnvironment = WebEnvironment.RANDOM_PORT,
	properties = {
		"app.security.identity.provider=local",
		"management.health.db.enabled=false",
		"management.health.mail.enabled=false"
	}
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ActuatorSecurityLocalProviderIT {

	@Autowired
	private MockMvc mockMvc;

	@LocalServerPort
	private int port;

	@Test
	void shouldExposeActuatorHealthWithoutCorrelationIdWhenLocalProviderIsActive() throws Exception {
		mockMvc.perform(get("/actuator/health"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("UP"));
	}

	@Test
	void shouldExposeActuatorLivenessWithoutCorrelationIdWhenLocalProviderIsActive() throws Exception {
		mockMvc.perform(get("/actuator/health/liveness"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("UP"));
	}

	@Test
	void shouldExposeActuatorReadinessWithoutCorrelationIdWhenLocalProviderIsActive() throws Exception {
		mockMvc.perform(get("/actuator/health/readiness"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("UP"));
	}

	@Test
	void shouldExposeActuatorHealthWithoutCorrelationIdOverRealHttpWhenLocalProviderIsActive() throws Exception {
		HttpResponse<String> response = httpGet("/actuator/health");

		assertEquals(200, response.statusCode());
		assertTrue(response.body().contains("\"status\":\"UP\""));
	}

	@Test
	void shouldExposeActuatorLivenessWithoutCorrelationIdOverRealHttpWhenLocalProviderIsActive() throws Exception {
		HttpResponse<String> response = httpGet("/actuator/health/liveness");

		assertEquals(200, response.statusCode());
		assertTrue(response.body().contains("\"status\":\"UP\""));
	}

	@Test
	void shouldExposeActuatorReadinessWithoutCorrelationIdOverRealHttpWhenLocalProviderIsActive() throws Exception {
		HttpResponse<String> response = httpGet("/actuator/health/readiness");

		assertEquals(200, response.statusCode());
		assertTrue(response.body().contains("\"status\":\"UP\""));
	}

	private HttpResponse<String> httpGet(String path) throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create("http://localhost:" + port + path))
			.GET()
			.build();

		return HttpClient.newHttpClient()
			.send(request, HttpResponse.BodyHandlers.ofString());
	}
}
