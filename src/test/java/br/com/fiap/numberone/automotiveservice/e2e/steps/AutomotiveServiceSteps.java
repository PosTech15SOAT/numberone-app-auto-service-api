package br.com.fiap.numberone.automotiveservice.e2e.steps;

import br.com.fiap.numberone.automotiveservice.infrastructure.persistence.repositories.AutoServiceRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AutomotiveServiceSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private AutoServiceRepository autoServiceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private Map<String, Object> requestBody;
    private HttpResponse<String> createResponse;
    private HttpResponse<String> findByIdResponse;
    private Map<String, Object> createResponseBody;
    private Map<String, Object> findByIdResponseBody;
    private String createdServiceId;
    private String accessToken;

    @Before
    public void setUp() {
        autoServiceRepository.deleteAll();
    }

    @Given("que existe um servico automotivo valido para cadastro")
    public void queExisteUmServicoAutomotivoValidoParaCadastro() {
        requestBody = Map.of(
                "codigo", "REV-E2E-001",
                "nome", "Revisao E2E",
                "descricao", "Revisao automotiva completa via Cucumber",
                "tipoServico", "REVISAO",
                "valorBase", new BigDecimal("350.00"),
                "tempoEstimadoMinutos", 120
        );
    }

    @When("eu cadastro o servico automotivo")
    public void euCadastroOServicoAutomotivo() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/admin/servicos"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + getAccessToken())
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                .build();

        createResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (createResponse.statusCode() == 201) {
            createResponseBody = objectMapper.readValue(createResponse.body(), Map.class);
            createdServiceId = (String) createResponseBody.get("id");
        }
    }

    @Then("o servico automotivo deve ser cadastrado com sucesso")
    public void oServicoAutomotivoDeveSerCadastradoComSucesso() {
        assertThat(createResponse.statusCode()).isEqualTo(201);
        assertThat(createdServiceId).isNotBlank();
        assertThat(createResponseBody.get("code")).isEqualTo("REV-E2E-001");
        assertThat(createResponseBody.get("name")).isEqualTo("Revisao E2E");
        assertThat(createResponseBody.get("serviceType")).isEqualTo("REVISAO");
        assertThat(createResponseBody.get("active")).isEqualTo(true);
        assertThat(createResponse.headers().firstValue("Location")).hasValueSatisfying(
                location -> assertThat(location).endsWith("/api/admin/servicos/" + createdServiceId)
        );
    }

    @When("eu consulto o servico automotivo cadastrado por id")
    public void euConsultoOServicoAutomotivoCadastradoPorId() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/admin/servicos/" + createdServiceId))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + getAccessToken())
                .GET()
                .build();

        findByIdResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (findByIdResponse.statusCode() == 200) {
            findByIdResponseBody = objectMapper.readValue(findByIdResponse.body(), Map.class);
        }
    }

    @Then("a consulta deve retornar o servico automotivo cadastrado")
    public void aConsultaDeveRetornarOServicoAutomotivoCadastrado() {
        assertThat(findByIdResponse.statusCode()).isEqualTo(200);
        assertThat(findByIdResponseBody.get("id")).isEqualTo(createdServiceId);
        assertThat(findByIdResponseBody.get("code")).isEqualTo("REV-E2E-001");
        assertThat(findByIdResponseBody.get("name")).isEqualTo("Revisao E2E");
        assertThat(findByIdResponseBody.get("description")).isEqualTo("Revisao automotiva completa via Cucumber");
        assertThat(findByIdResponseBody.get("serviceType")).isEqualTo("REVISAO");
        assertThat(findByIdResponseBody.get("estimatedTimeMinutes")).isEqualTo(120);
    }

    @SuppressWarnings("unchecked")
    private String getAccessToken() throws Exception {
        if (accessToken != null) {
            return accessToken;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/public/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("""
                        {
                          "username": "admin",
                          "password": "admin123456"
                        }
                        """))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        accessToken = (String) objectMapper.readValue(response.body(), Map.class).get("accessToken");
        return accessToken;
    }
}
