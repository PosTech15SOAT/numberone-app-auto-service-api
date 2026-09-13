# Tech Challenge Fase 3 - Aplicacao principal

Este diretorio concentra a documentacao tecnica do repositorio da aplicacao
principal no fechamento da Fase 3.

## Objetivo

Registrar os contratos e decisoes do componente Spring Boot que executa em
Kubernetes, atras do API Gateway, consumindo o contexto autenticado produzido
pelo Lambda Authorizer e expondo telemetria para a plataforma de observabilidade.

## Documentos

- [Backlog tecnico e debitos conhecidos](backlog.md)
- [Contrato de autenticacao](authentication-contract.md)
- [ADRs](adr/README.md)

## Estado da entrega

1. Estrutura, CI e contratos definidos.
2. Aplicacao funcionando localmente com provider local de identidade.
3. Integracao com API Gateway e Lambda Authorizer documentada e refletida no
   backend.
4. Deploy de producao no AWS Academy via GitHub Actions.
5. Observabilidade da aplicacao integrada com Datadog Agent compartilhado do
   cluster.
