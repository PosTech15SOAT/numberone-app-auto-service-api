package br.com.fiap.numberone.inventory.integration.infrastructure;

import br.com.fiap.numberone.inventory.domain.entities.InventoryMovement;
import br.com.fiap.numberone.inventory.infrastructure.persistence.entities.InventoryMovementEntity;
import br.com.fiap.numberone.inventory.infrastructure.persistence.gateways.InventoryMovementGatewayImpl;
import br.com.fiap.numberone.inventory.infrastructure.persistence.mappers.InventoryMovementEntityMapper;
import br.com.fiap.numberone.inventory.infrastructure.persistence.repositories.InventoryMovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.UUID;

import static br.com.fiap.numberone.inventory.support.InventoryTestFactory.ITEM_ID;
import static br.com.fiap.numberone.inventory.support.InventoryTestFactory.ORIGIN_REFERENCE_ID;
import static br.com.fiap.numberone.inventory.support.InventoryTestFactory.RESPONSIBLE_USER_ID;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.flyway.enabled=false"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = InventoryMovementGatewayImplIT.GatewayTestConfig.class)
@Sql(
        statements = {
                "DROP TABLE IF EXISTS movimentacao_estoque",
                "CREATE TABLE movimentacao_estoque (id UUID PRIMARY KEY, id_item_estoque UUID NOT NULL, tipo_movimentacao VARCHAR(50) NOT NULL, origem_movimentacao VARCHAR(50) NOT NULL, referencia_origem_id UUID, quantidade_antes INT NOT NULL, quantidade_depois INT NOT NULL, observacao VARCHAR(500), usuario_responsavel_id UUID NOT NULL, created_at TIMESTAMP NOT NULL)"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
class InventoryMovementGatewayImplIT {

    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void configurePostgres(DynamicPropertyRegistry registry) {
        POSTGRES.start();
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
    }

    @Autowired
    private InventoryMovementGatewayImpl gateway;

    @Autowired
    private InventoryMovementRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldSaveInventoryMovement() {
        // Given
        InventoryMovement movement = InventoryMovement.createEntry(
                ITEM_ID,
                br.com.fiap.numberone.inventory.domain.enums.InventoryMovementOrigin.COMPRA,
                ORIGIN_REFERENCE_ID,
                10,
                5,
                "Compra de reposicao",
                RESPONSIBLE_USER_ID
        );

        // When
        InventoryMovement saved = gateway.save(movement);

        // Then
        assertThat(saved.getId()).isEqualTo(movement.getId());
        assertThat(saved.getInventoryItemId()).isEqualTo(ITEM_ID);
        assertThat(saved.getQuantityBefore()).isEqualTo(10);
        assertThat(saved.getQuantityAfter()).isEqualTo(15);
        assertThat(repository.existsById(movement.getId())).isTrue();
    }

    @Test
    void shouldFindInventoryMovementsByInventoryItemId() {
        // Given
        UUID anotherItemId = UUID.randomUUID();
        gateway.save(InventoryMovement.createEntry(
                ITEM_ID,
                br.com.fiap.numberone.inventory.domain.enums.InventoryMovementOrigin.COMPRA,
                ORIGIN_REFERENCE_ID,
                10,
                5,
                "Compra de reposicao",
                RESPONSIBLE_USER_ID
        ));
        gateway.save(InventoryMovement.createWithdrawal(
                ITEM_ID,
                br.com.fiap.numberone.inventory.domain.enums.InventoryMovementOrigin.ORDEM_SERVICO,
                ORIGIN_REFERENCE_ID,
                15,
                3,
                "Uso em ordem de servico",
                RESPONSIBLE_USER_ID
        ));
        gateway.save(InventoryMovement.createEntry(
                anotherItemId,
                br.com.fiap.numberone.inventory.domain.enums.InventoryMovementOrigin.COMPRA,
                ORIGIN_REFERENCE_ID,
                1,
                2,
                "Outra compra",
                RESPONSIBLE_USER_ID
        ));

        // When
        List<InventoryMovement> result = gateway.findByInventoryItemId(ITEM_ID);

        // Then
        assertThat(result)
                .hasSize(2)
                .allSatisfy(movement -> assertThat(movement.getInventoryItemId()).isEqualTo(ITEM_ID));
    }

    @TestConfiguration
    @EnableAutoConfiguration
    @AutoConfigurationPackage(basePackageClasses = InventoryMovementEntity.class)
    @EnableJpaRepositories(basePackageClasses = InventoryMovementRepository.class)
    static class GatewayTestConfig {

        @Bean
        InventoryMovementEntityMapper inventoryMovementEntityMapper() {
            return Mappers.getMapper(InventoryMovementEntityMapper.class);
        }

        @Bean
        InventoryMovementGatewayImpl inventoryMovementGateway(
                InventoryMovementRepository repository,
                InventoryMovementEntityMapper mapper
        ) {
            return new InventoryMovementGatewayImpl(repository, mapper);
        }
    }
}
