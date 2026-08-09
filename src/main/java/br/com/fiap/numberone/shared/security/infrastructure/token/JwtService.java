package br.com.fiap.numberone.shared.security.infrastructure.token;

import br.com.fiap.numberone.shared.security.domain.entities.AdminUser;
import br.com.fiap.numberone.shared.security.domain.valueobjects.JwtPrincipal;
import br.com.fiap.numberone.shared.security.infrastructure.config.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

	private final SecurityProperties securityProperties;

	public JwtService(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
	}

	public String generateToken(AdminUser user) {
		Instant now = Instant.now();
		Instant expiration = now.plus(securityProperties.getJwt().getAccessTokenExpiration());

		return Jwts.builder()
			.subject(user.getUsername())
			.issuer(securityProperties.getJwt().getIssuer())
			.issuedAt(Date.from(now))
			.expiration(Date.from(expiration))
			.claim("role", user.getRole())
			.signWith(signingKey())
			.compact();
	}

	public Optional<JwtPrincipal> parse(String token) {
		try {
			Claims claims = Jwts.parser()
				.verifyWith(signingKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();

			return Optional.of(new JwtPrincipal(claims.getSubject(), claims.get("role", String.class)));
		} catch (JwtException | IllegalArgumentException exception) {
			return Optional.empty();
		}
	}

	private SecretKey signingKey() {
		return Keys.hmacShaKeyFor(securityProperties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8));
	}
}
