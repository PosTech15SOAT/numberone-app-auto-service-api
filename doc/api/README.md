# API, OpenAPI e Insomnia

## Documentacao oficial

A documentacao oficial da API e gerada pela propria aplicacao com Springdoc
OpenAPI.

Com a aplicacao em execucao local:

```text
Swagger UI: http://localhost:8080/swagger-ui.html
OpenAPI JSON: http://localhost:8080/v3/api-docs
```

Para iniciar a aplicacao localmente:

```bash
docker compose up --build
```

Ou, com a infraestrutura local ja iniciada:

```bash
docker compose up -d postgres mailpit
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

## Insomnia

A collection Insomnia usada na apresentacao esta versionada em:

```text
doc/api/numberone-insomnia.yaml
```

Ela foi organizada a partir do OpenAPI, com pastas e requests adicionais para
execucao dos fluxos principais no Insomnia.

O export versionado foi sanitizado:

- `accessToken` vazio;
- host de producao substituido por placeholder;
- IDs de execucao removidos dos ambientes;
- sem JWT real, secrets, senhas, credenciais AWS ou tokens.

Ao atualizar a collection, exporte novamente do Insomnia e sanitize antes de
versionar. Mantenha:

- usar variaveis de ambiente do Insomnia para `base_url`, `accessToken` e
  `correlation_id`;
- manter o Swagger/OpenAPI como fonte oficial do contrato da API.
