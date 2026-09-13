# Estrategia de Testes

Esta documentacao descreve o que existe hoje no repositorio e como executar as
suites locais.

## Tipos de teste existentes

- Testes unitarios (`*Test.java`) para dominio, application services, mappers e
  componentes isolados.
- Testes de integracao (`*IT.java`) para controllers/API, seguranca e gateways
  de persistencia.
- Testes de persistencia com Testcontainers PostgreSQL em gateways de
  `automotiveservice` e `inventory`.
- Testes com H2 em memoria para suites Spring que usam o profile `test`.
- Cucumber/E2E para fluxo principal de `automotiveservice`.
- Cobertura JaCoCo para unitarios, integracao e relatorio combinado.

Contagem validada no repositorio:

```text
34 arquivos de teste
11 arquivos *IT.java
1 runner Cucumber
1 arquivo .feature
```

## Comandos

Use `./mvnw` como comando recomendado. `mvn` tambem funciona quando o Maven
estiver instalado localmente.

### Somente unitarios

```bash
./mvnw clean test
```

Executa `*Test.java`, sem `*IT.java` e sem Cucumber.

### Unitarios + integracao

```bash
./mvnw clean verify
```

Executa unitarios e integracao. O Cucumber nao roda nesse comando.

### Somente integracao

```bash
./mvnw clean verify -DskipUnitTests=true -DskipMergedReport=true
```

Executa a suite de integracao configurada pelo Failsafe.

### Cucumber/E2E

```bash
./mvnw clean verify -Pcucumber
```

Executa somente o runner `AutomotiveServiceCucumberTest`.

## Relatorios gerados

```text
target/surefire-reports/unit
target/failsafe-reports/integration
target/site/jacoco-unit/index.html
target/site/jacoco-integration/index.html
target/site/jacoco-merged/index.html
target/cucumber-reports/automotiveservice/index.html
target/cucumber-reports/automotiveservice/cucumber.json
```

O relatorio principal de cobertura para apresentacao e:

```text
target/site/jacoco-merged/index.html
```

Nao ha threshold de cobertura bloqueante configurado no `pom.xml`.

## Cucumber/E2E

Fluxo existente:

```text
cadastrar servico automotivo com sucesso
consultar servico criado por id
```

Arquivos principais:

```text
src/test/resources/features/automotiveservice/automotive_service.feature
src/test/java/br/com/fiap/numberone/automotiveservice/e2e/runners/AutomotiveServiceCucumberTest.java
src/test/java/br/com/fiap/numberone/automotiveservice/e2e/steps/AutomotiveServiceSteps.java
src/test/java/br/com/fiap/numberone/automotiveservice/e2e/support/AutomotiveServiceCucumberContext.java
```

## Profile de teste

Os testes que sobem contexto Spring usam:

```text
src/test/resources/application-test.properties
```

Configuracao relevante:

- datasource H2 em memoria em modo PostgreSQL;
- `spring.jpa.hibernate.ddl-auto=create-drop`;
- `spring.flyway.enabled=false`;
- provider local de identidade com role `ADMIN`;
- metricas StatsD desabilitadas durante testes.

Os testes de gateway/persistencia de `automotiveservice` e `inventory` usam
Testcontainers PostgreSQL diretamente nos testes.

## Cobertura JaCoCo

O `pom.xml` configura:

- relatorio unitario em `target/site/jacoco-unit`;
- relatorio de integracao em `target/site/jacoco-integration`;
- merge em `target/site/jacoco-merged`.

As exclusoes removem da cobranca direta categorias sem regra propria, como DTOs,
exceptions simples, enums, interfaces de gateway, entities JPA simples,
configuracoes e a classe principal da aplicacao.

## Evolucoes futuras

Itens abaixo sao evolucao futura, nao requisito da entrega atual:

- ampliar Cucumber somente para fluxos principais que agreguem cobertura de
  negocio;
- avaliar Testcontainers tambem para E2E quando a fidelidade com PostgreSQL for
  mais importante que a simplicidade;
- avaliar PIT/mutation testing para regras de dominio e application services.
