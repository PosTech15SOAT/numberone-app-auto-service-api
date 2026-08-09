package br.com.fiap.numberone.shared.config;

import java.io.IOException;
import java.util.List;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.tags.Tag;

@Configuration
public class OpenApiDocumentationCustomizer {

    private static final String APPLICATION_JSON = "application/json";
    private static final String TEXT_PLAIN = "text/plain";
    private static final String BEARER_AUTH = "bearerAuth";

    private static final String CUSTOMER_ID = "1ed09259-0f4f-4fd8-867c-a13d4d2fda4e";
    private static final String VEHICLE_ID = "74a3eaac-979c-4f93-a926-2a3595047db9";
    private static final String SERVICE_ID = "f459a647-c094-4702-92f3-cf224105707a";
    private static final String INVENTORY_ITEM_ID = "11111111-1111-1111-1111-111111111111";
    private static final String SERVICE_ORDER_ID = "54e94616-70ad-4ce7-b6f7-41c6747d802e";
    private static final String SERVICE_ORDER_ITEM_ID = "e7b2c7b1-1c11-4832-a699-6e738608f61e";
    private static final String BUDGET_ID = "b1fb7c09-d680-4227-81c5-6b38dbaa88b9";
    private static final String ADMIN_ID = "22222222-2222-2222-2222-222222222222";

    @Bean
    OpenApiCustomizer numberOneOperationDocumentation() {
        return openApi -> {
            registerErrorResponseSchema(openApi);
            configureTags(openApi);

            if (openApi.getPaths() == null) {
                return;
            }

            openApi.getPaths().forEach((path, pathItem) ->
                    pathItem.readOperationsMap().forEach((method, operation) ->
                            customizeOperation(path, method, operation)));
        };
    }

    private static void registerErrorResponseSchema(OpenAPI openApi) {
        if (openApi.getComponents() == null) {
            openApi.setComponents(new Components());
        }

        Schema<?> schema = new Schema<>()
                .type("object")
                .description("Formato padrao retornado pela API em erros de validacao, autenticacao, regra de negocio e falhas internas.");

        schema.addProperty("status", new IntegerSchema()
                .format("int32")
                .description("Codigo HTTP do erro.")
                .example(400));
        schema.addProperty("message", new StringSchema()
                .description("Mensagem principal do erro.")
                .example("Erro de validacao"));
        schema.addProperty("errors", new ArraySchema()
                .description("Lista de detalhes adicionais do erro.")
                .items(new StringSchema().example("nome: Nome e obrigatorio")));

        openApi.getComponents().addSchemas("ErrorResponse", schema);
    }

    private static void configureTags(OpenAPI openApi) {
        openApi.setTags(List.of(
                new Tag()
                        .name("Health")
                        .description("Verificacao publica de disponibilidade da API."),
                new Tag()
                        .name("Autenticacao e Sessao")
                        .description("Login administrativo com JWT e consulta da sessao autenticada."),
                new Tag()
                        .name("Clientes")
                        .description("Cadastro de clientes da oficina, incluindo tipo de documento, contato e endereco."),
                new Tag()
                        .name("Veiculos")
                        .description("Cadastro de veiculos vinculados aos clientes."),
                new Tag()
                        .name("Servicos Automotivos")
                        .description("Catalogo de servicos executados pela oficina, com valor base e tempo estimado."),
                new Tag()
                        .name("Estoque - Itens")
                        .description("Cadastro de pecas, insumos, lubrificantes e acessorios."),
                new Tag()
                        .name("Estoque - Movimentacoes")
                        .description("Entradas, baixas, ajustes e historico de movimentacao do estoque."),
                new Tag()
                        .name("Ordens de Servico")
                        .description("Fluxo principal da oficina: abertura, diagnostico, execucao, conclusao e entrega."),
                new Tag()
                        .name("Itens da Ordem de Servico")
                        .description("Servicos executados dentro de uma ordem de servico."),
                new Tag()
                        .name("Insumos da Ordem de Servico")
                        .description("Pecas e insumos consumidos por um item de servico da OS."),
                new Tag()
                        .name("Orcamentos")
                        .description("Geracao, aprovacao, rejeicao e links publicos de orcamento."),
                new Tag()
                        .name("Acompanhamento Publico")
                        .description("Consulta publica do andamento da ordem de servico pelo cliente.")
        ));
    }

    private static void customizeOperation(String path, PathItem.HttpMethod method, Operation operation) {
        if (path.startsWith("/api/public/")) {
            operation.setSecurity(List.of());
        } else if (path.startsWith("/api/admin/")) {
            operation.setSecurity(List.of(new SecurityRequirement().addList(BEARER_AUTH)));
        }

        describePathParameters(operation);

        switch (method.name() + " " + path) {
            case "GET /api/public/health" -> health(operation);
            case "POST /api/public/auth/login" -> login(operation);
            case "GET /api/admin/session" -> currentSession(operation);
            case "POST /api/admin/clientes" -> createCustomer(operation);
            case "PUT /api/admin/clientes/{id}" -> updateCustomer(operation);
            case "GET /api/admin/clientes/{id}" -> getCustomer(operation);
            case "GET /api/admin/clientes" -> listCustomers(operation);
            case "DELETE /api/admin/clientes/{id}" -> deleteCustomer(operation);
            case "POST /api/admin/veiculos" -> createVehicle(operation);
            case "PUT /api/admin/veiculos/{id}" -> updateVehicle(operation);
            case "GET /api/admin/veiculos/{id}" -> getVehicle(operation);
            case "GET /api/admin/veiculos" -> listVehicles(operation);
            case "DELETE /api/admin/veiculos/{id}" -> deleteVehicle(operation);
            case "POST /api/admin/servicos" -> createAutomotiveService(operation);
            case "PUT /api/admin/servicos/{id}" -> updateAutomotiveService(operation);
            case "GET /api/admin/servicos" -> listAutomotiveServices(operation);
            case "GET /api/admin/servicos/{id}" -> getAutomotiveService(operation);
            case "PATCH /api/admin/servicos/{id}/inativar" -> inactivateAutomotiveService(operation);
            case "PATCH /api/admin/servicos/{id}/ativar" -> activateAutomotiveService(operation);
            case "POST /api/admin/itens" -> createInventoryItem(operation);
            case "PUT /api/admin/itens/{id}" -> updateInventoryItem(operation);
            case "GET /api/admin/itens" -> listInventoryItems(operation);
            case "GET /api/admin/itens/{id}" -> getInventoryItem(operation);
            case "PATCH /api/admin/itens/{id}/inativar" -> inactivateInventoryItem(operation);
            case "PATCH /api/admin/itens/{id}/ativar" -> activateInventoryItem(operation);
            case "POST /api/admin/estoque/entrada" -> registerInventoryEntry(operation);
            case "POST /api/admin/estoque/baixa" -> registerInventoryWithdrawal(operation);
            case "POST /api/admin/estoque/ajuste" -> registerInventoryAdjustment(operation);
            case "GET /api/admin/estoque/itens/{itemId}/movimentacoes" -> listInventoryMovements(operation);
            case "POST /api/admin/ordens-servico" -> createServiceOrder(operation);
            case "GET /api/admin/ordens-servico" -> listServiceOrders(operation);
            case "GET /api/admin/ordens-servico/{id}" -> getServiceOrder(operation);
            case "PATCH /api/admin/ordens-servico/{id}/iniciar-diagnostico" -> addFinalDiagnosis(operation);
            case "GET /api/admin/ordens-servico/{id}/calcular-servicos" -> calculateServiceOrderValue(operation);
            case "GET /api/admin/ordens-servico/{id}/calcular-tempo-estimado" -> calculateEstimatedTime(operation);
            case "GET /api/admin/ordens-servico/{id}/tempo-medio-execucao-servicos" -> calculateAverageExecutionTime(operation);
            case "PATCH /api/admin/ordens-servico/{id}/cancelar" -> cancelServiceOrder(operation);
            case "PATCH /api/admin/ordens-servico/{id}/iniciar" -> startServiceOrder(operation);
            case "PATCH /api/admin/ordens-servico/{id}/concluir" -> completeServiceOrder(operation);
            case "PATCH /api/admin/ordens-servico/{id}/entregar" -> deliverServiceOrder(operation);
            case "POST /api/admin/itens-ordem-servico" -> createServiceOrderItem(operation);
            case "DELETE /api/admin/itens-ordem-servico/{id}" -> deleteServiceOrderItem(operation);
            case "PATCH /api/admin/itens-ordem-servico/{id}/iniciar" -> startServiceOrderItem(operation);
            case "PATCH /api/admin/itens-ordem-servico/{id}/cancelar" -> cancelServiceOrderItem(operation);
            case "PATCH /api/admin/itens-ordem-servico/{id}/concluir" -> completeServiceOrderItem(operation);
            case "POST /api/admin/itens-ordem-servico/{serviceOrderItemId}/insumos" -> createServiceOrderItemSupply(operation);
            case "PUT /api/admin/itens-ordem-servico/{serviceOrderItemId}/insumos/{id}" -> updateServiceOrderItemSupply(operation);
            case "GET /api/admin/itens-ordem-servico/{serviceOrderItemId}/insumos" -> listServiceOrderItemSupplies(operation);
            case "DELETE /api/admin/itens-ordem-servico/{serviceOrderItemId}/insumos/{id}" -> deleteServiceOrderItemSupply(operation);
            case "POST /api/admin/ordens-servico/{serviceOrderId}/orcamentos" -> createBudget(operation);
            case "PATCH /api/admin/orcamentos-ordem-servico/{id}/solicitar-aprovacao" -> requestBudgetApproval(operation);
            case "PATCH /api/admin/orcamentos-ordem-servico/{id}/aprovar" -> approveBudget(operation);
            case "PATCH /api/admin/orcamentos-ordem-servico/{id}/rejeitar" -> rejectBudget(operation);
            case "GET /api/public/orcamentos-ordem-servico/{id}/aprovacao/aprovar" -> approveBudgetByEmail(operation);
            case "GET /api/public/orcamentos-ordem-servico/{id}/aprovacao/rejeitar" -> rejectBudgetByEmail(operation);
            case "GET /api/public/ordens-servico/{id}/acompanhamento" -> trackServiceOrder(operation);
            default -> defaultOperation(operation, path);
        }
    }

    private static void health(Operation operation) {
        describe(operation, "Health", "Consultar status da API",
                "Endpoint publico para confirmar se a aplicacao esta em execucao.");
        response(operation, "200", "API disponivel.", "health", "Status da aplicacao", """
                {
                  "status": "UP",
                  "application": "numberone",
                  "activeProfiles": [],
                  "timestamp": "2026-05-01T12:38:24.822997446Z"
                }
                """);
    }

    private static void login(Operation operation) {
        describe(operation, "Autenticacao e Sessao", "Autenticar administrador",
                "Realiza login administrativo e retorna um token JWT para uso nas rotas protegidas.");
        request(operation, "Credenciais locais criadas automaticamente em ambiente de desenvolvimento.",
                "insomnia", "Exemplo usado no Insomnia", """
                {
                  "username": "admin",
                  "password": "admin123456"
                }
                """);
        response(operation, "200", "Login realizado com sucesso.", "token", "Token JWT retornado", """
                {
                  "accessToken": "token.jwt.exemplo",
                  "tokenType": "Bearer",
                  "expiresInSeconds": 3600
                }
                """);
        error(operation, "400", "Dados obrigatorios ausentes ou invalidos.", badRequestExample());
        error(operation, "401", "Credenciais invalidas.", """
                {
                  "status": 401,
                  "message": "Usuario ou senha invalidos",
                  "errors": []
                }
                """);
    }

    private static void currentSession(Operation operation) {
        describe(operation, "Autenticacao e Sessao", "Consultar sessao autenticada",
                "Retorna usuario, papel e status da sessao com base no token JWT enviado.");
        response(operation, "200", "Sessao autenticada.", "sessao", "Sessao admin", """
                {
                  "username": "admin",
                  "role": "ADMIN",
                  "authenticated": true
                }
                """);
        protectedErrors(operation, false, false);
    }

    private static void createCustomer(Operation operation) {
        describe(operation, "Clientes", "Cadastrar cliente",
                "Cria um cliente ativo para abertura posterior de veiculos e ordens de servico.");
        request(operation, "Dados do cliente. O tipoDocumento aceita PESSOA_FISICA ou PESSOA_JURIDICA.",
                "cliente", "Cliente pessoa fisica", customerRequest());
        response(operation, "201", "Cliente criado.", "cliente", "Cliente criado", customerResponse());
        protectedErrors(operation, false, true);
    }

    private static void updateCustomer(Operation operation) {
        describe(operation, "Clientes", "Atualizar cliente",
                "Atualiza os dados cadastrais de um cliente existente.");
        request(operation, "Dados completos do cliente para atualizacao.",
                "cliente", "Cliente atualizado", customerRequest());
        response(operation, "200", "Cliente atualizado.", "cliente", "Cliente atualizado", customerResponse());
        protectedErrors(operation, true, true);
    }

    private static void getCustomer(Operation operation) {
        describe(operation, "Clientes", "Buscar cliente por ID",
                "Consulta um cliente pelo identificador UUID.");
        response(operation, "200", "Cliente encontrado.", "cliente", "Cliente encontrado", customerResponse());
        protectedErrors(operation, true, false);
    }

    private static void listCustomers(Operation operation) {
        describe(operation, "Clientes", "Listar clientes",
                "Lista todos os clientes cadastrados.");
        response(operation, "200", "Clientes cadastrados.", "clientes", "Lista de clientes", "[" + customerResponse() + "]");
        protectedErrors(operation, false, false);
    }

    private static void deleteCustomer(Operation operation) {
        describe(operation, "Clientes", "Excluir cliente",
                "Remove um cliente pelo identificador UUID.");
        noContent(operation, "204", "Cliente removido.");
        protectedErrors(operation, true, false);
    }

    private static void createVehicle(Operation operation) {
        describe(operation, "Veiculos", "Cadastrar veiculo",
                "Cria um veiculo vinculado a um cliente existente.");
        request(operation, "A placa deve estar no formato antigo ABC-1234 ou Mercosul ABC1D23.",
                "veiculo", "Veiculo do cliente", vehicleRequest());
        response(operation, "201", "Veiculo criado.", "veiculo", "Veiculo criado", vehicleResponse());
        protectedErrors(operation, true, true);
    }

    private static void updateVehicle(Operation operation) {
        describe(operation, "Veiculos", "Atualizar veiculo",
                "Atualiza os dados de um veiculo existente.");
        request(operation, "Dados completos do veiculo para atualizacao.",
                "veiculo", "Veiculo atualizado", vehicleRequest());
        response(operation, "200", "Veiculo atualizado.", "veiculo", "Veiculo atualizado", vehicleResponse());
        protectedErrors(operation, true, true);
    }

    private static void getVehicle(Operation operation) {
        describe(operation, "Veiculos", "Buscar veiculo por ID",
                "Consulta um veiculo pelo identificador UUID.");
        response(operation, "200", "Veiculo encontrado.", "veiculo", "Veiculo encontrado", vehicleResponse());
        protectedErrors(operation, true, false);
    }

    private static void listVehicles(Operation operation) {
        describe(operation, "Veiculos", "Listar veiculos",
                "Lista os veiculos cadastrados.");
        response(operation, "200", "Veiculos cadastrados.", "veiculos", "Lista de veiculos", "[" + vehicleResponse() + "]");
        protectedErrors(operation, false, false);
    }

    private static void deleteVehicle(Operation operation) {
        describe(operation, "Veiculos", "Excluir veiculo",
                "Remove um veiculo pelo identificador UUID.");
        noContent(operation, "204", "Veiculo removido.");
        protectedErrors(operation, true, false);
    }

    private static void createAutomotiveService(Operation operation) {
        describe(operation, "Servicos Automotivos", "Cadastrar servico automotivo",
                "Cria um item do catalogo de servicos da oficina.");
        request(operation, "Servico com valor base e tempo estimado em minutos.",
                "servico", "Servico de revisao", automotiveServiceRequest());
        response(operation, "201", "Servico criado.", "servico", "Servico criado", automotiveServiceResponse());
        operationalErrors(operation, true);
    }

    private static void updateAutomotiveService(Operation operation) {
        describe(operation, "Servicos Automotivos", "Atualizar servico automotivo",
                "Atualiza um servico do catalogo.");
        request(operation, "Dados completos do servico para atualizacao.",
                "servico", "Servico atualizado", automotiveServiceRequest());
        response(operation, "200", "Servico atualizado.", "servico", "Servico atualizado", automotiveServiceResponse());
        operationalErrors(operation, true);
    }

    private static void listAutomotiveServices(Operation operation) {
        describe(operation, "Servicos Automotivos", "Listar servicos automotivos",
                "Lista todos os servicos cadastrados no catalogo.");
        response(operation, "200", "Servicos cadastrados.", "servicos", "Lista de servicos", "[" + automotiveServiceResponse() + "]");
        operationalErrors(operation, false);
    }

    private static void getAutomotiveService(Operation operation) {
        describe(operation, "Servicos Automotivos", "Buscar servico automotivo por ID",
                "Consulta um servico do catalogo pelo identificador UUID.");
        response(operation, "200", "Servico encontrado.", "servico", "Servico encontrado", automotiveServiceResponse());
        operationalErrors(operation, true);
    }

    private static void inactivateAutomotiveService(Operation operation) {
        describe(operation, "Servicos Automotivos", "Inativar servico automotivo",
                "Marca um servico como inativo para impedir novo uso operacional.");
        noContent(operation, "204", "Servico inativado.");
        operationalErrors(operation, true);
    }

    private static void activateAutomotiveService(Operation operation) {
        describe(operation, "Servicos Automotivos", "Ativar servico automotivo",
                "Reativa um servico previamente inativado.");
        noContent(operation, "204", "Servico ativado.");
        operationalErrors(operation, true);
    }

    private static void createInventoryItem(Operation operation) {
        describe(operation, "Estoque - Itens", "Cadastrar item de estoque",
                "Cria uma peca, insumo, lubrificante ou acessorio no estoque.");
        request(operation, "Cadastro completo do item com custos, preco de venda e quantidade inicial.",
                "item", "Item de estoque", inventoryItemRequest());
        response(operation, "201", "Item criado.", "item", "Item criado", inventoryItemResponse());
        operationalErrors(operation, true);
    }

    private static void updateInventoryItem(Operation operation) {
        describe(operation, "Estoque - Itens", "Atualizar item de estoque",
                "Atualiza os dados cadastrais de um item de estoque.");
        request(operation, "Dados completos do item para atualizacao.",
                "item", "Item atualizado", inventoryItemRequest());
        response(operation, "200", "Item atualizado.", "item", "Item atualizado", inventoryItemResponse());
        operationalErrors(operation, true);
    }

    private static void listInventoryItems(Operation operation) {
        describe(operation, "Estoque - Itens", "Listar itens de estoque",
                "Lista os itens cadastrados no estoque.");
        response(operation, "200", "Itens cadastrados.", "itens", "Lista de itens", "[" + inventoryItemResponse() + "]");
        operationalErrors(operation, false);
    }

    private static void getInventoryItem(Operation operation) {
        describe(operation, "Estoque - Itens", "Buscar item de estoque por ID",
                "Consulta um item de estoque pelo identificador UUID.");
        response(operation, "200", "Item encontrado.", "item", "Item encontrado", inventoryItemResponse());
        operationalErrors(operation, true);
    }

    private static void inactivateInventoryItem(Operation operation) {
        describe(operation, "Estoque - Itens", "Inativar item de estoque",
                "Marca um item como inativo para impedir novo uso operacional.");
        noContent(operation, "204", "Item inativado.");
        operationalErrors(operation, true);
    }

    private static void activateInventoryItem(Operation operation) {
        describe(operation, "Estoque - Itens", "Ativar item de estoque",
                "Reativa um item previamente inativado.");
        noContent(operation, "204", "Item ativado.");
        operationalErrors(operation, true);
    }

    private static void registerInventoryEntry(Operation operation) {
        describe(operation, "Estoque - Movimentacoes", "Registrar entrada de estoque",
                "Registra entrada de quantidade no estoque e grava historico da movimentacao.");
        request(operation, "Entrada manual, por compra ou por referencia operacional.",
                "entrada", "Entrada de estoque", inventoryEntryRequest());
        response(operation, "201", "Entrada registrada.", "movimentacao", "Movimentacao criada", inventoryMovementResponse("ENTRADA"));
        operationalErrors(operation, true);
    }

    private static void registerInventoryWithdrawal(Operation operation) {
        describe(operation, "Estoque - Movimentacoes", "Registrar baixa de estoque",
                "Registra baixa de quantidade no estoque, normalmente por consumo em ordem de servico.");
        request(operation, "Baixa manual ou vinculada a uma ordem de servico.",
                "baixa", "Baixa de estoque", inventoryWithdrawalRequest());
        response(operation, "201", "Baixa registrada.", "movimentacao", "Movimentacao criada", inventoryMovementResponse("BAIXA"));
        operationalErrors(operation, true);
    }

    private static void registerInventoryAdjustment(Operation operation) {
        describe(operation, "Estoque - Movimentacoes", "Registrar ajuste de estoque",
                "Ajusta o saldo final de um item e registra auditoria da alteracao.");
        request(operation, "Ajuste com quantidade final e justificativa obrigatoria.",
                "ajuste", "Ajuste de estoque", inventoryAdjustmentRequest());
        response(operation, "201", "Ajuste registrado.", "movimentacao", "Movimentacao criada", inventoryMovementResponse("AJUSTE"));
        operationalErrors(operation, true);
    }

    private static void listInventoryMovements(Operation operation) {
        describe(operation, "Estoque - Movimentacoes", "Listar movimentacoes de um item",
                "Consulta o historico de entradas, baixas e ajustes de um item de estoque.");
        response(operation, "200", "Movimentacoes do item.", "movimentacoes", "Lista de movimentacoes",
                "[" + inventoryMovementResponse("ENTRADA") + "]");
        operationalErrors(operation, true);
    }

    private static void createServiceOrder(Operation operation) {
        describe(operation, "Ordens de Servico", "Criar ordem de servico",
                "Abre uma OS para um cliente e veiculo, registrando entrada, problema informado e diagnostico inicial.");
        request(operation, "Exemplo baseado na collection Insomnia_2026-04-30.yaml.",
                "insomnia", "Criacao de ordem de servico", """
                {
                  "idCliente": "1ed09259-0f4f-4fd8-867c-a13d4d2fda4e",
                  "descricaoInicial": "Teste ordem",
                  "idVeiculo": "74a3eaac-979c-4f93-a926-2a3595047db9",
                  "descricaoDiagnostico": "Teste",
                  "dataHoraEntrada": "2026-04-28T10:30:00"
                }
                """);
        response(operation, "201", "Ordem de servico criada.", "ordemServico", "OS criada", serviceOrderResponse("RECEBIDA"));
        protectedErrors(operation, true, true);
    }

    private static void listServiceOrders(Operation operation) {
        describe(operation, "Ordens de Servico", "Listar ordens de servico",
                "Lista as ordens de servico cadastradas.");
        response(operation, "200", "Ordens de servico cadastradas.", "ordensServico", "Lista de OS",
                "[" + serviceOrderResponse("RECEBIDA") + "]");
        protectedErrors(operation, false, false);
    }

    private static void getServiceOrder(Operation operation) {
        describe(operation, "Ordens de Servico", "Buscar ordem de servico por ID",
                "Consulta os dados completos de uma ordem de servico pelo identificador UUID.");
        response(operation, "200", "Ordem de servico encontrada.", "ordemServico", "OS encontrada", serviceOrderResponse("EM_DIAGNOSTICO"));
        protectedErrors(operation, true, false);
    }

    private static void addFinalDiagnosis(Operation operation) {
        describe(operation, "Ordens de Servico", "Registrar diagnostico final",
                "Registra o diagnostico final e a previsao de entrega. No Insomnia havia o campo notes; no contrato atual o campo correto e observacao.");
        request(operation, "Exemplo derivado do Insomnia e ajustado ao contrato atual da API.",
                "insomnia-ajustado", "Diagnostico final", """
                {
                  "descricaoDiagnosticoFinal": "Teste diagnostico final",
                  "dataHoraPrevista": "2026-05-01T10:30:00",
                  "observacao": "Observacao"
                }
                """);
        response(operation, "200", "Diagnostico registrado.", "ordemServico", "OS em diagnostico", serviceOrderResponse("AGUARDANDO_APROVACAO"));
        protectedErrors(operation, true, true);
    }

    private static void calculateServiceOrderValue(Operation operation) {
        describe(operation, "Ordens de Servico", "Calcular valor dos servicos da OS",
                "Soma os servicos associados a ordem de servico.");
        response(operation, "200", "Valor calculado.", "valor", "Valor dos servicos", """
                {
                  "idOrdemServico": "54e94616-70ad-4ce7-b6f7-41c6747d802e",
                  "valorTotal": 250.00
                }
                """);
        protectedErrors(operation, true, false);
    }

    private static void calculateEstimatedTime(Operation operation) {
        describe(operation, "Ordens de Servico", "Calcular tempo estimado da OS",
                "Calcula o tempo estimado total com base nos servicos adicionados.");
        response(operation, "200", "Tempo estimado calculado.", "tempoEstimado", "Tempo estimado", """
                {
                  "idOrdemServico": "54e94616-70ad-4ce7-b6f7-41c6747d802e",
                  "tempoEstimadoTotalMinutos": 120,
                  "dataHoraPrevistaSugerida": "2026-05-01T10:30:00"
                }
                """);
        protectedErrors(operation, true, false);
    }

    private static void calculateAverageExecutionTime(Operation operation) {
        describe(operation, "Ordens de Servico", "Calcular tempo medio de execucao dos servicos",
                "Calcula media de execucao dos servicos associados a OS, quando ha historico suficiente.");
        response(operation, "200", "Tempo medio calculado.", "tempoMedio", "Tempo medio", """
                {
                  "idOrdemServico": "54e94616-70ad-4ce7-b6f7-41c6747d802e",
                  "servicosConcluidos": 3,
                  "servicosPendentes": 1,
                  "servicosEmExecucao": 1,
                  "servicosCancelados": 0,
                  "servicosAguardandoPecasInsumos": 0,
                  "tempoMedioExecucaoMinutos": 95
                }
                """);
        protectedErrors(operation, true, false);
    }

    private static void cancelServiceOrder(Operation operation) {
        describe(operation, "Ordens de Servico", "Cancelar ordem de servico",
                "Cancela uma OS quando a transicao de status for permitida.");
        response(operation, "200", "Ordem de servico cancelada.", "ordemServico", "OS cancelada", serviceOrderResponse("CANCELADA"));
        protectedErrors(operation, true, true);
    }

    private static void startServiceOrder(Operation operation) {
        describe(operation, "Ordens de Servico", "Iniciar execucao da ordem de servico",
                "Altera a OS para execucao apos aprovacao do orcamento.");
        response(operation, "200", "Execucao iniciada.", "ordemServico", "OS em execucao", serviceOrderResponse("EM_EXECUCAO"));
        protectedErrors(operation, true, true);
    }

    private static void completeServiceOrder(Operation operation) {
        describe(operation, "Ordens de Servico", "Concluir ordem de servico",
                "Marca a OS como finalizada apos conclusao dos servicos.");
        response(operation, "200", "Ordem de servico concluida.", "ordemServico", "OS finalizada", serviceOrderResponse("FINALIZADA"));
        protectedErrors(operation, true, true);
    }

    private static void deliverServiceOrder(Operation operation) {
        describe(operation, "Ordens de Servico", "Entregar ordem de servico",
                "Marca a OS como entregue ao cliente.");
        response(operation, "200", "Ordem de servico entregue.", "ordemServico", "OS entregue", serviceOrderResponse("ENTREGUE"));
        protectedErrors(operation, true, true);
    }

    private static void createServiceOrderItem(Operation operation) {
        describe(operation, "Itens da Ordem de Servico", "Adicionar servico na OS",
                "Adiciona um servico do catalogo dentro da ordem de servico.");
        request(operation, "Exemplo baseado na collection Insomnia_2026-04-30.yaml.",
                "insomnia", "Servico dentro da OS", """
                {
                  "idOrdemServico": "54e94616-70ad-4ce7-b6f7-41c6747d802e",
                  "idServico": "f459a647-c094-4702-92f3-cf224105707a",
                  "valor": 250,
                  "opcional": false
                }
                """);
        response(operation, "200", "Item de servico adicionado.", "itemServico", "Item criado", serviceOrderItemResponse("PENDENTE"));
        protectedErrors(operation, true, true);
    }

    private static void deleteServiceOrderItem(Operation operation) {
        describe(operation, "Itens da Ordem de Servico", "Remover servico da OS",
                "Remove um item de servico da OS quando a regra de negocio permitir.");
        noContent(operation, "204", "Item de servico removido.");
        protectedErrors(operation, true, true);
    }

    private static void startServiceOrderItem(Operation operation) {
        describe(operation, "Itens da Ordem de Servico", "Iniciar item de servico",
                "Altera o item de servico para em trabalho.");
        response(operation, "200", "Item iniciado.", "itemServico", "Item iniciado", serviceOrderItemResponse("EM_EXECUCAO"));
        protectedErrors(operation, true, true);
    }

    private static void cancelServiceOrderItem(Operation operation) {
        describe(operation, "Itens da Ordem de Servico", "Cancelar item de servico",
                "Cancela um item de servico quando a transicao de status for permitida.");
        response(operation, "200", "Item cancelado.", "itemServico", "Item cancelado", serviceOrderItemResponse("CANCELADO"));
        protectedErrors(operation, true, true);
    }

    private static void completeServiceOrderItem(Operation operation) {
        describe(operation, "Itens da Ordem de Servico", "Concluir item de servico",
                "Conclui a execucao de um item de servico.");
        response(operation, "200", "Item concluido.", "itemServico", "Item concluido", serviceOrderItemResponse("FINALIZADO"));
        protectedErrors(operation, true, true);
    }

    private static void createServiceOrderItemSupply(Operation operation) {
        describe(operation, "Insumos da Ordem de Servico", "Adicionar insumo ao item da OS",
                "Vincula uma peca ou insumo de estoque a um item de servico.");
        request(operation, "Exemplo baseado na collection Insomnia_2026-04-30.yaml.",
                "insomnia", "Insumo usado", """
                {
                  "idItemEstoque": "11111111-1111-1111-1111-111111111111",
                  "quantidadeUsada": 20
                }
                """);
        response(operation, "201", "Insumo adicionado.", "insumo", "Insumo criado", serviceOrderItemSupplyResponse());
        protectedErrors(operation, true, true);
    }

    private static void updateServiceOrderItemSupply(Operation operation) {
        describe(operation, "Insumos da Ordem de Servico", "Atualizar insumo do item da OS",
                "Atualiza o item de estoque e quantidade consumida em um item de servico.");
        request(operation, "Dados completos do insumo para atualizacao.",
                "insumo", "Insumo atualizado", """
                {
                  "idItemEstoque": "11111111-1111-1111-1111-111111111111",
                  "quantidadeUsada": 10
                }
                """);
        response(operation, "200", "Insumo atualizado.", "insumo", "Insumo atualizado", serviceOrderItemSupplyResponse());
        protectedErrors(operation, true, true);
    }

    private static void listServiceOrderItemSupplies(Operation operation) {
        describe(operation, "Insumos da Ordem de Servico", "Listar insumos do item da OS",
                "Lista pecas e insumos consumidos por um item de servico.");
        response(operation, "200", "Insumos do item.", "insumos", "Lista de insumos", "[" + serviceOrderItemSupplyResponse() + "]");
        protectedErrors(operation, true, false);
    }

    private static void deleteServiceOrderItemSupply(Operation operation) {
        describe(operation, "Insumos da Ordem de Servico", "Remover insumo do item da OS",
                "Remove um insumo vinculado ao item de servico.");
        noContent(operation, "204", "Insumo removido.");
        protectedErrors(operation, true, true);
    }

    private static void createBudget(Operation operation) {
        describe(operation, "Orcamentos", "Criar orcamento da OS",
                "Cria um orcamento para uma ordem de servico. O valorProposto pode ser informado ou calculado pelos servicos.");
        request(operation, "Exemplo do Insomnia usa corpo vazio para permitir calculo automatico.",
                "insomnia", "Orcamento calculado automaticamente", """
                {
                }
                """);
        request(operation, "Alternativa quando a oficina deseja informar um valor proposto manual.",
                "valor-manual", "Orcamento com valor proposto", """
                {
                  "valorProposto": 250.00
                }
                """);
        response(operation, "201", "Orcamento criado.", "orcamento", "Orcamento criado", budgetResponse("RASCUNHO"));
        protectedErrors(operation, true, true);
    }

    private static void requestBudgetApproval(Operation operation) {
        describe(operation, "Orcamentos", "Solicitar aprovacao do orcamento",
                "Envia o orcamento para aprovacao do cliente e dispara os links publicos de aprovar/rejeitar.");
        response(operation, "200", "Aprovacao solicitada.", "orcamento", "Orcamento enviado", budgetResponse("ENVIADO"));
        protectedErrors(operation, true, true);
    }

    private static void approveBudget(Operation operation) {
        describe(operation, "Orcamentos", "Aprovar orcamento pelo admin",
                "Aprova manualmente um orcamento pelo endpoint administrativo.");
        response(operation, "200", "Orcamento aprovado.", "orcamento", "Orcamento aprovado", budgetResponse("APROVADO"));
        protectedErrors(operation, true, true);
    }

    private static void rejectBudget(Operation operation) {
        describe(operation, "Orcamentos", "Rejeitar orcamento pelo admin",
                "Rejeita manualmente um orcamento pelo endpoint administrativo.");
        response(operation, "200", "Orcamento rejeitado.", "orcamento", "Orcamento rejeitado", budgetResponse("REJEITADO"));
        protectedErrors(operation, true, true);
    }

    private static void approveBudgetByEmail(Operation operation) {
        describe(operation, "Orcamentos", "Aprovar orcamento por link publico",
                "Endpoint publico usado pelo link enviado ao cliente por email.");
        textResponse(operation, "200", "Orcamento aprovado pelo cliente.", "Orcamento aprovado com sucesso.");
        publicErrors(operation, true, true);
    }

    private static void rejectBudgetByEmail(Operation operation) {
        describe(operation, "Orcamentos", "Rejeitar orcamento por link publico",
                "Endpoint publico usado pelo link enviado ao cliente por email.");
        textResponse(operation, "200", "Orcamento rejeitado pelo cliente.", "Orcamento rejeitado com sucesso.");
        publicErrors(operation, true, true);
    }

    private static void trackServiceOrder(Operation operation) {
        describe(operation, "Acompanhamento Publico", "Acompanhar ordem de servico",
                "Consulta publica para o cliente acompanhar status, orcamentos e servicos da OS. No Insomnia o caminho estava sem /acompanhamento; o contrato atual usa este sufixo.");
        response(operation, "200", "Acompanhamento da OS.", "acompanhamento", "Acompanhamento publico", trackingResponse());
        publicErrors(operation, true, false);
    }

    private static void defaultOperation(Operation operation, String path) {
        if (operation.getTags() == null || operation.getTags().isEmpty()) {
            operation.setTags(List.of(resolveTag(path)));
        }
    }

    private static void describe(Operation operation, String tag, String summary, String description) {
        operation.setTags(List.of(tag));
        operation.setSummary(summary);
        operation.setDescription(description);
    }

    private static void describePathParameters(Operation operation) {
        if (operation.getParameters() == null) {
            return;
        }

        for (Parameter parameter : operation.getParameters()) {
            if (!"path".equals(parameter.getIn())) {
                continue;
            }

            switch (parameter.getName()) {
                case "id" -> {
                    parameter.setDescription("Identificador UUID do recurso.");
                    parameter.setExample(SERVICE_ORDER_ID);
                }
                case "serviceOrderId" -> {
                    parameter.setDescription("Identificador UUID da ordem de servico.");
                    parameter.setExample(SERVICE_ORDER_ID);
                }
                case "serviceOrderItemId" -> {
                    parameter.setDescription("Identificador UUID do item de servico da OS.");
                    parameter.setExample(SERVICE_ORDER_ITEM_ID);
                }
                case "itemId" -> {
                    parameter.setDescription("Identificador UUID do item de estoque.");
                    parameter.setExample(INVENTORY_ITEM_ID);
                }
                default -> parameter.setDescription("Identificador UUID usado no recurso.");
            }
        }
    }

    private static void request(Operation operation, String description, String key, String summary, String json) {
        RequestBody requestBody = operation.getRequestBody();
        if (requestBody == null) {
            requestBody = new RequestBody();
            operation.setRequestBody(requestBody);
        }

        requestBody.setDescription(description);
        requestBody.setRequired(true);

        MediaType mediaType = mediaType(requestBody);
        mediaType.addExamples(key, new Example()
                .summary(summary)
                .value(jsonValue(json)));
    }

    private static void response(Operation operation, String status, String description, String key, String summary, String json) {
        ApiResponse apiResponse = response(operation, status, description);
        MediaType mediaType = mediaType(apiResponse, APPLICATION_JSON);
        mediaType.addExamples(key, new Example()
                .summary(summary)
                .value(jsonValue(json)));
    }

    private static ApiResponse response(Operation operation, String status, String description) {
        ApiResponses responses = operation.getResponses();
        if (responses == null) {
            responses = new ApiResponses();
            operation.setResponses(responses);
        }

        ApiResponse apiResponse = responses.get(status);
        if (apiResponse == null) {
            apiResponse = new ApiResponse();
            responses.addApiResponse(status, apiResponse);
        }
        apiResponse.setDescription(description);
        return apiResponse;
    }

    private static void noContent(Operation operation, String status, String description) {
        response(operation, status, description).setContent(null);
    }

    private static void textResponse(Operation operation, String status, String description, String text) {
        ApiResponse apiResponse = response(operation, status, description);
        MediaType mediaType = mediaType(apiResponse, TEXT_PLAIN);
        mediaType.addExamples("texto", new Example()
                .summary("Mensagem retornada")
                .value(text));
    }

    private static void error(Operation operation, String status, String description, String json) {
        ApiResponse apiResponse = response(operation, status, description);
        MediaType mediaType = mediaType(apiResponse, APPLICATION_JSON);
        if (mediaType.getSchema() == null) {
            mediaType.setSchema(new Schema<>().$ref("#/components/schemas/ErrorResponse"));
        }
        mediaType.addExamples("erro-" + status, new Example()
                .summary(description)
                .value(jsonValue(json)));
    }

    private static void protectedErrors(Operation operation, boolean notFound, boolean businessValidation) {
        error(operation, "400", "Requisicao invalida ou erro de validacao.", badRequestExample());
        error(operation, "401", "Token JWT ausente, invalido ou expirado.", unauthorizedExample());

        if (notFound) {
            error(operation, "404", "Recurso nao encontrado.", notFoundExample());
        }

        if (businessValidation) {
            error(operation, "422", "Regra de negocio violada.", businessValidationExample());
        }

        error(operation, "500", "Erro interno inesperado.", internalErrorExample());
    }

    private static void operationalErrors(Operation operation, boolean notFound) {
        error(operation, "400", "Requisicao invalida ou erro de validacao.", badRequestExample());

        if (notFound) {
            error(operation, "404", "Recurso nao encontrado.", notFoundExample());
            error(operation, "422", "Regra de negocio violada.", businessValidationExample());
        }

        error(operation, "500", "Erro interno inesperado.", internalErrorExample());
    }

    private static void publicErrors(Operation operation, boolean notFound, boolean businessValidation) {
        error(operation, "400", "Parametro invalido ou requisicao mal formada.", badRequestExample());

        if (notFound) {
            error(operation, "404", "Recurso nao encontrado.", notFoundExample());
        }

        if (businessValidation) {
            error(operation, "422", "Regra de negocio violada.", businessValidationExample());
        }

        error(operation, "500", "Erro interno inesperado.", internalErrorExample());
    }

    private static MediaType mediaType(RequestBody requestBody) {
        Content content = requestBody.getContent();
        if (content == null) {
            content = new Content();
            requestBody.setContent(content);
        }

        MediaType mediaType = content.get(APPLICATION_JSON);
        if (mediaType == null) {
            mediaType = new MediaType();
            content.addMediaType(APPLICATION_JSON, mediaType);
        }
        return mediaType;
    }

    private static MediaType mediaType(ApiResponse response, String contentType) {
        Content content = response.getContent();
        if (content == null) {
            content = new Content();
            response.setContent(content);
        }

        MediaType mediaType = content.get(contentType);
        if (mediaType == null) {
            mediaType = new MediaType();
            content.addMediaType(contentType, mediaType);
        }
        return mediaType;
    }

    private static Object jsonValue(String json) {
        try {
            return Json.mapper().readValue(json, Object.class);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Exemplo OpenAPI com JSON invalido.", ex);
        }
    }

    private static String resolveTag(String path) {
        if (path.startsWith("/api/public/auth") || path.startsWith("/api/admin/session")) {
            return "Autenticacao e Sessao";
        }
        if (path.startsWith("/api/public/health")) {
            return "Health";
        }
        if (path.startsWith("/api/admin/clientes")) {
            return "Clientes";
        }
        if (path.startsWith("/api/admin/veiculos")) {
            return "Veiculos";
        }
        if (path.startsWith("/api/admin/servicos")) {
            return "Servicos Automotivos";
        }
        if (path.startsWith("/api/admin/itens")) {
            return "Estoque - Itens";
        }
        if (path.startsWith("/api/admin/estoque")) {
            return "Estoque - Movimentacoes";
        }
        if (path.contains("orcamentos-ordem-servico") || path.endsWith("/orcamentos")) {
            return "Orcamentos";
        }
        if (path.contains("/insumos")) {
            return "Insumos da Ordem de Servico";
        }
        if (path.startsWith("/api/admin/itens-ordem-servico")) {
            return "Itens da Ordem de Servico";
        }
        if (path.startsWith("/api/public/ordens-servico")) {
            return "Acompanhamento Publico";
        }
        if (path.startsWith("/api/admin/ordens-servico")) {
            return "Ordens de Servico";
        }
        return "API";
    }

    private static String customerRequest() {
        return """
                {
                  "nome": "Joao da Silva",
                  "tipoDocumento": "PESSOA_FISICA",
                  "documento": "12345678901",
                  "email": "joao.silva@email.com",
                  "telefone": "11999999999",
                  "endereco": "Rua das Oficinas, 100",
                  "ativo": true
                }
                """;
    }

    private static String customerResponse() {
        return """
                {
                  "id": "1ed09259-0f4f-4fd8-867c-a13d4d2fda4e",
                  "nome": "Joao da Silva",
                  "tipoDocumento": "PESSOA_FISICA",
                  "documento": "12345678901",
                  "email": "joao.silva@email.com",
                  "telefone": "11999999999",
                  "endereco": "Rua das Oficinas, 100",
                  "ativo": true,
                  "criadoEm": "2026-04-28T09:00:00",
                  "atualizadoEm": "2026-04-28T09:00:00"
                }
                """;
    }

    private static String vehicleRequest() {
        return """
                {
                  "placa": "ABC1D23",
                  "marca": "Toyota",
                  "modelo": "Corolla",
                  "ano": 2022,
                  "idCliente": "1ed09259-0f4f-4fd8-867c-a13d4d2fda4e"
                }
                """;
    }

    private static String vehicleResponse() {
        return """
                {
                  "id": "74a3eaac-979c-4f93-a926-2a3595047db9",
                  "placa": "ABC1D23",
                  "marca": "Toyota",
                  "modelo": "Corolla",
                  "ano": 2022,
                  "idCliente": "1ed09259-0f4f-4fd8-867c-a13d4d2fda4e",
                  "criadoEm": "2026-04-28T09:15:00",
                  "atualizadoEm": "2026-04-28T09:15:00"
                }
                """;
    }

    private static String automotiveServiceRequest() {
        return """
                {
                  "codigo": "REV-001",
                  "nome": "Revisao basica",
                  "descricao": "Troca de oleo, filtros e checklist preventivo",
                  "tipoServico": "REVISAO",
                  "valorBase": 250.00,
                  "tempoEstimadoMinutos": 120
                }
                """;
    }

    private static String automotiveServiceResponse() {
        return """
                {
                  "id": "f459a647-c094-4702-92f3-cf224105707a",
                  "code": "REV-001",
                  "name": "Revisao basica",
                  "description": "Troca de oleo, filtros e checklist preventivo",
                  "serviceType": "REVISAO",
                  "baseValue": 250.00,
                  "estimatedTimeMinutes": 120,
                  "active": true,
                  "createdAt": "2026-04-28T09:20:00",
                  "updatedAt": "2026-04-28T09:20:00"
                }
                """;
    }

    private static String inventoryItemRequest() {
        return """
                {
                  "codigo": "OLEO-5W30",
                  "nome": "Oleo sintetico 5W30",
                  "descricao": "Oleo de motor sintetico",
                  "tipoItem": "LUBRIFICANTE",
                  "unidadeMedida": "LITRO",
                  "custoUnitario": 38.50,
                  "precoVenda": 55.00,
                  "quantidadeEstoque": 40,
                  "estoqueMinimo": 10,
                  "marca": "NumberOil",
                  "veiculoAplicavel": "Motores flex",
                  "ativo": true
                }
                """;
    }

    private static String inventoryItemResponse() {
        return """
                {
                  "id": "11111111-1111-1111-1111-111111111111",
                  "codigo": "OLEO-5W30",
                  "nome": "Oleo sintetico 5W30",
                  "descricao": "Oleo de motor sintetico",
                  "tipoItem": "LUBRIFICANTE",
                  "unidadeMedida": "LITRO",
                  "custoUnitario": 38.50,
                  "precoVenda": 55.00,
                  "quantidadeEstoque": 40,
                  "estoqueMinimo": 10,
                  "marca": "NumberOil",
                  "veiculoAplicavel": "Motores flex",
                  "ativo": true,
                  "criadoEm": "2026-04-28T09:25:00",
                  "atualizadoEm": "2026-04-28T09:25:00"
                }
                """;
    }

    private static String inventoryEntryRequest() {
        return """
                {
                  "idItemEstoque": "11111111-1111-1111-1111-111111111111",
                  "quantidade": 20,
                  "origemMovimentacao": "COMPRA",
                  "referenciaOrigemId": "33333333-3333-3333-3333-333333333333",
                  "observacao": "Entrada por compra de reposicao",
                  "usuarioResponsavelId": "22222222-2222-2222-2222-222222222222"
                }
                """;
    }

    private static String inventoryWithdrawalRequest() {
        return """
                {
                  "idItemEstoque": "11111111-1111-1111-1111-111111111111",
                  "quantidade": 2,
                  "origemMovimentacao": "ORDEM_SERVICO",
                  "referenciaOrigemId": "54e94616-70ad-4ce7-b6f7-41c6747d802e",
                  "observacao": "Baixa por consumo na ordem de servico",
                  "usuarioResponsavelId": "22222222-2222-2222-2222-222222222222"
                }
                """;
    }

    private static String inventoryAdjustmentRequest() {
        return """
                {
                  "idItemEstoque": "11111111-1111-1111-1111-111111111111",
                  "quantidadeFinal": 35,
                  "origemMovimentacao": "MANUAL",
                  "referenciaOrigemId": null,
                  "observacao": "Ajuste apos conferencia fisica do estoque",
                  "usuarioResponsavelId": "22222222-2222-2222-2222-222222222222"
                }
                """;
    }

    private static String inventoryMovementResponse(String type) {
        return """
                {
                  "id": "44444444-4444-4444-4444-444444444444",
                  "inventoryItemId": "11111111-1111-1111-1111-111111111111",
                  "tipoMovimentacao": "%s",
                  "origemMovimentacao": "COMPRA",
                  "referenciaOrigemId": "33333333-3333-3333-3333-333333333333",
                  "quantidadeAntes": 40,
                  "quantidadeDepois": 60,
                  "observacao": "Movimentacao registrada",
                  "usuarioResponsavelId": "22222222-2222-2222-2222-222222222222",
                  "createdAt": "2026-04-28T09:30:00"
                }
                """.formatted(type);
    }

    private static String serviceOrderResponse(String status) {
        return """
                {
                  "id": "54e94616-70ad-4ce7-b6f7-41c6747d802e",
                  "descricaoInicial": "Teste ordem",
                  "descricaoDiagnostico": "Teste",
                  "descricaoDiagnosticoFinal": "Teste diagnostico final",
                  "observacao": "Cliente relatou barulho ao frear",
                  "cliente": {
                    "id": "1ed09259-0f4f-4fd8-867c-a13d4d2fda4e",
                    "nome": "Joao da Silva",
                    "tipoDocumento": "PESSOA_FISICA",
                    "documento": "12345678901",
                    "email": "joao.silva@email.com",
                    "telefone": "11999999999",
                    "endereco": "Rua das Oficinas, 100",
                    "ativo": true
                  },
                  "veiculo": {
                    "id": "74a3eaac-979c-4f93-a926-2a3595047db9",
                    "placa": "ABC1D23",
                    "marca": "Toyota",
                    "modelo": "Corolla",
                    "ano": 2022,
                    "idCliente": "1ed09259-0f4f-4fd8-867c-a13d4d2fda4e",
                    "created_at": "2026-04-28T09:15:00",
                    "updated_at": "2026-04-28T09:15:00"
                  },
                  "itensServico": [],
                  "orcamentos": [],
                  "status": "%s",
                  "dataHoraEntrada": "2026-04-28T10:30:00",
                  "dataHoraPrevista": "2026-05-01T10:30:00",
                  "dataHoraEntrega": null,
                  "created_at": "2026-04-28T10:30:00",
                  "updated_at": "2026-04-28T10:30:00"
                }
                """.formatted(status);
    }

    private static String serviceOrderItemResponse(String status) {
        return """
                {
                  "id": "e7b2c7b1-1c11-4832-a699-6e738608f61e",
                  "idOrdemServico": "54e94616-70ad-4ce7-b6f7-41c6747d802e",
                  "servicoAutomotivo": {
                    "id": "f459a647-c094-4702-92f3-cf224105707a",
                    "codigo": "REV-001",
                    "nome": "Revisao basica",
                    "descricao": "Troca de oleo, filtros e checklist preventivo",
                    "tipoServico": "REVISAO",
                    "valorBase": 250.00,
                    "tempoEstimadoMinutos": 120,
                    "ativo": true
                  },
                  "valor": 250.00,
                  "status": "%s",
                  "opcional": false,
                  "insumos": [],
                  "dataHoraInicio": null,
                  "dataHoraFim": null,
                  "created_at": "2026-04-28T10:45:00",
                  "updated_at": "2026-04-28T10:45:00"
                }
                """.formatted(status);
    }

    private static String serviceOrderItemSupplyResponse() {
        return """
                {
                  "id": "55555555-5555-5555-5555-555555555555",
                  "idOrdemServicoItem": "e7b2c7b1-1c11-4832-a699-6e738608f61e",
                  "itemEstoque": {
                    "id": "11111111-1111-1111-1111-111111111111",
                    "codigo": "OLEO-5W30",
                    "nome": "Oleo sintetico 5W30",
                    "descricao": "Oleo de motor sintetico",
                    "tipoItem": "LUBRIFICANTE",
                    "unidadeMedida": "LITRO"
                  },
                  "quantidadeUsada": 20
                }
                """;
    }

    private static String budgetResponse(String status) {
        return """
                {
                  "id": "b1fb7c09-d680-4227-81c5-6b38dbaa88b9",
                  "idOrdemServico": "54e94616-70ad-4ce7-b6f7-41c6747d802e",
                  "valorProposto": 250.00,
                  "valorAprovado": null,
                  "status": "%s",
                  "enviadoEm": null,
                  "aprovadoEm": null,
                  "created_at": "2026-04-28T11:00:00",
                  "updated_at": "2026-04-28T11:00:00"
                }
                """.formatted(status);
    }

    private static String trackingResponse() {
        return """
                {
                  "id": "54e94616-70ad-4ce7-b6f7-41c6747d802e",
                  "descricaoInicial": "Teste ordem",
                  "descricaoDiagnosticoFinal": "Teste diagnostico final",
                  "dataHoraEntrada": "2026-04-28T10:30:00",
                  "dataHoraPrevista": "2026-05-01T10:30:00",
                  "dataHoraEntrega": null,
                  "veiculo": {
                    "placa": "ABC1D23",
                    "marca": "Toyota",
                    "modelo": "Corolla",
                    "ano": 2022
                  },
                  "status": "AGUARDANDO_APROVACAO",
                  "orcamento": {
                    "valorProposto": 250.00,
                    "valorAprovado": null,
                    "status": "ENVIADO",
                    "enviadoEm": "2026-04-28T11:10:00",
                    "aprovadoEm": null
                  },
                  "itensServico": [
                    {
                      "id": "e7b2c7b1-1c11-4832-a699-6e738608f61e",
                      "nomeServico": "Revisao basica",
                      "tipoServico": "REVISAO",
                      "status": "PENDENTE",
                      "opcional": false,
                      "dataHoraInicio": null,
                      "dataHoraFim": null
                    }
                  ]
                }
                """;
    }

    private static String badRequestExample() {
        return """
                {
                  "status": 400,
                  "message": "Erro de validacao",
                  "errors": [
                    "nome: Nome e obrigatorio"
                  ]
                }
                """;
    }

    private static String unauthorizedExample() {
        return """
                {
                  "status": 401,
                  "message": "Token JWT ausente, invalido ou expirado",
                  "errors": []
                }
                """;
    }

    private static String notFoundExample() {
        return """
                {
                  "status": 404,
                  "message": "Recurso nao encontrado",
                  "errors": []
                }
                """;
    }

    private static String businessValidationExample() {
        return """
                {
                  "status": 422,
                  "message": "Transicao de status invalida para a ordem de servico",
                  "errors": []
                }
                """;
    }

    private static String internalErrorExample() {
        return """
                {
                  "status": 500,
                  "message": "Erro interno, tente novamente mais tarde",
                  "errors": []
                }
                """;
    }
}
