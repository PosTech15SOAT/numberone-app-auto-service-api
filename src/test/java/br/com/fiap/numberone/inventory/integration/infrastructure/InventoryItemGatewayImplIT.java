package br.com.fiap.numberone.inventory.integration.infrastructure;

import br.com.fiap.numberone.inventory.domain.entities.InventoryItem;
import br.com.fiap.numberone.inventory.infrastructure.persistence.entities.InventoryItemEntity;
import br.com.fiap.numberone.inventory.infrastructure.persistence.gateways.InventoryItemGatewayImpl;
import br.com.fiap.numberone.inventory.infrastructure.persistence.mappers.InventoryItemEntityMapper;
import br.com.fiap.numberone.inventory.infrastructure.persistence.repositories.InventoryItemRepository;
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
import java.util.Optional;
import java.util.UUID;

import static br.com.fiap.numberone.inventory.support.InventoryTestFactory.inventoryItem;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.flyway.enabled=false"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = InventoryItemGatewayImplIT.GatewayTestConfig.class)
@Sql(
        statements = {
                "DROP TABLE IF EXISTS movimentacao_estoque",
                "DROP TABLE IF EXISTS item_estoque",
                "CREATE TABLE item_estoque (id UUID PRIMARY KEY, codigo VARCHAR(100) NOT NULL UNIQUE, nome VARCHAR(150) NOT NULL, descricao VARCHAR(255), tipo_item VARCHAR(50) NOT NULL, unidade_medida VARCHAR(50) NOT NULL, custo_unitario NUMERIC(10,2) NOT NULL, preco_venda NUMERIC(10,2) NOT NULL, quantidade_estoque INT NOT NULL, estoque_minimo INT NOT NULL, marca VARCHAR(100), veiculo_aplicavel VARCHAR(255), ativo BOOLEAN NOT NULL, created_at TIMESTAMP NOT NULL, updated_at TIMESTAMP NOT NULL)"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
class InventoryItemGatewayImplIT {

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
    private InventoryItemGatewayImpl gateway;

    @Autowired
    private InventoryItemRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldSaveInventoryItem() {
        // Given
        InventoryItem item = inventoryItem(UUID.randomUUID(), "OLEO-001", true, 10);

        // When
        InventoryItem saved = gateway.save(item);

        // Then
        assertThat(saved.getId()).isEqualTo(item.getId());
        assertThat(saved.getCode()).isEqualTo("OLEO-001");
        assertThat(saved.getInventoryQuantity()).isEqualTo(10);
        assertThat(repository.existsById(item.getId())).isTrue();
    }

    @Test
    void shouldFindInventoryItemById() {
        // Given
        InventoryItem saved = gateway.save(inventoryItem(UUID.randomUUID(), "OLEO-001", true, 10));

        // When
        Optional<InventoryItem> result = gateway.findById(saved.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("OLEO-001");
    }

    @Test
    void shouldFindInventoryItemByCode() {
        // Given
        gateway.save(inventoryItem(UUID.randomUUID(), "OLEO-001", true, 10));

        // When
        Optional<InventoryItem> result = gateway.findByCode("OLEO-001");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Oleo de motor");
    }

    @Test
    void shouldCheckIfInventoryItemCodeExists() {
        // Given
        gateway.save(inventoryItem(UUID.randomUUID(), "OLEO-001", true, 10));

        // When / Then
        assertThat(gateway.existsByCode("OLEO-001")).isTrue();
        assertThat(gateway.existsByCode("FILTRO-001")).isFalse();
    }

    @Test
    void shouldFindAllActiveInventoryItems() {
        // Given
        gateway.save(inventoryItem(UUID.randomUUID(), "OLEO-001", true, 10));
        gateway.save(inventoryItem(UUID.randomUUID(), "FILTRO-001", true, 5));
        gateway.save(inventoryItem(UUID.randomUUID(), "INATIVO-001", false, 3));

        // When
        List<InventoryItem> result = gateway.findAllActive();

        // Then
        assertThat(result)
                .extracting(InventoryItem::getCode)
                .containsExactlyInAnyOrder("OLEO-001", "FILTRO-001");
    }

    @TestConfiguration
    @EnableAutoConfiguration
    @AutoConfigurationPackage(basePackageClasses = InventoryItemEntity.class)
    @EnableJpaRepositories(basePackageClasses = InventoryItemRepository.class)
    static class GatewayTestConfig {

        @Bean
        InventoryItemEntityMapper inventoryItemEntityMapper() {
            return Mappers.getMapper(InventoryItemEntityMapper.class);
        }

        @Bean
        InventoryItemGatewayImpl inventoryItemGateway(
                InventoryItemRepository repository,
                InventoryItemEntityMapper mapper
        ) {
            return new InventoryItemGatewayImpl(repository, mapper);
        }
    }
}
