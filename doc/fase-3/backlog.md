# Backlog tecnico da Fase 3

Este arquivo registra o estado tecnico do repositorio da aplicacao principal no
fechamento da Fase 3. Itens concluidos abaixo refletem a implementacao atual; os
itens pendentes sao debitos ou consolidacoes documentais.

## Concluido

- [x] Repositorio da aplicacao principal separado.
- [x] CI com build, testes e validacao de manifests Kubernetes.
- [x] Provider local de identidade para desenvolvimento.
- [x] Provider de identidade baseado nos headers confiaveis do API Gateway.
- [x] Remocao do login e da emissao/validacao propria de JWT da API principal.
- [x] Autorizacao por roles, permissoes e propriedade contextual.
- [x] Testes de autenticacao e autorizacao com filtros ativos.
- [x] OpenAPI/Swagger atualizado para o fluxo com autenticacao externa.
- [x] Dockerfile com Datadog Java Agent.
- [x] Logs estruturados JSON.
- [x] `correlation_id` via `X-Correlation-Id` nas rotas `/api/**`.
- [x] Actuator health, liveness e readiness.
- [x] Metricas de negocio de ordens de servico via Micrometer/DogStatsD.
- [x] Deploy de producao no EKS via GitHub Actions.
- [x] Publicacao de imagem no ECR com tag do SHA do commit.

## Governanca externa

- Protecao de `develop` e `main`, Required CI Checks e regras de promocao para
  `main` sao centralizadas no repositorio `postech15soat-governance`.

## Debitos tecnicos conhecidos

- Traces de `/actuator/health/**` podem aparecer no Datadog APM e gerar ruido.
  Nao e impeditivo para a entrega e deve ser ajustado posteriormente na
  consolidacao da observabilidade integrada.

## Pendencias de documentacao

- [ ] Adicionar o diagrama arquitetural final do componente apos a consolidacao
  da documentacao da Fase 3.

## Candidatos a ADR ou evolucao futura

- Avaliar centralizacao do ownership/executor das migrations no repositorio
  `postech15soat-infra-database`.
- Avaliar mutation testing com PIT para regras de dominio e application
  services.
