package br.com.fiap.numberone.shared.api.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Object target = ex.getBindingResult().getTarget();
        Class<?> targetClass = target != null ? target.getClass() : null;

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> {
                    String jsonFieldName = resolveJsonFieldName(targetClass, fieldError.getField());
                    return jsonFieldName + ": " + fieldError.getDefaultMessage();
                })
                .toList();

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        InvalidFormatException invalidFormatException = findInvalidFormatException(ex);

        if (invalidFormatException != null) {
            String fieldName = extractFieldName(invalidFormatException);
            Class<?> targetType = invalidFormatException.getTargetType();

            if (targetType != null && targetType.isEnum()) {
                String acceptedValues = String.join(", ",
                        Arrays.stream(targetType.getEnumConstants())
                                .map(Object::toString)
                                .toList()
                );

                String errorDetail = String.format(
                        "O campo '%s' recebeu um valor inválido. Valores aceitos: [%s]",
                        fieldName,
                        acceptedValues
                );

                return ResponseEntity.badRequest().body(
                        new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Erro de validação",
                                List.of(errorDetail)
                        )
                );
            }
        }

        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Requisição inválida",
                        List.of("O corpo da requisição está inválido ou mal formatado")
                )
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Recurso não encontrado: {}", ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Erro inesperado", ex);

        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno, tente novamente mais tarde",
                List.of()
        );

        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                List.of()
        );

        return ResponseEntity.badRequest().body(response);
    }
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Recurso nao encontrado",
                List.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String paramName = ex.getName();
        Object value = ex.getValue();
        Class<?> requiredType = ex.getRequiredType();

        String mainMessage = "Parâmetro de solicitação inválido";

        String errorDetail = String.format(
                "O parâmetro '%s' recebeu o valor '%s', que é inválido. Tipo esperado: %s",
                paramName,
                value,
                requiredType != null ? requiredType.getSimpleName() : "desconhecido"
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        mainMessage,
                        List.of(errorDetail)
                ));
    }

    private String resolveJsonFieldName(Class<?> clazz, String fieldName) {
        if (clazz == null) {
            return fieldName;
        }

        try {
            Field field = clazz.getDeclaredField(fieldName);
            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);

            if (jsonProperty != null && !jsonProperty.value().isBlank()) {
                return jsonProperty.value();
            }
        } catch (NoSuchFieldException ignored) {
        }

        return fieldName;
    }

    private String extractFieldName(InvalidFormatException ex) {
        String pathReference = ex.getPathReference();

        if (pathReference != null) {
            int lastQuoteStart = pathReference.lastIndexOf('"');
            if (lastQuoteStart >= 0) {
                int previousQuote = pathReference.lastIndexOf('"', lastQuoteStart - 1);
                if (previousQuote >= 0) {
                    return pathReference.substring(previousQuote + 1, lastQuoteStart);
                }
            }
        }

        return "desconhecido";
    }

    private InvalidFormatException findInvalidFormatException(Throwable ex) {
        while (ex != null) {
            if (ex instanceof InvalidFormatException invalidFormatException) {
                return invalidFormatException;
            }
            ex = ex.getCause();
        }
        return null;
    }
}
