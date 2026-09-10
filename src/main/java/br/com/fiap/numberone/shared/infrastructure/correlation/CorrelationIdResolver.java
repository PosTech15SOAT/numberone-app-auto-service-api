package br.com.fiap.numberone.shared.infrastructure.correlation;

import jakarta.servlet.http.HttpServletRequest;

public final class CorrelationIdResolver {

	public static final String DEFAULT_HEADER_NAME = "X-Correlation-Id";
	public static final String MDC_KEY = "correlation_id";
	public static final String REQUEST_ATTRIBUTE = CorrelationIdResolver.class.getName() + ".correlationId";
	private static final int MAX_LENGTH = 128;

	private CorrelationIdResolver() {
	}

	public static String resolve(HttpServletRequest request, String headerName) {
		Object existingCorrelationId = request.getAttribute(REQUEST_ATTRIBUTE);
		if (existingCorrelationId instanceof String value && isValid(value)) {
			return value;
		}

		String incomingCorrelationId = request.getHeader(headerName);
		if (!isValid(incomingCorrelationId)) {
			throw new MissingCorrelationIdException(headerName + " header is required");
		}

		request.setAttribute(REQUEST_ATTRIBUTE, incomingCorrelationId);
		return incomingCorrelationId;
	}

	public static boolean isValid(String value) {
		return value != null
			&& !value.isBlank()
			&& value.length() <= MAX_LENGTH
			&& value.chars().noneMatch(Character::isISOControl);
	}
}
