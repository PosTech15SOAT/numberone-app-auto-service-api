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

## Credenciais locais

Usuario administrativo criado automaticamente:

- usuario: `admin`
- senha: `admin123456`

Endpoint de login:

```text
POST /api/public/auth/login
```

Body:

```json
{
  "username": "admin",
  "password": "admin123456"
}
```

Use o `accessToken` retornado no header:

```text
Authorization: Bearer <token>
```
