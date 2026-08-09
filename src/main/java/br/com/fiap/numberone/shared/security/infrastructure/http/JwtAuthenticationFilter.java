package br.com.fiap.numberone.shared.security.infrastructure.http;

import br.com.fiap.numberone.shared.security.domain.entities.AdminUser;
import br.com.fiap.numberone.shared.security.infrastructure.repositories.AdminUserRepository;
import br.com.fiap.numberone.shared.security.infrastructure.token.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final AdminUserRepository adminUserRepository;

	public JwtAuthenticationFilter(JwtService jwtService, AdminUserRepository adminUserRepository) {
		this.jwtService = jwtService;
		this.adminUserRepository = adminUserRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		String header = request.getHeader("Authorization");
		if (header == null || !header.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = header.substring(7);
		jwtService.parse(token)
			.flatMap(principal -> adminUserRepository.findByUsernameIgnoreCase(principal.username()))
			.filter(AdminUser::isEnabled)
			.ifPresent(user -> {
				if (SecurityContextHolder.getContext().getAuthentication() != null) {
					return;
				}

				AuthenticatedAdmin principal = new AuthenticatedAdmin(user.getUsername(), user.getRole());
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					principal,
					null,
					List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
				);
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			});

		filterChain.doFilter(request, response);
	}

	public record AuthenticatedAdmin(String username, String role) {
	}
}
