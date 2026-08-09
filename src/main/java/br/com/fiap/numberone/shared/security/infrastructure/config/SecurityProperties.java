package br.com.fiap.numberone.shared.security.infrastructure.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

	private final Jwt jwt = new Jwt();
	private final BootstrapAdmin bootstrapAdmin = new BootstrapAdmin();

	public Jwt getJwt() {
		return jwt;
	}

	public BootstrapAdmin getBootstrapAdmin() {
		return bootstrapAdmin;
	}

	public static class Jwt {
		private String issuer;
		private String secret;
		private Duration accessTokenExpiration = Duration.ofHours(1);

		public String getIssuer() {
			return issuer;
		}

		public void setIssuer(String issuer) {
			this.issuer = issuer;
		}

		public String getSecret() {
			return secret;
		}

		public void setSecret(String secret) {
			this.secret = secret;
		}

		public Duration getAccessTokenExpiration() {
			return accessTokenExpiration;
		}

		public void setAccessTokenExpiration(Duration accessTokenExpiration) {
			this.accessTokenExpiration = accessTokenExpiration;
		}
	}

	public static class BootstrapAdmin {
		private String username;
		private String password;
		private String role = "ADMIN";

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getRole() {
			return role;
		}

		public void setRole(String role) {
			this.role = role;
		}
	}
}
