package br.com.fiap.numberone.shared.infrastructure.correlation;

import br.com.fiap.numberone.shared.api.exception.HttpErrorResponseWriter;
import br.com.fiap.numberone.shared.security.infrastructure.identity.AuthenticatedUserProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

	private static final String APPLICATION_API_PATH_PREFIX = "/api/";

	private final AuthenticatedUserProperties properties;
	private final HttpErrorResponseWriter errorResponseWriter;

	public CorrelationIdFilter(AuthenticatedUserProperties properties, HttpErrorResponseWriter errorResponseWriter) {
		this.properties = properties;
		this.errorResponseWriter = errorResponseWriter;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return !applicationPath(request).startsWith(APPLICATION_API_PATH_PREFIX);
	}

	private String applicationPath(HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		String contextPath = request.getContextPath();
		if (contextPath != null && !contextPath.isBlank() && requestUri.startsWith(contextPath)) {
			return requestUri.substring(contextPath.length());
		}
		return requestUri;
	}

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		String headerName = properties.getHeaders().getCorrelationId();
		String correlationId;
		try {
			correlationId = CorrelationIdResolver.resolve(request, headerName);
		} catch (MissingCorrelationIdException exception) {
			errorResponseWriter.write(response, HttpStatus.BAD_REQUEST, exception.getMessage());
			return;
		}

		MDC.put(CorrelationIdResolver.MDC_KEY, correlationId);

		try {
			filterChain.doFilter(request, response);
		} finally {
			MDC.remove(CorrelationIdResolver.MDC_KEY);
		}
	}
}
