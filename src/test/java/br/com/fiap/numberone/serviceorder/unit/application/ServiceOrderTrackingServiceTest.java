package br.com.fiap.numberone.serviceorder.unit.application;

import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderGateway;
import br.com.fiap.numberone.serviceorder.application.services.ServiceOrderTrackingService;
import br.com.fiap.numberone.serviceorder.domain.entities.ServiceOrder;
import br.com.fiap.numberone.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.fiap.numberone.shared.api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static br.com.fiap.numberone.serviceorder.support.ServiceOrderTestFixtures.serviceOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceOrderTrackingServiceTest {

    @Mock
    private ServiceOrderGateway serviceOrderGateway;

    private ServiceOrderTrackingService service;

    @BeforeEach
    void setUp() {
        service = new ServiceOrderTrackingService(serviceOrderGateway);
    }

    @Test
    void shouldReturnTrackingServiceOrder() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        ServiceOrder existingOrder = serviceOrder(serviceOrderId, ServiceOrderStatus.IN_PROGRESS);
        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.of(existingOrder));

        // Act
        ServiceOrder result = service.getTracking(serviceOrderId);

        // Assert
        assertThat(result).isSameAs(existingOrder);
    }

    @Test
    void shouldThrowWhenTrackingServiceOrderDoesNotExist() {
        // Arrange
        UUID serviceOrderId = UUID.randomUUID();
        when(serviceOrderGateway.findById(serviceOrderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.getTracking(serviceOrderId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Service order not found for id: " + serviceOrderId);
    }
}
