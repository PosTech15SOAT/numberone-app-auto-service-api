# README - Ordem de Serviço

## Objetivo
Registrar e acompanhar o ciclo de atendimento da oficina, desde a abertura da ordem de serviço até a entrega do veículo, incluindo itens de serviço, insumos, orçamento e acompanhamento do cliente.

## Convenções
- Banco de dados em português
- Código Java em inglês
- Payloads em português
- Status expostos nas APIs em português

## Tabelas
- `ordem_servico`
- `ordem_servico_servico`
- `ordem_servico_servico_item`
- `ordem_servico_orcamento`

## Entidades no código
- `ServiceOrder`
- `ServiceOrderItem`
- `ServiceOrderItemSupply`
- `ServiceOrderBudget`

## Estrutura semântica do módulo

### Ordem de serviço
Representa o agregado principal do atendimento. Centraliza cliente, veículo, diagnóstico, status, previsão, entrega e os demais subelementos operacionais.

### Item de serviço
Representa um serviço executável dentro da ordem. Cada item referencia um `AutomotiveService` e possui valor, status, opcionalidade e tempos de execução.

### Insumo do item
Representa o vínculo entre um item de serviço e um item de estoque consumido na execução.

### Orçamento
Representa a proposta financeira vinculada à ordem de serviço, incluindo solicitação de aprovação, decisão do cliente e valor aprovado.

## Campos principais

### Ordem de serviço

| Campo | Descrição | Exemplo |
|---|---|---|
| id | Identificador único da ordem | UUID |
| descricaoInicial | Relato inicial do problema | `Cliente relata ruído ao frear` |
| descricaoDiagnostico | Registro do diagnóstico em andamento | `Desgaste avançado nas pastilhas dianteiras` |
| descricaoDiagnosticoFinal | Conclusão técnica consolidada | `Necessária troca das pastilhas e revisão do fluido` |
| observacao | Observações complementares | `Veículo precisa ser entregue até sexta-feira` |
| idCliente | Cliente vinculado à ordem | UUID |
| idVeiculo | Veículo vinculado à ordem | UUID |
| status | Situação atual da ordem | `EM_DIAGNOSTICO` |
| dataHoraEntrada | Data/hora de abertura | `2026-05-03T09:30:00` |
| dataHoraPrevista | Previsão de entrega | `2026-05-04T17:00:00` |
| dataHoraEntrega | Data/hora efetiva da entrega | `2026-05-04T16:40:00` |
| createdAt | Data/hora de criação | `2026-05-03T09:30:00` |
| updatedAt | Data/hora da última atualização | `2026-05-03T10:10:00` |

### Item de serviço

| Campo | Descrição | Exemplo |
|---|---|---|
| id | Identificador único do item da OS | UUID |
| idOrdemServico | Ordem à qual o item pertence | UUID |
| idServico | Serviço automotivo referenciado | UUID |
| valor | Valor cobrado pelo item | `280.00` |
| status | Situação atual do item | `EM_EXECUCAO` |
| opcional | Indica se o item é opcional | `false` |
| dataHoraInicio | Início real da execução | `2026-05-03T14:00:00` |
| dataHoraFim | Fim real da execução | `2026-05-03T15:20:00` |
| createdAt | Data/hora de criação | `2026-05-03T10:00:00` |
| updatedAt | Data/hora da última atualização | `2026-05-03T15:20:00` |

### Insumo do item de serviço

| Campo | Descrição | Exemplo |
|---|---|---|
| id | Identificador único do consumo | UUID |
| idOrdemServicoServico | Item da OS que consumiu o insumo | UUID |
| idItemEstoque | Item de estoque utilizado | UUID |
| quantidadeUsada | Quantidade consumida | `2` |
| createdAt | Data/hora de criação | `2026-05-03T14:05:00` |
| updatedAt | Data/hora da última atualização | `2026-05-03T14:10:00` |

### Orçamento

| Campo | Descrição | Exemplo |
|---|---|---|
| id | Identificador único do orçamento | UUID |
| idOrdemServico | Ordem à qual o orçamento pertence | UUID |
| valorProposto | Valor originalmente enviado | `520.00` |
| valorAprovado | Valor aprovado pelo cliente | `520.00` |
| status | Situação atual do orçamento | `ENVIADO` |
| enviadoEm | Data/hora de envio ao cliente | `2026-05-03T11:00:00` |
| aprovadoEm | Data/hora da aprovação | `2026-05-03T12:15:00` |
| createdAt | Data/hora de criação | `2026-05-03T10:40:00` |
| updatedAt | Data/hora da última atualização | `2026-05-03T12:15:00` |

## Enum `ServiceOrderStatus`

```java
RECEIVED
IN_DIAGNOSIS
WAITING_APPROVAL
APPROVED
REJECTED
IN_PROGRESS
COMPLETED
CANCELLED
DELIVERED
```

## Enum `OrderItemStatus`

```java
PENDING
WAITING_FOR_PARTS_AND_SUPPLIES
IN_PROGRESS
CANCELLED
COMPLETED
```

## Enum `ServiceOrderBudgetStatus`

```java
DRAFT
SENT
APPROVED
REJECTED
CANCELLED
```

## Endpoints

### Ordem de serviço administrativa

### `POST /api/admin/ordens-servico`
Cria uma nova ordem de serviço.

### `GET /api/admin/ordens-servico`
Lista as ordens cadastradas.

### `GET /api/admin/ordens-servico/{id}`
Detalha uma ordem específica.

### `PATCH /api/admin/ordens-servico/{id}/iniciar-diagnostico`
Registra o diagnóstico final e atualiza a ordem para o fluxo de diagnóstico.

### `GET /api/admin/ordens-servico/{id}/calcular-servicos`
Calcula o valor total da ordem com base em serviços e insumos.

### `GET /api/admin/ordens-servico/{id}/calcular-tempo-estimado`
Calcula o tempo estimado da ordem com base nos serviços associados.

### `GET /api/admin/ordens-servico/{id}/tempo-medio-execucao-servicos`
Calcula o tempo médio real de execução dos itens concluídos.

### `PATCH /api/admin/ordens-servico/{id}/cancelar`
Cancela a ordem de serviço.

### `PATCH /api/admin/ordens-servico/{id}/iniciar`
Coloca a ordem em execução.

### `PATCH /api/admin/ordens-servico/{id}/concluir`
Finaliza a ordem de serviço.

### `PATCH /api/admin/ordens-servico/{id}/entregar`
Registra a entrega do veículo.

### Itens da ordem

### `POST /api/admin/itens-ordem-servico`
Adiciona um item de serviço à ordem.

### `PATCH /api/admin/itens-ordem-servico/{id}/iniciar`
Inicia a execução do item de serviço.

### `PATCH /api/admin/itens-ordem-servico/{id}/cancelar`
Cancela o item de serviço.

### `PATCH /api/admin/itens-ordem-servico/{id}/concluir`
Finaliza o item de serviço.

### `DELETE /api/admin/itens-ordem-servico/{id}`
Remove o item da ordem.

### Insumos do item

### `POST /api/admin/itens-ordem-servico/{serviceOrderItemId}/insumos`
Associa um insumo ao item da ordem.

### `PUT /api/admin/itens-ordem-servico/{serviceOrderItemId}/insumos/{id}`
Atualiza o item de estoque ou a quantidade usada no consumo.

### `GET /api/admin/itens-ordem-servico/{serviceOrderItemId}/insumos`
Lista os insumos associados ao item.

### `DELETE /api/admin/itens-ordem-servico/{serviceOrderItemId}/insumos/{id}`
Remove o consumo registrado.

### Orçamento

### `POST /api/admin/ordens-servico/{serviceOrderId}/orcamentos`
Cria um orçamento para a ordem de serviço.

### `PATCH /api/admin/orcamentos-ordem-servico/{id}/solicitar-aprovacao`
Solicita aprovação do orçamento e dispara a notificação por e-mail.

### `PATCH /api/admin/orcamentos-ordem-servico/{id}/aprovar`
Aprova o orçamento.

### `PATCH /api/admin/orcamentos-ordem-servico/{id}/rejeitar`
Rejeita o orçamento.

### Aprovação pública por link

### `GET /api/public/orcamentos-ordem-servico/{id}/aprovacao/aprovar`
Aprova o orçamento por link público.

### `GET /api/public/orcamentos-ordem-servico/{id}/aprovacao/rejeitar`
Rejeita o orçamento por link público.

### Acompanhamento público

### `GET /api/public/ordens-servico/{id}/acompanhamento`
Expõe uma visão simplificada da ordem para o cliente.

## Regras de negócio
- ordem deve estar vinculada a cliente e veículo válidos
- cliente precisa estar ativo para abertura da ordem
- serviço automotivo precisa estar ativo para inclusão em item de serviço
- item de serviço pode consumir insumos do estoque
- valor total da ordem deve considerar serviços e insumos
- orçamento pertence a uma única ordem de serviço
- solicitação de aprovação exige e-mail válido do cliente
- transições de status da ordem devem respeitar a máquina de estados do domínio
- transições de status do item devem respeitar a máquina de estados do domínio
- não permitir iniciar, cancelar ou concluir item já no mesmo status
- iniciar item deve registrar `dataHoraInicio`
- concluir item deve registrar `dataHoraFim`
- entrega da ordem deve registrar `dataHoraEntrega`
- cálculo de tempo estimado usa `tempoEstimadoMinutos` dos serviços automotivos
- tempo médio de execução considera apenas itens concluídos com início e fim válidos

## Validação contra o escopo inicial
Itens do escopo original cobertos:
- migration da ordem de serviço
- entidade de domínio da ordem
- entidade de item da ordem
- entidade de insumo do item
- entidade de orçamento
- services/use cases de ordem, item, insumo e orçamento
- cálculo de valor da ordem
- cálculo de tempo estimado
- criação de orçamento
- aprovação e rejeição de orçamento
- acompanhamento público da ordem
- envio de e-mail de aprovação

Ajustes realizados em relação ao escopo inicial:
- separação do módulo em subfluxos de ordem, item, insumo, orçamento e tracking
- inclusão de acompanhamento público em endpoint próprio
- inclusão de cálculo de tempo médio de execução
- tradução dos status expostos nas APIs
- criação de commands específicos para updates parciais do fluxo
- adoção de persistência parcial para mudanças de status e campos escalares
