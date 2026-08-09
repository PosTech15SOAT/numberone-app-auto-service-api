package br.com.fiap.numberone.customer.integration.api;

import br.com.fiap.numberone.customer.api.controllers.CustomerController;
import br.com.fiap.numberone.customer.api.exceptions.CustomerExceptionHandler;
import br.com.fiap.numberone.customer.api.mappers.CustomerApiMapper;
import br.com.fiap.numberone.customer.application.services.CustomerService;
import br.com.fiap.numberone.customer.domain.entities.Customer;
import br.com.fiap.numberone.customer.domain.enums.TipoDocumento;
import br.com.fiap.numberone.customer.domain.exceptions.CustomerDocumentException;
import br.com.fiap.numberone.customer.domain.exceptions.CustomerNotFoundException;
import br.com.fiap.numberone.shared.api.exception.GlobalExceptionHandler;
import br.com.fiap.numberone.shared.security.infrastructure.repositories.AdminUserRepository;
import br.com.fiap.numberone.shared.security.infrastructure.token.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
        CustomerApiMapper.class,
        CustomerExceptionHandler.class,
        GlobalExceptionHandler.class
})
class CustomerControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AdminUserRepository adminUserRepository;

    @Test
    void shouldCreateCustomer() throws Exception {
        // Arrange
        UUID customerId = UUID.randomUUID();
        Customer createdCustomer = customer(customerId, "Maria da Silva", "52998224725");

        when(customerService.create(any(Customer.class))).thenReturn(createdCustomer);

        // Act & Assert
        mockMvc.perform(post("/api/admin/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Maria da Silva",
                                  "tipoDocumento": "PESSOA_FISICA",
                                  "documento": "52998224725",
                                  "email": "maria@example.com",
                                  "telefone": "11999999999",
                                  "endereco": "Rua A",
                                  "ativo": true
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/admin/clientes/" + customerId))
                .andExpect(jsonPath("$.id").value(customerId.toString()))
                .andExpect(jsonPath("$.nome").value("Maria da Silva"))
                .andExpect(jsonPath("$.tipoDocumento").value("PESSOA_FISICA"))
                .andExpect(jsonPath("$.documento").value("52998224725"))
                .andExpect(jsonPath("$.email").value("maria@example.com"))
                .andExpect(jsonPath("$.telefone").value("11999999999"))
                .andExpect(jsonPath("$.endereco").value("Rua A"))
                .andExpect(jsonPath("$.ativo").value(true))
                .andExpect(jsonPath("$.criadoEm").exists());
    }

    @Test
    void shouldReturnValidationErrorsWhenCreatingInvalidCustomer() throws Exception {
        // Arrange
        String requestBody = """
                {
                  "nome": "",
                  "email": "email-invalido"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/admin/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray());

        verify(customerService, never()).create(any(Customer.class));
    }

    @Test
    void shouldReturnBusinessErrorWhenDocumentIsInvalid() throws Exception {
        // Arrange
        when(customerService.create(any(Customer.class)))
                .thenThrow(new CustomerDocumentException("CPF invalido"));

        // Act & Assert
        mockMvc.perform(post("/api/admin/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Maria da Silva",
                                  "tipoDocumento": "PESSOA_FISICA",
                                  "documento": "123",
                                  "email": "maria@example.com",
                                  "telefone": "11999999999",
                                  "endereco": "Rua A",
                                  "ativo": true
                                }
                                """))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("CPF invalido"));
    }

    @Test
    void shouldReturnCustomerById() throws Exception {
        // Arrange
        UUID customerId = UUID.randomUUID();
        when(customerService.findById(customerId)).thenReturn(customer(customerId, "Maria da Silva", "52998224725"));

        // Act & Assert
        mockMvc.perform(get("/api/admin/clientes/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId.toString()))
                .andExpect(jsonPath("$.nome").value("Maria da Silva"));
    }

    @Test
    void shouldReturnNotFoundWhenCustomerDoesNotExist() throws Exception {
        // Arrange
        UUID customerId = UUID.randomUUID();
        when(customerService.findById(customerId))
                .thenThrow(new CustomerNotFoundException("Cliente nao encontrado para o id: " + customerId));

        // Act & Assert
        mockMvc.perform(get("/api/admin/clientes/{id}", customerId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Cliente nao encontrado para o id: " + customerId));
    }

    @Test
    void shouldReturnAllCustomers() throws Exception {
        // Arrange
        Customer firstCustomer = customer(UUID.randomUUID(), "Maria da Silva", "52998224725");
        Customer secondCustomer = customer(UUID.randomUUID(), "Joao da Silva", "11144477735");
        when(customerService.findAll()).thenReturn(List.of(firstCustomer, secondCustomer));

        // Act & Assert
        mockMvc.perform(get("/api/admin/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Maria da Silva"))
                .andExpect(jsonPath("$[1].nome").value("Joao da Silva"));
    }

    @Test
    void shouldUpdateCustomer() throws Exception {
        // Arrange
        UUID customerId = UUID.randomUUID();
        Customer updatedCustomer = customer(customerId, "Maria Clara", "52998224725");
        when(customerService.update(any(UUID.class), any(Customer.class))).thenReturn(updatedCustomer);

        // Act & Assert
        mockMvc.perform(put("/api/admin/clientes/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Maria Clara",
                                  "tipoDocumento": "PESSOA_FISICA",
                                  "documento": "52998224725",
                                  "email": "maria.clara@example.com",
                                  "telefone": "11999999999",
                                  "endereco": "Rua B",
                                  "ativo": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId.toString()))
                .andExpect(jsonPath("$.nome").value("Maria Clara"));
    }

    @Test
    void shouldDeleteCustomer() throws Exception {
        // Arrange
        UUID customerId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/api/admin/clientes/{id}", customerId))
                .andExpect(status().isNoContent());

        verify(customerService).delete(customerId);
    }

    private static Customer customer(UUID id, String name, String document) {
        return Customer.builder()
                .id(id)
                .name(name)
                .documentType(TipoDocumento.PESSOA_FISICA)
                .document(document)
                .email("maria@example.com")
                .phone("11999999999")
                .address("Rua A")
                .active(true)
                .createdAt(LocalDateTime.of(2026, 4, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2026, 4, 2, 10, 0))
                .build();
    }
}

