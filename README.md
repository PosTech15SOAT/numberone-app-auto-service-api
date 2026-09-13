# NumberOne Auto Service API

Aplicacao principal Spring Boot do projeto NumberOne para gestao de oficina mecanica no Tech Challenge Fase 3 da FIAP. Este repositorio contem a API de clientes, veiculos, servicos automotivos, estoque e ordens de servico.

A aplicacao funcional esta integrada ao fluxo de producao da solucao NumberOne e opera atras de API Gateway, Lambda Authorizer, VPC Link, NLB interno, EKS e RDS PostgreSQL.

## 📌 Visão Geral

Este servico e a API principal de dominio da oficina. Ele nao realiza login por CPF, nao emite JWT e nao valida JWT de clientes. A autenticacao fica na frente de autenticacao e no API Gateway com Lambda Authorizer; esta API recebe apenas o contexto autenticado confiavel nos headers `X-Authenticated-*`.

Fluxo de producao entregue:

```text
Insomnia/Cliente
-> API Gateway HTTP API
-> Lambda Authorizer
-> VPC Link
-> NLB interno
-> EKS / numberone-app-auto-service-api
-> RDS PostgreSQL
```

Modulos principais:

- `customer`: cadastro e consulta de clientes.
- `vehicle`: cadastro e consulta de veiculos.
- `automotiveservice`: catalogo de servicos automotivos.
- `inventory`: itens e movimentacoes de estoque.
- `serviceorder`: ordens de servico, orcamentos, itens, insumos e acompanhamento.
- `shared`: seguranca, contexto autenticado, correlacao, Swagger, logs, metricas e configuracoes comuns.

## 🏗️ Arquitetura

Na arquitetura integrada, este repositorio e o componente de aplicacao executado no EKS. A entrada publica ocorre pelo API Gateway; o NLB usado por este servico e interno, acessado pelo VPC Link.

```text
Cliente
-> API Gateway
-> Lambda Authorizer
-> VPC Link
-> NLB interno
-> EKS / Auto Service API
-> RDS PostgreSQL
```

O API Gateway e o Lambda Authorizer validam o JWT emitido pela solucao de autenticacao e encaminham para a aplicacao headers como `X-Authenticated-Subject`, `X-Authenticated-Customer-Id`, `X-Authenticated-Status`, `X-Authenticated-Roles` e `X-Authenticated-Permissions`.

TODO: adicionar o diagrama arquitetural final do componente apos a consolidacao da documentacao da Fase 3.

## 🧰 Tecnologias

- Java 25
- Spring Boot 4.0.5
- Spring Web MVC, Security, Data JPA, Validation, Mail e Actuator
- Springdoc OpenAPI/Swagger UI
- Flyway
- PostgreSQL
- Docker e Docker Compose
- Kubernetes, Kustomize, EKS, ECR e NLB interno
- GitHub Actions
- Micrometer, DogStatsD e Datadog Java Agent
- JaCoCo, JUnit, Mockito, MockMvc, Testcontainers, H2 e Cucumber
- SonarQube para analise local de qualidade e seguranca

## 📁 Estrutura do Projeto

```text
.
|-- .github/workflows/        # CI, deploy e validacao de promocao para main
|-- doc/                      # documentacao detalhada
|   |-- api/                  # orientacoes de Swagger/OpenAPI e Insomnia
|   |-- fase-3/               # contratos, backlog tecnico e ADRs
|   |-- security/             # analise estatica e evidencias de seguranca
|   `-- testes/               # estrategia e comandos de teste
|-- k8s/                      # manifests Kubernetes base e overlay production
|-- scripts/                  # scripts auxiliares de SonarQube
|-- src/main/java/            # codigo da aplicacao por modulo de dominio
|-- src/main/resources/       # configuracoes e migrations Flyway
|   `-- db/migrations/
|-- src/test/java/            # testes unitarios, integracao e Cucumber/E2E
|-- Dockerfile
|-- docker-compose.yml
|-- pom.xml
`-- README.md
```

## ✅ Pré-requisitos

Para desenvolvimento local:

- JDK 25
- Docker
- Docker Compose
- Maven Wrapper do projeto (`./mvnw`)

Para deploy em producao:

- GitHub Environment `production` configurado
- credenciais temporarias do AWS Academy
- ECR, EKS e RDS provisionados pelos repositorios de infraestrutura

## ⚙️ Configuração

O profile `local` usa defaults para PostgreSQL local, Mailpit e identidade local `ADMIN` apenas para desenvolvimento.

Principais configuracoes locais:

```text
DB_URL=jdbc:postgresql://localhost:5432/numberone
DB_USERNAME=admin
DB_PASSWORD=admin
LOCAL_AUTHENTICATED_ROLES=ADMIN
```

Em producao, a imagem usa `application.properties` e recebe valores por variaveis de ambiente, ConfigMaps e Secrets Kubernetes. Segredos, JWTs, credenciais e tokens nao devem ser versionados.

## ▶️ Execução Local

Com Docker Compose:

```bash
./executar-projeto.sh
```

Ou manualmente:

```bash
docker compose up --build
```

Servicos locais:

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- PostgreSQL: `localhost:5432`
- Mailpit Web: `http://localhost:8025`

Para rodar a aplicacao fora do container, suba somente as dependencias e inicie o Spring Boot com o profile local:

```bash
docker compose up -d postgres mailpit
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Mais detalhes: [doc/execucao-local.md](doc/execucao-local.md).

## 🧪 Testes

A suite atual possui testes unitarios, testes de integracao, testes de persistencia com Testcontainers PostgreSQL em alguns gateways, Cucumber/E2E para fluxo de `automotiveservice` e cobertura JaCoCo.

Comandos principais:

```bash
./mvnw clean test
./mvnw clean verify
./mvnw clean verify -DskipUnitTests=true -DskipMergedReport=true
./mvnw clean verify -Pcucumber
```

Relatorios gerados:

- testes unitarios: `target/surefire-reports/unit`
- testes de integracao: `target/failsafe-reports/integration`
- cobertura unitaria: `target/site/jacoco-unit/index.html`
- cobertura de integracao: `target/site/jacoco-integration/index.html`
- cobertura combinada: `target/site/jacoco-merged/index.html`
- Cucumber: `target/cucumber-reports/automotiveservice/index.html`

Nao ha threshold de cobertura bloqueante configurado no `pom.xml`.

Documentacao detalhada: [doc/testes/README.md](doc/testes/README.md).

## 🔐 Segurança

A autenticacao e delegada ao API Gateway e Lambda Authorizer. O backend converte os headers `X-Authenticated-*` em contexto autenticado do Spring Security e aplica regras de autorizacao por role, permissao e propriedade contextual do recurso.

Rotas administrativas exigem `ADMIN`. Fluxos publicos autenticados, como acompanhamento e resposta de orcamento, validam permissao e propriedade quando aplicavel.

A analise estatica local de qualidade e seguranca e feita com SonarQube pelos scripts em `scripts/`.

Documentacao detalhada: [doc/security/README.md](doc/security/README.md).

## 🚀 CI/CD

### CI

O workflow [`.github/workflows/ci.yml`](.github/workflows/ci.yml) roda em push e pull request para `develop` e `main`:

- checkout e JDK 25;
- `./mvnw --batch-mode clean verify`;
- build da imagem Docker;
- renderizacao do overlay Kubernetes de producao;
- validacao dos manifests com Kubeconform;
- bloqueio de segredos obsoletos de autenticacao gerenciada pela aplicacao;
- publicacao dos relatorios de teste como artifact.

O workflow [`.github/workflows/branch-flow.yml`](.github/workflows/branch-flow.yml) valida que PRs para `main` tenham origem em `develop`.

### CD

O workflow [`.github/workflows/deploy.yml`](.github/workflows/deploy.yml) executa deploy quando ha push em `main` ou acionamento manual, sempre com `environment: production`:

- roda testes automatizados;
- autentica na AWS Academy;
- busca host e credenciais do RDS via AWS;
- builda e publica imagem no ECR com tag igual ao SHA do commit;
- configura acesso ao EKS;
- cria/atualiza ConfigMap e Secret de runtime;
- aplica o overlay `k8s/overlays/production` no namespace `numberone-production`;
- aguarda rollout, NLB interno e smoke test em `/actuator/health/readiness`.

Fluxo de governanca:

```text
feature/* -> Pull Request -> develop -> Pull Request -> main -> Production
```

As protecoes de `develop` e `main`, Required CI Checks e validacao de promocao para `main` sao centralizadas no repositorio `postech15soat-governance`.

Ambientes:

| Ambiente | Utilização |
|---|---|
| Local | Desenvolvimento |
| Production | AWS Academy |

Nao existe ambiente cloud de homologacao por orientacao do professor.

## ☁️ Deploy

Os manifests ficam em [k8s/](k8s/README.md) e usam Kustomize com base comum e overlay de producao.

Recursos confirmados:

- `Deployment` `numberone-api` com 2 replicas;
- `Service` `LoadBalancer` com NLB interno;
- `ConfigMap` base e ConfigMap runtime criado no workflow;
- `Secret` runtime criado no workflow;
- `HorizontalPodAutoscaler` entre 2 e 5 replicas por CPU/memoria;
- `PodDisruptionBudget` com `minAvailable: 1`;
- startup, readiness e liveness probes em Actuator;
- namespace `numberone-production`;
- imagem publicada no ECR e implantada no EKS.

A estrategia de rollout usa `maxSurge: 0` e `maxUnavailable: 1`. Esta e uma decisao pragmatica do ambiente academico por limitacao de memoria/capacidade do cluster AWS Academy; nao representa necessariamente a estrategia ideal para uma producao corporativa.

## 🔌 APIs

A documentacao oficial da API e o OpenAPI gerado pela propria aplicacao.

Com a aplicacao rodando:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

A collection do Insomnia usada na apresentacao esta versionada em [doc/api/numberone-insomnia.yaml](doc/api/numberone-insomnia.yaml). O export foi sanitizado para nao conter JWT real, secrets, credenciais ou tokens. O Swagger/OpenAPI continua sendo a documentacao oficial da API.

Usuario academico de demonstracao para apresentacao:

```text
CPF: 52998224725
Perfil: ADMIN
Uso: dados deterministicos academicos para demonstracao, sem tratar como dado real de producao
```

## 📊 Observabilidade

Esta aplicacao participa da observabilidade integrada com Datadog produzindo:

- APM/traces via Datadog Java Agent empacotado no Dockerfile e iniciado em `entrypoint.sh`;
- logs estruturados JSON no formato `logstash` para stdout;
- correlacao por `X-Correlation-Id`, armazenado no MDC como `correlation_id` nas rotas `/api/**`;
- preservacao de campos Datadog no MDC, como `dd.trace_id` e `dd.span_id`, quando injetados pelo tracer;
- metricas Micrometer enviadas via DogStatsD (`micrometer-registry-statsd`);
- Actuator health, liveness e readiness;
- metricas customizadas de negocio:
  - `numberone.service_order.created`;
  - `numberone.service_order.stage.duration` com tag `stage` para `diagnostico`, `execucao` e `finalizacao`.

O dashboard final do Datadog e os add-ons compartilhados de observabilidade do cluster pertencem a solucao integrada, especialmente ao repositorio `postech15soat-infra-cloud`.

Debito tecnico registrado: traces de `/actuator/health/**` podem aparecer no APM e gerar ruido; a correcao nao faz parte desta tarefa.

## 🗃️ Banco de Dados

A aplicacao usa PostgreSQL com Flyway. As migrations ficam em:

```text
src/main/resources/db/migrations
```

O Flyway executa automaticamente durante o startup da aplicacao e esse comportamento foi mantido.

Migrations atuais:

- `V1__create_initial_schema.sql`: schema principal da oficina.
- `V2__create_auth_rbac.sql`: tabelas de Auth/RBAC usadas pelo fluxo integrado.
- `V3__seed_default_roles_permissions.sql`: perfis e permissoes padrao.
- `V4__align_application_authorization_contract.sql`: alinhamento do perfil `CUSTOMER` e permissoes esperadas pela API.
- `V5__seed_academic_demo_data.sql`: massa academica minima para demonstracao integrada.
- `V6__promote_academic_demo_user_to_admin.sql`: promove somente o usuario academico CPF `52998224725` para `ADMIN`.

Centralizar ownership/executor das migrations no repositorio `postech15soat-infra-database` e candidato a ADR/evolucao futura, nao uma decisao aceita nesta entrega. As migrations nao foram movidas.

## 📚 Documentação

- [doc/README.md](doc/README.md): indice geral de documentacao.
- [doc/execucao-local.md](doc/execucao-local.md): execucao local.
- [doc/api/README.md](doc/api/README.md): Swagger/OpenAPI e Insomnia.
- [doc/testes/README.md](doc/testes/README.md): testes e evidencias.
- [doc/security/README.md](doc/security/README.md): SonarQube e seguranca.
- [doc/fase-3/README.md](doc/fase-3/README.md): documentacao tecnica da Fase 3.
- [doc/fase-3/authentication-contract.md](doc/fase-3/authentication-contract.md): contrato dos headers autenticados.
- [doc/fase-3/authentication-integration-review.md](doc/fase-3/authentication-integration-review.md): revisao da integracao de autenticacao.
- [doc/fase-3/adr/README.md](doc/fase-3/adr/README.md): ADRs.
- [k8s/README.md](k8s/README.md): manifests e deploy Kubernetes.
- [doc/modulos/ordem-servico.md](doc/modulos/ordem-servico.md): modulo de ordem de servico.
- [doc/modulos/servico-automotivo.md](doc/modulos/servico-automotivo.md): modulo de servicos automotivos.
- [doc/modulos/item-estoque.md](doc/modulos/item-estoque.md): modulo de item de estoque.
- [doc/modulos/movimento-estoque.md](doc/modulos/movimento-estoque.md): modulo de movimento de estoque.
- [doc/linguagem_ubiqua/linguagem-ubiqua.md](doc/linguagem_ubiqua/linguagem-ubiqua.md): linguagem ubiqua.

## 🧠 Decisões Arquiteturais

ADRs existentes:

- [ADR-0001 - Aplicacao principal em repositorio independente](doc/fase-3/adr/0001-aplicacao-em-repositorio-independente.md), status `aceito`.

Decisoes implementadas e documentadas no repositorio:

- autenticacao delegada ao API Gateway/Lambda Authorizer;
- provider local apenas para desenvolvimento;
- Flyway executado no startup nesta entrega;
- deploy unico em `Production` no AWS Academy;
- rollout academico com `maxSurge: 0` e `maxUnavailable: 1` por limite de capacidade.

## ⚠️ Limitações e decisões do ambiente acadêmico

- O ambiente cloud usado e o AWS Academy.
- Nao existe ambiente cloud de homologacao por orientacao do professor.
- O cluster possui restricoes de capacidade/memoria, refletidas em sizing e rollout.
- `maxSurge: 0` e `maxUnavailable: 1` atendem ao contexto academico e nao devem ser lidos como recomendacao generica para producao corporativa.
- O NLB deste servico e interno; a exposicao externa ocorre pelo API Gateway da solucao integrada.

## 🤝 Contribuição

Fluxo esperado:

```text
feature/* -> Pull Request -> develop -> Pull Request -> main
```

Nao fazer push direto para `develop` ou `main`. As regras de protecao de branch e checks obrigatorios sao centralizadas no repositorio `postech15soat-governance`.
