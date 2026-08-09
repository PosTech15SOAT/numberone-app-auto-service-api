package br.com.fiap.numberone.serviceorder.infrastructure.config;

import br.com.fiap.numberone.inventory.application.services.InventoryMovementService;
import br.com.fiap.numberone.inventory.infrastructure.persistence.repositories.InventoryItemRepository;
import br.com.fiap.numberone.serviceorder.application.gateways.AutomotiveServiceGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.CustomerGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.InventoryItemGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.InventoryWithdrawalGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderBudgetApprovalNotificationGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderBudgetGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderItemGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.ServiceOrderItemSupplyGateway;
import br.com.fiap.numberone.serviceorder.application.gateways.VehicleGateway;
import br.com.fiap.numberone.serviceorder.application.services.ServiceOrderBudgetService;
import br.com.fiap.numberone.serviceorder.application.services.ServiceOrderItemService;
import br.com.fiap.numberone.serviceorder.application.services.ServiceOrderItemSupplyService;
import br.com.fiap.numberone.serviceorder.application.services.ServiceOrderService;
import br.com.fiap.numberone.serviceorder.application.services.ServiceOrderTrackingService;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.gateways.InventoryWithdrawalGatewayImpl;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.gateways.ServiceOrderInventoryItemGatewayImpl;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.gateways.ServiceOrderBudgetGatewayImpl;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.gateways.ServiceOrderGatewayImpl;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.gateways.ServiceOrderItemGatewayImpl;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.gateways.ServiceOrderItemSupplyGatewayImpl;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.mappers.InventoryItemMapper;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.mappers.ServiceOrderBudgetMapper;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.mappers.ServiceOrderItemMapper;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.mappers.ServiceOrderItemSupplyMapper;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.mappers.ServiceOrderMapper;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.repositories.ServiceOrderBudgetRepository;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.repositories.ServiceOrderItemRepository;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.repositories.ServiceOrderItemSupplyRepository;
import br.com.fiap.numberone.serviceorder.infrastructure.persistence.repositories.ServiceOrderRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceOrderBeansConfig {

    @Bean
    public ServiceOrderGateway serviceOrderGateway(ServiceOrderRepository serviceOrderRepository, ServiceOrderMapper serviceOrderMapper) {
        return new ServiceOrderGatewayImpl(serviceOrderRepository, serviceOrderMapper);
    }

    @Bean
    public ServiceOrderItemGateway serviceOrderItemGateway(ServiceOrderItemRepository serviceOrderItemRepository, ServiceOrderItemMapper serviceOrderItemMapper) {
        return new ServiceOrderItemGatewayImpl(serviceOrderItemRepository, serviceOrderItemMapper);
    }

    @Bean
    public ServiceOrderBudgetGateway serviceOrderBudgetGateway(ServiceOrderBudgetRepository serviceOrderBudgetRepository, ServiceOrderBudgetMapper serviceOrderBudgetMapper) {
        return new ServiceOrderBudgetGatewayImpl(serviceOrderBudgetRepository, serviceOrderBudgetMapper);
    }

    @Bean
    public InventoryItemGateway serviceOrderInventoryItemGateway(
            InventoryItemRepository inventoryItemRepository,
            InventoryItemMapper inventoryItemMapper
    ) {
        return new ServiceOrderInventoryItemGatewayImpl(inventoryItemRepository, inventoryItemMapper);
    }

    @Bean
    public ServiceOrderItemSupplyGateway serviceOrderItemSupplyGateway(
            ServiceOrderItemSupplyRepository serviceOrderItemSupplyRepository,
            ServiceOrderItemSupplyMapper serviceOrderItemSupplyMapper
    ) {
        return new ServiceOrderItemSupplyGatewayImpl(serviceOrderItemSupplyRepository, serviceOrderItemSupplyMapper);
    }

    @Bean
    public ServiceOrderService serviceOrderService(
            ServiceOrderGateway serviceOrderGateway,
            CustomerGateway customerGateway,
            VehicleGateway vehicleGateway
    ) {
        return new ServiceOrderService(
                serviceOrderGateway,
                customerGateway,
                vehicleGateway
        );
    }

    @Bean
    public ServiceOrderTrackingService serviceOrderTrackingService(ServiceOrderGateway serviceOrderGateway) {
        return new ServiceOrderTrackingService(serviceOrderGateway);
    }

    @Bean
    public ServiceOrderItemService serviceOrderItemService(
            ServiceOrderGateway serviceOrderGateway,
            ServiceOrderItemGateway serviceOrderItemGateway,
            AutomotiveServiceGateway automotiveServiceGateway,
            InventoryWithdrawalGateway inventoryWithdrawalGateway
    ) {
        return new ServiceOrderItemService(
                serviceOrderGateway,
                serviceOrderItemGateway,
                automotiveServiceGateway,
                inventoryWithdrawalGateway
        );
    }

    @Bean
    public ServiceOrderBudgetService serviceOrderBudgetService(
            ServiceOrderGateway serviceOrderGateway,
            ServiceOrderBudgetGateway serviceOrderBudgetGateway,
            ServiceOrderBudgetApprovalNotificationGateway serviceOrderBudgetApprovalNotificationGateway
    ) {
        return new ServiceOrderBudgetService(
                serviceOrderGateway,
                serviceOrderBudgetGateway,
                serviceOrderBudgetApprovalNotificationGateway
        );
    }

    @Bean
    public ServiceOrderItemSupplyService serviceOrderItemSupplyService(
            ServiceOrderItemSupplyGateway serviceOrderItemSupplyGateway,
            ServiceOrderItemGateway serviceOrderItemGateway,
            InventoryItemGateway serviceOrderInventoryItemGateway
    ) {
        return new ServiceOrderItemSupplyService(
                serviceOrderItemSupplyGateway,
                serviceOrderItemGateway,
                serviceOrderInventoryItemGateway
        );
    }

    @Bean
    public InventoryWithdrawalGateway inventoryWithdrawalGateway(
            br.com.fiap.numberone.inventory.application.gateways.InventoryItemGateway inventoryItemGateway,
            InventoryMovementService inventoryMovementService
    ) {
        return new InventoryWithdrawalGatewayImpl(inventoryItemGateway, inventoryMovementService);
    }
}
