**# Estratégia de Testes

## Objetivo

A estratégia de testes do projeto `numberone` foi definida para aumentar a confiabilidade da aplicação, reduzir regressões e validar regras de negócio, contratos técnicos e fluxos principais.

O padrão foi aplicado inicialmente no módulo `automotiveservice`, com uma separação clara entre testes unitários, testes de integração e testes E2E/BDD com Cucumber. A estrutura foi pensada para ser replicada nos demais módulos sem misturar responsabilidades entre camadas.

## Escopo atual

O módulo de referência é:

```text
automotiveservice
```

O mesmo padrão pode ser replicado para:

```text
serviceorder
inventory
customer
vehicle
shared, quando fizer sentido
```

## Estrutura de pastas

Estrutura atual validada para `automotiveservice`:

```text
src/test/java/br/com/fiap/numberone/
└── automotiveservice/
    ├── unit/
    │   ├── domain/
    │   ├── application/
    │   └── api/
    ├── integration/
    │   ├── api/
    │   └── infrastructure/
    ├── e2e/
    │   ├── runners/
    │   ├── steps/
    │   └── support/
    └── support/
```

Os arquivos `.feature` do Cucumber ficam em:

```text
src/test/resources/
└── features/
    └── automotiveservice/
```

Responsabilidades por pasta:

- `unit/domain`: entidades e regras puras de domínio.
- `unit/application`: services/use cases, mockando gateways e dependências externas.
- `unit/api`: mappers ou componentes de API com lógica relevante.
- `integration/api`: controller/API, request, response, validações, mapper e exception handler.
- `integration/infrastructure`: gateway, repository, mapper de persistência e banco.
- `integration/flow`: reservado para fluxo integrado mais completo, quando fizer sentido. Ainda não existe no `automotiveservice`.
- `e2e/runners`: runners Cucumber.
- `e2e/steps`: steps Cucumber.
- `e2e/support`: contexto e configuração do Cucumber.
- `support`: factories, builders e helpers reutilizáveis nos testes.

## Tipos de teste

### Testes unitários

Testes unitários rodam isolados, não sobem Spring, não acessam banco e validam o comportamento de uma classe ou regra de negócio específica. Quando a classe depende de gateways, repositories, clients ou outros serviços externos, essas dependências devem ser mockadas.

Exemplos atuais no `automotiveservice`:

```text
AutomotiveServiceTest
AutomotiveServiceServiceTest
AutomotiveServiceApiMapperTest
```

### Testes de integração

Testes de integração validam a colaboração entre componentes ou camadas. Eles podem subir parte do contexto Spring, validar controller/API com MockMvc ou testar persistence/gateway/repository com banco de teste.

Exemplos atuais no `automotiveservice`:

```text
AutomotiveServiceControllerIT
AutoServiceGatewayImplIT
```

### Testes E2E/BDD com Cucumber

Testes E2E/BDD validam fluxos de negócio mais completos usando Gherkin. Eles rodam isolados com profile Maven próprio e não substituem testes unitários nem testes de integração.

No projeto, o Cucumber deve cobrir poucos fluxos principais e evitar duplicar todos os cenários que já são testados nas camadas unitária e de integração.

## Critério de escolha por camada

### Domain

- `domain/entities`: teste unitário obrigatório quando houver regra de negócio.
- `domain/enums`: normalmente não testar diretamente, exceto se tiver método ou regra.
- `domain/exceptions`: não testar diretamente quando forem exceptions simples.

### Application

- `application/services`: teste unitário obrigatório.
- Mockar gateways, repositories, clients, loggers e dependências externas.
- `application/gateways`: não testar interface diretamente; testar a implementação na camada de infrastructure.

### API

- `api/controllers`: teste de integração de API.
- `api/mappers`: teste unitário quando houver lógica ou conversão relevante.
- `api/dto` ou `api/dtos`: não testar diretamente; validar via controller/API.
- `api/exceptions` e handlers: validar via integração de controller/API.

### Infrastructure

- `infrastructure/persistence/gateways`: teste de integração.
- `infrastructure/persistence/repositories`: testar indiretamente via gateway ou diretamente quando houver query customizada.
- `infrastructure/persistence/mappers`: testar via integração de gateway ou por unitário quando houver lógica relevante.
- `infrastructure/persistence/entities`: não testar diretamente se forem JPA entities simples.
- `infrastructure/config`: não testar diretamente se for configuração simples.

### E2E

Usar Cucumber/E2E apenas para fluxos principais. Ele não deve repetir todos os cenários dos testes unitários e integrados.

## Padrão de nomes

```text
*Test.java
-> testes unitários

*IT.java
-> testes de integração

*CucumberTest.java
-> runner Cucumber/E2E
```

Exemplos reais no `automotiveservice`:

```text
AutomotiveServiceTest
AutomotiveServiceServiceTest
AutomotiveServiceApiMapperTest

AutomotiveServiceControllerIT
AutoServiceGatewayImplIT

AutomotiveServiceCucumberTest
```

## Profile de teste

Os testes que sobem Spring usam:

```text
src/test/resources/application-test.properties
```

Esse profile está configurado com:

- datasource H2 em memória em modo PostgreSQL;
- `spring.jpa.hibernate.ddl-auto=create-drop`;
- `spring.jpa.open-in-view=false`;
- `spring.flyway.enabled=false`;
- propriedades JWT de teste;
- usuário administrativo de teste;
- URL local para aprovação de ordem de serviço.

Atualmente, o Cucumber/E2E usa esse profile com H2 em memória. Nos testes de integração, o uso depende do tipo de teste: controller/API usa `WebMvcTest` com MockMvc e dependências mockadas; gateway/persistence do `automotiveservice` usa Testcontainers PostgreSQL diretamente no teste. Uma evolução possível é migrar também os fluxos E2E para Testcontainers PostgreSQL, caso a fidelidade com o banco real se torne mais importante que a simplicidade de execução.

## Como executar

Use `./mvnw` como comando recomendado. `mvn` também funciona quando o Maven estiver instalado localmente.

### Somente unitários

```bash
./mvnw clean test
# ou
mvn clean test
```

Executa `*Test.java`, não executa `*IT.java` e não executa Cucumber.

### Unitários + integração

```bash
./mvnw clean verify
# ou
mvn clean verify
```

Executa unitários e integração. O Cucumber não roda nesse comando.

### Somente integração

```bash
./mvnw clean verify -DskipUnitTests=true -DskipMergedReport=true
# ou
mvn clean verify -DskipUnitTests=true -DskipMergedReport=true
```

Executa somente integração e não executa unitários.

### Cucumber/E2E

```bash
./mvnw clean verify -Pcucumber
# ou
mvn clean verify -Pcucumber
```

Executa somente Cucumber/E2E. Não executa unitários nem a suíte de integração padrão.

## Relatórios gerados

### Relatórios de execução dos testes

```text
target/surefire-reports/unit
target/failsafe-reports/integration
```

Esses relatórios mostram quais testes rodaram, quais passaram ou falharam, mensagens de erro e tempo de execução.

### Relatórios de cobertura JaCoCo

```text
target/site/jacoco-unit/index.html
target/site/jacoco-integration/index.html
target/site/jacoco-merged/index.html
```

- `jacoco-unit`: cobertura gerada pelos testes unitários.
- `jacoco-integration`: cobertura gerada pelos testes de integração.
- `jacoco-merged`: cobertura geral combinando unitários e integração.

### Relatórios Cucumber

```text
target/cucumber-reports/automotiveservice/index.html
target/cucumber-reports/automotiveservice/cucumber.json
```

- HTML: relatório legível dos cenários BDD/E2E.
- JSON: útil para integração com outras ferramentas.

Principais relatórios para apresentação:

```text
target/site/jacoco-merged/index.html
target/cucumber-reports/automotiveservice/index.html
```

Comandos úteis para abrir no Linux:

```bash
xdg-open target/site/jacoco-unit/index.html
xdg-open target/site/jacoco-integration/index.html
xdg-open target/site/jacoco-merged/index.html
xdg-open target/cucumber-reports/automotiveservice/index.html
```

## Cobertura com JaCoCo

A cobertura deve priorizar classes com comportamento relevante: domínio, application services, controllers, mappers com lógica e gateways de persistência.

As exclusões configuradas no `pom.xml` removem da cobrança direta classes sem regra própria:

```text
**/api/dto/**
**/api/dtos/**
**/application/commands/**
**/application/gateways/**
**/domain/enums/**
**/domain/exceptions/**
**/domain/references/**
**/config/**
**/infrastructure/persistence/entities/**
**/infrastructure/config/**
**/automotiveservice/infrastructure/config/**
**/customer/infrastructure/config/**
**/inventory/infrastructure/config/**
**/serviceorder/infrastructure/config/**
**/vehicle/infrastructure/config/**
**/shared/config/**
**/shared/infrastructure/config/**
**/shared/security/infrastructure/config/**
**/shared/api/exception/ErrorResponse.class
**/shared/api/exception/ResourceNotFoundException.class
**/*Application.class
```

Essas exclusões não significam que as classes nunca são executadas. Elas apenas não entram diretamente no percentual cobrado pelo JaCoCo. DTOs, exceptions simples, JPA entities simples e configurações simples devem ser validados indiretamente quando fizer sentido.

Atualmente não há `includes` limitando a cobertura somente ao `automotiveservice`; a configuração é global, com exclusões para categorias sem regra própria.

## Cucumber/E2E

Cucumber é usado como teste BDD/E2E. Ele roda isolado com o profile `cucumber`, não substitui unitários nem integração e deve cobrir poucos fluxos principais.

O fluxo inicial existente em `automotiveservice` valida:

```text
cadastrar serviço automotivo com sucesso
consultar serviço criado por id
```

Arquivos principais:

```text
src/test/resources/features/automotiveservice/automotive_service.feature
src/test/java/br/com/fiap/numberone/automotiveservice/e2e/runners/AutomotiveServiceCucumberTest.java
src/test/java/br/com/fiap/numberone/automotiveservice/e2e/steps/AutomotiveServiceSteps.java
src/test/java/br/com/fiap/numberone/automotiveservice/e2e/support/AutomotiveServiceCucumberContext.java
```

O Cucumber usa `@SpringBootTest` com `RANDOM_PORT`, profile `test` e H2 em memória. O teste de gateway/persistence do `automotiveservice` já usa Testcontainers PostgreSQL. A evolução possível é migrar E2E para Testcontainers PostgreSQL quando for necessário aproximar mais o teste do ambiente real.

## Classes sem teste direto

Normalmente não se testa diretamente:

- DTOs simples;
- exceptions simples;
- interfaces sem implementação;
- JPA entities simples;
- classes de configuração simples;
- getters e setters simples.

Esses itens podem ser cobertos indiretamente por testes de API, aplicação ou infraestrutura.

## Como replicar para outros módulos

1. Criar a estrutura de pastas do módulo em `src/test/java`.
2. Começar por testes unitários de domínio.
3. Criar testes unitários de services de application.
4. Criar testes unitários de mappers relevantes.
5. Criar testes de integração de API/controller.
6. Criar testes de integração de persistence/gateway.
7. Criar Cucumber/E2E apenas para fluxos principais.
8. Atualizar este README quando um novo padrão for adotado.

## Boas práticas

- Usar Given/When/Then.
- Evitar testar detalhe interno sem valor.
- Não subir Spring em teste unitário.
- Não acessar banco em teste unitário.
- Mockar dependências externas em teste unitário.
- Preferir AssertJ para asserções.
- Usar Mockito quando houver dependências a isolar.
- Não duplicar cenários entre unitário, integração e Cucumber.
- Manter testes legíveis e com nomes claros.
- Usar factories e helpers em `support`.

## Pendências/evoluções

- Replicar o padrão para outros módulos quando houver novos fluxos críticos.
- Avaliar novos cenários Cucumber somente para fluxos principais.
- Evoluir Cucumber/E2E para Testcontainers PostgreSQL se a fidelidade com o banco real for necessária.
- Revisar exclusões e relatórios JaCoCo conforme novos módulos entrarem na cobertura.
- Criar `integration/flow` apenas quando houver fluxo integrado estável que agregue valor além de API e infrastructure.**
- O projeto pode incorporar testes mutantes com PIT para medir a efetividade dos testes unitários, principalmente nas regras de domínio e aplicação.
