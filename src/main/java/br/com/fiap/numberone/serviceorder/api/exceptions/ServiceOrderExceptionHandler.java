package br.com.fiap.numberone.serviceorder.api.exceptions;

import br.com.fiap.numberone.serviceorder.domain.exceptions.AutomotiveServiceNotActiveException;
import br.com.fiap.numberone.serviceorder.domain.exceptions.CustomerEmailException;
import br.com.fiap.numberone.serviceorder.domain.exceptions.CustomerNotActiveException;
import br.com.fiap.numberone.serviceorder.domain.exceptions.InvalidServiceOrderBudgetStatusException;
import br.com.fiap.numberone.serviceorder.domain.exceptions.InvalidServiceOrderItemStatusException;
import br.com.fiap.numberone.serviceorder.domain.exceptions.InvalidServiceOrderStatusException;
import br.com.fiap.numberone.serviceorder.domain.exceptions.ServiceOrderItemAlreadyInStatusException;
import br.com.fiap.numberone.serviceorder.domain.exceptions.ServiceOrderItemEndStatusException;
import br.com.fiap.numberone.shared.api.exception.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice(basePackages = "br.com.fiap.numberone.serviceorder")
public class ServiceOrderExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ServiceOrderExceptionHandler.class);

    @ExceptionHandler({
            InvalidServiceOrderStatusException.class,
            InvalidServiceOrderBudgetStatusException.class,
            InvalidServiceOrderItemStatusException.class,
            ServiceOrderItemAlreadyInStatusException.class,
            AutomotiveServiceNotActiveException.class,
            CustomerNotActiveException.class,
            CustomerEmailException.class,
            ServiceOrderItemEndStatusException.class
    })
    public ResponseEntity<ErrorResponse> handleValidationErrors(Exception ex) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_CONTENT.value(),
                ex.getMessage(),
                List.of()
        );

        return ResponseEntity.unprocessableContent().body(response);
    }
}
