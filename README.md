# NumberOne Auto Service API

Aplicacao principal do Tech Challenge Fase 3 para gerenciamento de uma oficina mecanica. Este repositorio foi migrado da solucao desenvolvida nas fases anteriores e sera evoluido para operar em Kubernetes, atras de um API Gateway e integrado a um Lambda Authorizer.

> Estado da migracao: o codigo legado ainda possui autenticacao JWT interna. A substituicao pelo contexto autenticado fornecido pelo API Gateway esta registrada no backlog da Fase 3.

## Stack

- Java 25
- Spring Boot 4.0.5
- Spring Web MVC
- Spring Security
- Spring Data JPA
- Flyway
- PostgreSQL
- Mailpit
- Docker e Docker Compose
- H2 para testes
- SonarQube para analise local de qualidade e seguranca

## Modulos

- `customer`: cadastro de clientes, documento, tipo de documento e validacoes.
- `vehicle`: cadastro de veiculos, placa, marca, modelo, ano e vinculo com cliente.
- `automotiveservice`: catalogo de servicos automotivos, valor base e tempo estimado.
- `inventory`: cadastro de itens de estoque e movimentacoes de entrada, baixa e ajuste.
- `serviceorder`: ordem de servico, diagnostico, orcamento, itens, insumos, status e acompanhamento.
- `shared`: seguranca JWT, tratamento global de erros, Swagger, email e configuracoes comuns.

## Como Rodar com Um Comando

Pre-requisitos:

- Docker instalado
- Docker Compose instalado

Na raiz do projeto:

```bash
./executar-projeto.sh
```

Esse comando executa `docker compose up --build` e sobe a aplicacao, o banco PostgreSQL e o Mailpit.

Se aparecer erro de permissao no Docker, execute com `sudo` ou adicione seu usuario ao grupo `docker`:

```bash
sudo usermod -aG docker $USER
```

Depois faca logout/login ou reinicie o terminal.

## Como Rodar Manualmente com Docker

```bash
docker compose up --build
```

Servicos:

- API: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`
- PostgreSQL: `localhost:5432`
- Mailpit SMTP: `localhost:1025`
- Mailpit Web: `http://localhost:8025`

Para parar:

```bash
docker compose down
```

Para parar e apagar o volume do banco:

```bash
docker compose down -v
```

Mais detalhes em `doc/execucao-local.md`.

## Como Rodar Sem Docker para a Aplicacao

Suba apenas infraestrutura:

```bash
docker compose up -d postgres mailpit
```

Rode a aplicacao localmente:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

O profile `local` carrega `src/main/resources/application-local.properties`, com defaults para banco local, Mailpit e uma identidade ADMIN exclusiva para desenvolvimento.

Sem profile ativo, a aplicacao usa `src/main/resources/application.properties`, que e a configuracao produtiva empacotada na imagem Docker. Nesse modo, valores sensiveis e dependentes do ambiente devem ser informados por variaveis de ambiente.

Alternativa:

```bash
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

Configuracao local padrao:

- database: `numberone`
- username: `admin`
- password: `admin`
- JDBC: `jdbc:postgresql://localhost:5432/numberone`

## Autenticacao e autorizacao

Em producao, o JWT e emitido pela Lambda de autenticacao e validado pelo Lambda Authorizer. O API Gateway remove ou sobrescreve headers enviados pelo cliente e encaminha para esta API apenas o contexto de identidade confiavel. A aplicacao converte esse contexto em um usuario do Spring Security e aplica roles, permissions e propriedade do recurso.

A API nao possui mais endpoint de login, senha administrativa ou validacao propria de JWT.

No profile `local`, a identidade e definida pelas variaveis `LOCAL_AUTHENTICATED_SUBJECT`, `LOCAL_AUTHENTICATED_CUSTOMER_ID`, `LOCAL_AUTHENTICATED_STATUS`, `LOCAL_AUTHENTICATED_ROLES` e `LOCAL_AUTHENTICATED_PERMISSIONS`. Os defaults representam um administrador ativo e permitem usar Swagger sem login. Esse provider nao deve ser habilitado em producao.

## Swagger

Com a aplicacao rodando:

```text
http://localhost:8080/swagger-ui.html
```

Com o profile `local`, o Swagger usa automaticamente a identidade local configurada.

## Endpoints Principais

Publicos:

- `GET /api/public/health`

Autenticados como cliente proprietario ou administrador:

- `GET /api/public/ordens-servico/{id}/acompanhamento`
- `GET /api/public/orcamentos-ordem-servico/{id}/aprovacao/aprovar`
- `GET /api/public/orcamentos-ordem-servico/{id}/aprovacao/rejeitar`

Administrativos:

- `GET /api/admin/session`
- `POST /api/admin/clientes`
- `GET /api/admin/clientes`
- `POST /api/admin/veiculos`
- `GET /api/admin/veiculos`
- `POST /api/admin/servicos`
- `GET /api/admin/servicos`
- `POST /api/admin/itens`
- `GET /api/admin/itens`
- `POST /api/admin/estoque/entrada`
- `POST /api/admin/estoque/baixa`
- `POST /api/admin/estoque/ajuste`
- `POST /api/admin/ordens-servico`
- `GET /api/admin/ordens-servico`
- `POST /api/admin/itens-ordem-servico`
- `POST /api/admin/itens-ordem-servico/{serviceOrderItemId}/insumos`
- `POST /api/admin/ordens-servico/{serviceOrderId}/orcamentos`
- `PATCH /api/admin/orcamentos-ordem-servico/{id}/solicitar-aprovacao`

## Flyway

As migrations ficam em:

```text
src/main/resources/db/migrations
```

O Flyway roda automaticamente na subida da aplicacao e cria/atualiza as tabelas no PostgreSQL.

## Testes

A documentacao completa do teste em [doc/testes/README.md](doc/testes/README.md).

Fluxo resumido:

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

## Analise de Seguranca com SonarQube

A documentacao completa para executar a analise local de qualidade e seguranca com SonarQube esta em [doc/security/README.md](doc/security/README.md).

Fluxo resumido:

```bash
./scripts/sonar-up.sh
```

Depois:

```text
Acessar http://localhost:9000
Login inicial: admin/admin
Criar token em My Account > Security
```

Depois:

```bash
export SONAR_TOKEN=seu_token_aqui
./scripts/sonar-scan.sh
```

Depois:

```bash
./scripts/security-evidence.sh
```

Salve as evidencias em `doc/security/evidencias/` e preencha o relatorio final em `doc/security/relatorio-vulnerabilidades.md`.

## Justificativa do banco de dados relacional e da escolha do PostgreSQL

A escolha por um **banco de dados relacional** neste projeto foi feita para garantir consistencia e confiabilidade no tratamento dos dados de negocio, especialmente porque o dominio possui entidades com relacionamentos claros (como clientes, veiculos, servicos, itens, estoque e ordens de servico). Nesse contexto, o modelo relacional oferece:

- **Integridade referencial nativa** por meio de chaves primarias e estrangeiras, reduzindo risco de inconsistencias entre tabelas.
- **Transacoes ACID**, importantes para operacoes criticas (por exemplo: abertura de ordem, atualizacao de estoque e faturamento), evitando estados parciais em caso de falha.
- **Consultas estruturadas com SQL**, facilitando filtros, agregacoes e relatorios operacionais sem perda de legibilidade.
- **Evolucao controlada do schema**, alinhada ao uso de migrations com Flyway ja adotado no projeto.

Dentro desse contexto, o **PostgreSQL** foi escolhido por combinar robustez, maturidade e excelente integracao com o ecossistema Java/Spring:

- **Confiabilidade e estabilidade em producao**, sendo amplamente utilizado em sistemas corporativos.
- **Aderencia completa ao SQL e recursos avancados** (indices, constraints, views, funcoes e tipos customizados), permitindo crescimento tecnico sem trocar de tecnologia.
- **Otima integracao com Spring Data JPA e Flyway**, simplificando mapeamento de entidades, versionamento de banco e deploy continuo.
- **Bom desempenho para cargas transacionais** e capacidade de escalar verticalmente e horizontalmente conforme a necessidade do projeto.
- **Software livre e comunidade ativa**, reduzindo custo de licenciamento e facilitando suporte de longo prazo.

Em resumo, a combinacao **modelo relacional + PostgreSQL** atende tanto aos requisitos atuais de consistencia e seguranca dos dados quanto a evolucao futura da aplicacao.

## Documentacao do Projeto

- `doc/README.md`: indice geral de documentacao.
- `doc/equipe/modelagem-banco-aprovada.md`: decisoes de modelagem do banco.
- `doc/equipe/documentacao_final_grupo_numbeone.pdf`: documento final do grupo.
- `doc/linguagem_ubiqua/linguagem-ubiqua.md`: linguagem ubiqua do dominio.
- `doc/equipe/*.md`: divisao de tarefas por integrante.
- `doc/modulos/*.md`: documentacao dos modulos de estoque, servicos e ordem de servico.
- `doc/execucao-local.md`: passo a passo de execucao automatica e manual.
- `doc/padroes-java-25.md`: padroes de codigo Java definidos pelo grupo.
- `doc/security/README.md`: execucao do SonarQube e evidencias de seguranca.
- `doc/testes/README.md`: estrategia e evidencias de testes.
- `doc/fase-3/README.md`: plano tecnico, contratos e backlog da Fase 3.
