# Execucao Local do Projeto

Este documento descreve duas formas de subir o projeto: com Docker, recomendada para avaliacao, e manualmente, para desenvolvimento local.

## Opcao 1 - Subida automatica com Docker

Pre-requisitos:

- Docker instalado
- Docker Compose instalado

Na raiz do projeto, execute:

```bash
./executar-projeto.sh
```

Esse comando sobe:

- aplicacao Spring Boot em `http://localhost:8080`
- PostgreSQL em `localhost:5432`
- Mailpit SMTP em `localhost:1025`
- Mailpit Web em `http://localhost:8025`

## Opcao 2 - Subida manual com Docker Compose

Na raiz do projeto, execute:

```bash
docker compose up --build
```

Para parar os containers:

```bash
docker compose down
```

Para parar e apagar o volume do banco:

```bash
docker compose down -v
```

## Opcao 3 - Subida manual sem Docker da aplicacao

Suba apenas banco e Mailpit:

```bash
docker compose up -d postgres mailpit
```

Depois execute a aplicacao localmente:

```bash
./mvnw spring-boot:run
```

Configuracao padrao usada pela aplicacao:

- banco: `jdbc:postgresql://localhost:5432/numberone`
- usuario: `admin`
- senha: `admin`
- Mailpit SMTP: `localhost:1025`

## URLs principais

- API: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`
- Health Check: `http://localhost:8080/api/public/health`
- Mailpit: `http://localhost:8025`

## Identidade local

O profile `local` usa uma identidade ADMIN apenas para desenvolvimento. Nao existe endpoint de login na API principal. Por padrao, Swagger e chamadas locais ja sao executados como `local-admin`.

O contexto pode ser alterado pelas variaveis:

- `LOCAL_AUTHENTICATED_SUBJECT`
- `LOCAL_AUTHENTICATED_CUSTOMER_ID`
- `LOCAL_AUTHENTICATED_STATUS`
- `LOCAL_AUTHENTICATED_ROLES`
- `LOCAL_AUTHENTICATED_PERMISSIONS`

Para simular um cliente, use role `CUSTOMER`, informe o UUID do cliente e as permissions necessarias. Em producao, mantenha `AUTHENTICATED_USER_PROVIDER=gateway`; o provider local nao deve ser habilitado.
