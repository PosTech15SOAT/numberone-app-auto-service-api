package br.com.fiap.numberone.shared.security.integration.infrastructure;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIT {

	@Autowired
	private MockMvc mockMvc;

//	@Test
//	void shouldExposePublicHealthEndpoint() throws Exception {
//		mockMvc.perform(get("/api/public/health"))
//			.andExpect(status().isOk())
//			.andExpect(jsonPath("$.status").value("UP"));
//	}
//
//	@Test
//	void shouldRejectAdminEndpointWithoutToken() throws Exception {
//		mockMvc.perform(get("/api/admin/session"))
//			.andExpect(status().isUnauthorized());
//	}
//
//	@Test
//	void shouldAuthenticateAndAccessProtectedEndpoint() throws Exception {
//		String payload = mockMvc.perform(post("/api/public/auth/login")
//				.contentType(MediaType.APPLICATION_JSON)
//				.content("""
//					{
//					  "username": "admin",
//					  "password": "admin123456"
//					}
//					"""))
//			.andExpect(status().isOk())
//			.andExpect(jsonPath("$.accessToken").isNotEmpty())
//			.andReturn()
//			.getResponse()
//			.getContentAsString();
//
//		String accessToken = payload.replaceAll(".*\"accessToken\":\"([^\"]+)\".*", "$1");
//
//		mockMvc.perform(get("/api/admin/session")
//				.header("Authorization", "Bearer " + accessToken))
//			.andExpect(status().isOk())
//			.andExpect(jsonPath("$.username").value("admin"))
//			.andExpect(jsonPath("$.authenticated").value(true));
//	}
}
