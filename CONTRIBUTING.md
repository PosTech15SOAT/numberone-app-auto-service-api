# Contribuindo

## Fluxo de branches

- `main`: codigo candidato a producao.
- `develop`: integracao e homologacao.
- `feature/<descricao>`: funcionalidades.
- `fix/<descricao>`: correcoes.
- `docs/<descricao>`: documentacao.

Todo desenvolvimento deve partir de `develop` e retornar por Pull Request. A promocao para producao ocorre por Pull Request de `develop` para `main`.

## Regras de Pull Request

- Nao realizar commits diretos em `main` ou `develop`.
- Exigir ao menos uma revisao de outro integrante.
- Aguardar o workflow `CI` concluir com sucesso.
- Manter o PR pequeno e com objetivo unico quando possivel.
- Atualizar testes e documentacao afetados.

## Validacao local

```bash
./mvnw clean test
./mvnw clean verify
docker build -t numberone-auto-service-api:local .
```

Os testes de persistencia usam Testcontainers e exigem Docker em execucao.

## Convencao de commits

Use mensagens curtas no formato:

```text
tipo: descricao objetiva
```

Tipos sugeridos: `feat`, `fix`, `test`, `docs`, `refactor`, `chore` e `ci`.
