package br.com.fiap.numberone.shared.security.infrastructure.http;

import br.com.fiap.numberone.shared.security.application.gateways.AuthenticatedUserProvider;
import br.com.fiap.numberone.shared.security.domain.exceptions.InvalidAuthenticatedUserContextException;
import br.com.fiap.numberone.shared.security.domain.valueobjects.AuthenticatedUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AuthenticatedUserAuthenticationFilter extends OncePerRequestFilter {

	private static final String ROLE_PREFIX = "ROLE_";

	private final AuthenticatedUserProvider authenticatedUserProvider;
	private final RestAuthenticationEntryPoint authenticationEntryPoint;
	private final RestAccessDeniedHandler accessDeniedHandler;

	public AuthenticatedUserAuthenticationFilter(
		AuthenticatedUserProvider authenticatedUserProvider,
		RestAuthenticationEntryPoint authenticationEntryPoint,
		RestAccessDeniedHandler accessDeniedHandler
	) {
		this.authenticatedUserProvider = authenticatedUserProvider;
		this.authenticationEntryPoint = authenticationEntryPoint;
		this.accessDeniedHandler = accessDeniedHandler;
	}

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			authenticatedUserProvider.currentUser().ifPresent(user -> authenticate(request, user));
		} catch (InvalidAuthenticatedUserContextException exception) {
			authenticationEntryPoint.commence(
				request,
				response,
				new BadCredentialsException("Invalid authenticated user context", exception)
			);
			return;
		} catch (InactiveAuthenticatedUserException exception) {
			accessDeniedHandler.handle(request, response, exception);
			return;
		}

		filterChain.doFilter(request, response);
	}

	private void authenticate(HttpServletRequest request, AuthenticatedUser user) {
		if (!user.isActive()) {
			throw new InactiveAuthenticatedUserException();
		}

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
			user,
			null,
			authorities(user)
		);
		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private List<SimpleGrantedAuthority> authorities(AuthenticatedUser user) {
		Set<String> authorities = new LinkedHashSet<>();
		user.roles().stream()
			.map(role -> role.startsWith(ROLE_PREFIX) ? role : ROLE_PREFIX + role)
			.forEach(authorities::add);
		authorities.addAll(user.permissions());
		return authorities.stream().map(SimpleGrantedAuthority::new).toList();
	}

	private static class InactiveAuthenticatedUserException extends AccessDeniedException {
		private InactiveAuthenticatedUserException() {
			super("Authenticated user is not active");
		}
	}
}
