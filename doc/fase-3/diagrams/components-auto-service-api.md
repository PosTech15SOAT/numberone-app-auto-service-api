# Diagrama de Componentes — NumberOne Auto Service API

## Objetivo

Apresentar os principais componentes da Auto Service API e suas dependências, sem detalhar classes. A aplicação recebe o contexto autenticado confiável pelos headers `X-Authenticated-*`; login e emissão de JWT não são responsabilidades deste componente.

```mermaid
flowchart LR
    client[Cliente / Admin] --> rest[API / REST Controllers]

    subgraph app[NumberOne Auto Service API — Spring Boot]
        auth[Contexto autenticado<br/>X-Authenticated-* / Spring Security] --> rest

        rest --> serviceOrder[Service Order]
        rest --> customer[Customer]
        rest --> vehicle[Vehicle]
        rest --> automotive[Automotive Service]
        rest --> inventory[Inventory]

        serviceOrder --> customer
        serviceOrder --> vehicle
        serviceOrder --> automotive
        serviceOrder --> inventory

        serviceOrder --> gateways[Persistência / Gateways]
        customer --> gateways
        vehicle --> gateways
        automotive --> gateways
        inventory --> gateways

        rest --> observability[Observabilidade<br/>logs JSON, APM e métricas]
        serviceOrder --> observability
    end

    gateways --> postgres[(PostgreSQL)]
    observability --> datadog[Datadog Agent]
```

Os módulos usam gateways e adaptadores de persistência para acessar o PostgreSQL. A observabilidade inclui métricas Micrometer/DogStatsD, logs estruturados e traces enviados ao Datadog Agent.
