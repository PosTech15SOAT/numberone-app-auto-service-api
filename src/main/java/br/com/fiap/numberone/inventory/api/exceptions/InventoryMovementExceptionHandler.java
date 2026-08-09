package br.com.fiap.numberone.inventory.api.exceptions;

import br.com.fiap.numberone.automotiveservice.domain.exceptions.*;
import br.com.fiap.numberone.inventory.domain.exceptions.*;
import br.com.fiap.numberone.shared.api.exception.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice(basePackages = "br.com.fiap.numberone.inventory")
public class InventoryMovementExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(InventoryMovementExceptionHandler.class);
    private static final int UNPROCESSABLE_ENTITY = 422;

    @ExceptionHandler({
            InventoryBusinessException.class
    })
    public ResponseEntity<ErrorResponse> handleBusinessExceptions(
            RuntimeException ex
    ) {

        ErrorResponse response = new ErrorResponse(
                UNPROCESSABLE_ENTITY,
                ex.getMessage(),
                List.of()
        );

        return ResponseEntity.status(HttpStatusCode.valueOf(UNPROCESSABLE_ENTITY)).body(response);
    }

}
