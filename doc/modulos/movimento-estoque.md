

# README - Movimentação de Estoque

## Objetivo
Registrar o histórico de alterações de saldo dos itens de estoque, garantindo rastreabilidade e auditoria.

## Convenções
- Banco de dados em português
- Código Java em inglês
- Payloads em português
- Valores dos enums em português

## Tabela
`movimentacao_estoque`

## Entidade no código
`InventoryMovement`

## Campos

| Campo | Descrição | Exemplo |
|---|---|---|
| id | Identificador único da movimentação | UUID |
| idItemEstoque | Identificador do item movimentado | UUID |
| tipoMovimentacao | Tipo da alteração de saldo | `ENTRADA` |
| origemMovimentacao | Origem de negócio da movimentação | `COMPRA` |
| referenciaOrigemId | Id da entidade que originou a movimentação | UUID |
| quantidadeAntes | Saldo antes da operação | `10` |
| quantidadeDepois | Saldo depois da operação | `7` |
| observacao | Justificativa complementar | `Baixa por OS` |
| usuarioResponsavelId | Usuário responsável | UUID |
| createdAt | Data/hora do registro | `2026-04-21T10:40:00` |

## Enum `InventoryMovementType`

```java
ENTRADA
BAIXA
AJUSTE
```

## Enum `InventoryMovementOrigin`

```java
COMPRA
ORDEM_SERVICO
VENDA
DEVOLUCAO
PERDA
INVENTARIO
AJUSTE_MANUAL
```

## Regras semânticas dos tipos

| Tipo | Significado |
|---|---|
| ENTRADA | Quando o saldo do item aumenta |
| BAIXA | Quando o saldo do item diminui |
| AJUSTE | Quando o saldo final correto é definido manualmente |

## Regras semânticas das origens

| Origem | Uso esperado |
|---|---|
| COMPRA | Entrada de item recebida no estoque |
| ORDEM_SERVICO | Baixa por consumo em ordem de serviço |
| VENDA | Baixa por venda direta |
| DEVOLUCAO | Entrada por devolução ao estoque |
| PERDA | Baixa por quebra, vencimento ou extravio |
| INVENTARIO | Ajuste decorrente de contagem física |
| AJUSTE_MANUAL | Ajuste manual com observação |

## Endpoints

### `POST /estoque/entrada`
Registra uma entrada de estoque e aumenta o saldo atual do item.

### `POST /estoque/baixa`
Registra uma baixa de estoque e reduz o saldo atual do item.

### `POST /estoque/ajuste`
Registra um ajuste de estoque com base no saldo final desejado.

### `GET /estoque/itens/{itemId}/movimentacoes`
Lista o histórico de movimentações de um item.

## Regras de negócio
- não pode editar movimentação
- não pode excluir movimentação
- histórico é imutável
- item inativo bloqueia movimentação
- `ENTRADA` soma no saldo atual
- `BAIXA` subtrai do saldo atual
- `BAIXA` pode zerar o estoque
- `BAIXA` não pode deixar o estoque negativo
- `AJUSTE` usa saldo final
- toda movimentação deve registrar `quantidadeAntes` e `quantidadeDepois`

## Validações
- item deve existir
- item deve estar ativo
- quantidade obrigatória e maior que zero para `ENTRADA` e `BAIXA`
- quantidade final obrigatória e maior ou igual a zero para `AJUSTE`
- observação obrigatória para `AJUSTE`

## Validação contra o escopo inicial
Itens do escopo original cobertos:
- migration de movimentação de estoque
- entidade de domínio de movimentação
- enum de tipo de movimentação
- repository/gateway de movimentação
- service de estoque
- endpoint de registrar entrada de estoque
- endpoint de registrar baixa de estoque
- endpoint de listar movimentações de um item
- regra de quantidade de estoque não negativa
- regra de entrada soma no estoque
- regra de baixa subtrai do estoque
- regra de ajuste formalizada

Ajustes realizados em relação ao escopo inicial:
- nome da tabela refinado de `movimentacaoEstoque` para `movimentacao_estoque`
- inclusão de `origemMovimentacao` e `referenciaOrigemId` para auditoria
- substituição do conceito genérico de quantidade por `quantidadeAntes` e `quantidadeDepois`
- formalização de ajuste por saldo final
- decisão de manter histórico imutável