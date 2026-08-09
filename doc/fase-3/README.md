# Tech Challenge Fase 3 - Aplicacao principal

Este diretorio concentra o planejamento tecnico do repositorio da aplicacao principal.

## Objetivo

Evoluir a aplicacao legada para executar em Kubernetes, atras do API Gateway, consumindo o contexto autenticado produzido pelo Lambda Authorizer e expondo telemetria para a plataforma de observabilidade.

## Documentos

- [Backlog tecnico](backlog.md)
- [Contrato de autenticacao](authentication-contract.md)
- [ADRs](adr/README.md)

## Marcos

1. Estrutura, CI e contratos definidos.
2. Aplicacao funcionando isoladamente com provider local de identidade.
3. Integracao com API Gateway e Lambda Authorizer.
4. Observabilidade, homologacao e evidencias da entrega.
