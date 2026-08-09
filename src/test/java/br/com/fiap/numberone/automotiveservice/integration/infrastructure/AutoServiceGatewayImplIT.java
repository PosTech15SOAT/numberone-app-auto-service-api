package br.com.fiap.numberone.automotiveservice.integration.infrastructure;

import br.com.fiap.numberone.automotiveservice.domain.entities.AutomotiveService;
import br.com.fiap.numberone.automotiveservice.infrastructure.persistence.entities.AutomotiveServiceEntity;
import br.com.fiap.numberone.automotiveservice.infrastructure.persistence.gateways.AutoServiceGatewayImpl;
import br.com.fiap.numberone.automotiveservice.infrastructure.persistence.mappers.AutomotiveServicePersistenceMapper;
import br.com.fiap.numberone.automotiveservice.infrastructure.persistence.repositories.AutoServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
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

import static br.com.fiap.numberone.automotiveservice.support.AutomotiveServiceTestFactory.automotiveService;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.flyway.enabled=false"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = AutoServiceGatewayImplIT.GatewayTestConfig.class)
@Sql(
        statements = {
                "DROP TABLE IF EXISTS servico_automotivo",
                "CREATE TABLE servico_automotivo (id UUID PRIMARY KEY, codigo VARCHAR(100) NOT NULL UNIQUE, nome VARCHAR(150) NOT NULL, descricao VARCHAR(255), tipo_servico VARCHAR(50), valor_base NUMERIC(10,2) NOT NULL, tempo_estimado_minutos INT NOT NULL, ativo BOOLEAN NOT NULL, created_at TIMESTAMP NOT NULL, updated_at TIMESTAMP)"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
class AutoServiceGatewayImplIT {

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
    private AutoServiceGatewayImpl gateway;

    @Autowired
    private AutoServiceRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldSaveAutomotiveService() {
        // Given
        AutomotiveService service = automotiveService(UUID.randomUUID(), "REV-001", true);

        // When
        AutomotiveService saved = gateway.save(service);

        // Then
        assertThat(saved.getId()).isEqualTo(service.getId());
        assertThat(saved.getCode()).isEqualTo("REV-001");
        assertThat(repository.existsById(service.getId())).isTrue();
    }

    @Test
    void shouldFindAutomotiveServiceById() {
        // Given
        AutomotiveService saved = gateway.save(automotiveService(UUID.randomUUID(), "REV-001", true));

        // When
        Optional<AutomotiveService> result = gateway.findById(saved.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("REV-001");
    }

    @Test
    void shouldFindAutomotiveServiceByCode() {
        // Given
        gateway.save(automotiveService(UUID.randomUUID(), "REV-001", true));

        // When
        Optional<AutomotiveService> result = gateway.findByCode("REV-001");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Revisao completa");
    }

    @Test
    void shouldCheckIfAutomotiveServiceCodeExists() {
        // Given
        gateway.save(automotiveService(UUID.randomUUID(), "REV-001", true));

        // When / Then
        assertThat(gateway.existsByCode("REV-001")).isTrue();
        assertThat(gateway.existsByCode("ALI-001")).isFalse();
    }

    @Test
    void shouldFindAllActiveAutomotiveServices() {
        // Given
        gateway.save(automotiveService(UUID.randomUUID(), "REV-001", true));
        gateway.save(automotiveService(UUID.randomUUID(), "ALI-001", true));
        gateway.save(automotiveService(UUID.randomUUID(), "INA-001", false));

        // When
        List<AutomotiveService> result = gateway.findAllActive();

        // Then
        assertThat(result)
                .extracting(AutomotiveService::getCode)
                .containsExactlyInAnyOrder("REV-001", "ALI-001");
    }

    @TestConfiguration
    @EnableAutoConfiguration
    @AutoConfigurationPackage(basePackageClasses = AutomotiveServiceEntity.class)
    @EnableJpaRepositories(basePackageClasses = AutoServiceRepository.class)
    static class GatewayTestConfig {

        @Bean
        AutomotiveServicePersistenceMapper automotiveServicePersistenceMapper() {
            return Mappers.getMapper(AutomotiveServicePersistenceMapper.class);
        }

        @Bean
        AutoServiceGatewayImpl autoServiceGateway(
                AutoServiceRepository repository,
                AutomotiveServicePersistenceMapper mapper
        ) {
            return new AutoServiceGatewayImpl(repository, mapper);
        }
    }
}
