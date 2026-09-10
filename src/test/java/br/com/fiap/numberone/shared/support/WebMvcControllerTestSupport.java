package br.com.fiap.numberone.shared.support;

import br.com.fiap.numberone.shared.api.exception.HttpErrorResponseWriter;
import br.com.fiap.numberone.shared.security.infrastructure.identity.AuthenticatedUserProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@TestPropertySource(properties = "management.statsd.metrics.export.enabled=false")
public abstract class WebMvcControllerTestSupport {

	@MockitoBean
	private AuthenticatedUserProperties authenticatedUserProperties;

	@MockitoBean
	private HttpErrorResponseWriter httpErrorResponseWriter;
}
