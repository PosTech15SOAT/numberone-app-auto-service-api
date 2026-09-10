package br.com.fiap.numberone.shared.infrastructure.correlation;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CorrelationIdResolverTest {

	@Test
	void shouldPreserveValidIncomingCorrelationId() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(CorrelationIdResolver.DEFAULT_HEADER_NAME, "teste-numberone-123");

		String correlationId = CorrelationIdResolver.resolve(request, CorrelationIdResolver.DEFAULT_HEADER_NAME);

		assertEquals("teste-numberone-123", correlationId);
		assertEquals("teste-numberone-123", request.getAttribute(CorrelationIdResolver.REQUEST_ATTRIBUTE));
	}

	@Test
	void shouldRejectAbsentHeader() {
		MockHttpServletRequest request = new MockHttpServletRequest();

		assertThrows(
			MissingCorrelationIdException.class,
			() -> CorrelationIdResolver.resolve(request, CorrelationIdResolver.DEFAULT_HEADER_NAME)
		);
	}

	@Test
	void shouldRejectBlankHeader() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(CorrelationIdResolver.DEFAULT_HEADER_NAME, " ");

		assertThrows(
			MissingCorrelationIdException.class,
			() -> CorrelationIdResolver.resolve(request, CorrelationIdResolver.DEFAULT_HEADER_NAME)
		);
	}

	@Test
	void shouldReuseRequestAttributeWhenAlreadyResolved() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(CorrelationIdResolver.DEFAULT_HEADER_NAME, "header-value");
		request.setAttribute(CorrelationIdResolver.REQUEST_ATTRIBUTE, "resolved-value");

		String correlationId = CorrelationIdResolver.resolve(request, CorrelationIdResolver.DEFAULT_HEADER_NAME);

		assertEquals("resolved-value", correlationId);
	}

	@Test
	void shouldAcceptNonUuidCorrelationId() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(CorrelationIdResolver.DEFAULT_HEADER_NAME, "teste-julio-os-123");

		String correlationId = CorrelationIdResolver.resolve(request, CorrelationIdResolver.DEFAULT_HEADER_NAME);

		assertEquals("teste-julio-os-123", correlationId);
	}
}
