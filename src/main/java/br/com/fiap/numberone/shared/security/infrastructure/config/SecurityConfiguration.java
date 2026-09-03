package br.com.fiap.numberone.shared.security.infrastructure.config;

import br.com.fiap.numberone.shared.security.application.gateways.AuthenticatedUserProvider;
import br.com.fiap.numberone.shared.security.infrastructure.http.AuthenticatedUserAuthenticationFilter;
import br.com.fiap.numberone.shared.security.infrastructure.http.RestAccessDeniedHandler;
import br.com.fiap.numberone.shared.security.infrastructure.http.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {

	@Bean
	AuthenticatedUserAuthenticationFilter authenticatedUserAuthenticationFilter(
		AuthenticatedUserProvider authenticatedUserProvider,
		RestAuthenticationEntryPoint authenticationEntryPoint,
		RestAccessDeniedHandler accessDeniedHandler
	) {
		return new AuthenticatedUserAuthenticationFilter(
			authenticatedUserProvider,
			authenticationEntryPoint,
			accessDeniedHandler
		);
	}

	@Bean
	SecurityFilterChain securityFilterChain(
		HttpSecurity http,
		AuthenticatedUserAuthenticationFilter authenticatedUserAuthenticationFilter,
		RestAuthenticationEntryPoint authenticationEntryPoint,
		RestAccessDeniedHandler accessDeniedHandler
	) throws Exception {
		return http
			.csrf(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.exceptionHandling(exception -> exception
				.authenticationEntryPoint(authenticationEntryPoint)
				.accessDeniedHandler(accessDeniedHandler))
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/api/public/health").permitAll()
				.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/public/ordens-servico/*/acompanhamento")
					.hasAnyAuthority("ROLE_ADMIN", "SERVICE_ORDER_TRACK_OWN")
				.requestMatchers(HttpMethod.GET, "/api/public/orcamentos-ordem-servico/*/aprovacao/**")
					.hasAnyAuthority("ROLE_ADMIN", "BUDGET_RESPOND_OWN")
				.requestMatchers("/api/admin/**").hasRole("ADMIN")
				.anyRequest().denyAll())
			.addFilterBefore(authenticatedUserAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.build();
	}
}
