package br.com.fiap.numberone.shared.infrastructure.correlation;

import br.com.fiap.numberone.shared.api.exception.HttpErrorResponseWriter;
import br.com.fiap.numberone.shared.security.infrastructure.identity.AuthenticatedUserProperties;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CorrelationIdFilterTest {

	private static final org.slf4j.Logger testLogger = LoggerFactory.getLogger(CorrelationIdFilterTest.class);

	private final CorrelationIdFilter filter = new CorrelationIdFilter(
		new AuthenticatedUserProperties(),
		new HttpErrorResponseWriter(new ObjectMapper())
	);

	@AfterEach
	void cleanMdc() {
		MDC.remove(CorrelationIdResolver.MDC_KEY);
		MDC.remove("dd.trace_id");
		MDC.remove("dd.span_id");
	}

	@Test
	void shouldExposeCorrelationIdInMdcRequestAttributeAndResponse() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(CorrelationIdResolver.DEFAULT_HEADER_NAME, "teste-numberone-123");
		MockHttpServletResponse response = new MockHttpServletResponse();
		AtomicReference<String> correlationIdInChain = new AtomicReference<>();

		filter.doFilter(request, response, (servletRequest, servletResponse) ->
			correlationIdInChain.set(MDC.get(CorrelationIdResolver.MDC_KEY))
		);

		assertEquals("teste-numberone-123", correlationIdInChain.get());
		assertEquals("teste-numberone-123", request.getAttribute(CorrelationIdResolver.REQUEST_ATTRIBUTE));
		assertNull(response.getHeader(CorrelationIdResolver.DEFAULT_HEADER_NAME));
		assertNull(MDC.get(CorrelationIdResolver.MDC_KEY));
	}

	@Test
	void shouldRejectRequestWhenHeaderIsAbsent() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		filter.doFilter(request, response, new MockFilterChain());

		assertEquals(400, response.getStatus());
		assertNull(request.getAttribute(CorrelationIdResolver.REQUEST_ATTRIBUTE));
		assertNull(response.getHeader(CorrelationIdResolver.DEFAULT_HEADER_NAME));
		assertNull(MDC.get(CorrelationIdResolver.MDC_KEY));
	}

	@Test
	void shouldRejectRequestWhenHeaderIsBlank() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(CorrelationIdResolver.DEFAULT_HEADER_NAME, " ");
		MockHttpServletResponse response = new MockHttpServletResponse();

		filter.doFilter(request, response, new MockFilterChain());

		assertEquals(400, response.getStatus());
		assertNull(MDC.get(CorrelationIdResolver.MDC_KEY));
	}

	@Test
	void shouldKeepDatadogMdcFieldsAfterCleanup() throws Exception {
		MDC.put("dd.trace_id", "trace-1");
		MDC.put("dd.span_id", "span-1");

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(CorrelationIdResolver.DEFAULT_HEADER_NAME, "datadog-correlation");

		filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

		assertNull(MDC.get(CorrelationIdResolver.MDC_KEY));
		assertEquals("trace-1", MDC.get("dd.trace_id"));
		assertEquals("span-1", MDC.get("dd.span_id"));
	}

	@Test
	void shouldCleanupMdcOnErrorsWithoutReturningCorrelationIdHeader() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(CorrelationIdResolver.DEFAULT_HEADER_NAME, "error-correlation");
		MockHttpServletResponse response = new MockHttpServletResponse();

		assertThrows(ServletException.class, () ->
			filter.doFilter(request, response, (servletRequest, servletResponse) -> {
				throw new ServletException("failure");
			})
		);

		assertNull(response.getHeader(CorrelationIdResolver.DEFAULT_HEADER_NAME));
		assertNull(MDC.get(CorrelationIdResolver.MDC_KEY));
	}

	@Test
	void shouldIncludeCorrelationIdInStructuredLogMdc() throws Exception {
		Logger logger = (Logger) LoggerFactory.getLogger(CorrelationIdFilterTest.class);
		ListAppender<ILoggingEvent> appender = new ListAppender<>();
		appender.start();
		logger.addAppender(appender);

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(CorrelationIdResolver.DEFAULT_HEADER_NAME, "teste-julio-os-001");

		try {
			filter.doFilter(request, new MockHttpServletResponse(), (servletRequest, servletResponse) ->
				testLogger.info("correlation log test")
			);
		} finally {
			logger.detachAppender(appender);
		}

		ILoggingEvent event = appender.list.stream()
			.filter(loggingEvent -> loggingEvent.getLevel() == Level.INFO)
			.findFirst()
			.orElseThrow();
		assertEquals("teste-julio-os-001", event.getMDCPropertyMap().get("correlation_id"));
	}
}
