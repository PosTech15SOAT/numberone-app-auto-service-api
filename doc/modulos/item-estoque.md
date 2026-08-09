# README - Item de Estoque

## Objetivo
Cadastrar e manter peças, insumos e demais itens controlados em estoque.

## Convenções
- Banco de dados em português
- Código Java em inglês
- Payloads em português
- Valores dos enums em português

## Tabela
`item_estoque`

## Entidade no código
`InventoryItem`

## Campos

| Campo | Descrição | Exemplo |
|---|---|---|
| id | Identificador único do item | UUID |
| codigo | Código único do item | `ITM-001` |
| nome | Nome do item | `Óleo 5W30` |
| descricao | Descrição complementar | `Óleo sintético para motor` |
| tipoItem | Classificação do item | `LUBRIFICANTE` |
| unidadeMedida | Unidade de controle | `LITRO` |
| custoUnitario | Custo unitário do item | `20.00` |
| precoVenda | Preço de venda do item | `35.00` |
| quantidadeEstoque | Saldo atual disponível | `50` |
| estoqueMinimo | Quantidade mínima desejada | `10` |
| marca | Marca do item | `Mobil` |
| veiculoAplicavel | Veículo ou contexto de aplicação | `Gol 1.6` |
| ativo | Indica se o item está ativo | `true` |
| createdAt | Data/hora de criação | `2026-04-21T10:00:00` |
| updatedAt | Data/hora da última atualização | `2026-04-21T10:30:00` |

## Enum `ItemType`

```java
PECA
INSUMO
LUBRIFICANTE
ACESSORIO
```

## Enum `UnitOfMeasure`

```java
UNIDADE
LITRO
MILILITRO
QUILO
GRAMA
CAIXA
```

## Endpoints

### `POST /itens`
Cria um novo item de estoque.

### `PUT /itens/{id}`
Atualiza os dados cadastrais de um item.

### `GET /itens`
Lista os itens ativos.

### `GET /itens/{id}`
Detalha um item específico, incluindo saldo atual.

### `PATCH /itens/{id}/inativar`
Inativa logicamente um item.

## Regras de negócio
- código obrigatório
- código único
- nome obrigatório
- tipoItem obrigatório
- unidadeMedida obrigatória
- custoUnitario maior que zero
- precoVenda maior que zero
- quantidadeEstoque maior ou igual a zero
- estoqueMinimo maior ou igual a zero
- não excluir fisicamente
- item inativo pode ser atualizado
- item inativo não aparece na listagem padrão
- item inativo pode ser detalhado
- item inativo não pode receber movimentação

## Validação contra o escopo inicial
Itens do escopo original cobertos:
- migration de item
- entidade de domínio de item
- repository/gateway de item
- service de item
- endpoint de criar item
- endpoint de atualizar item
- endpoint de listar itens
- endpoint de detalhar item
- endpoint de consultar saldo, resolvido via `GET /itens/{id}` com `quantidadeEstoque`

Ajustes realizados em relação ao escopo inicial:
- nome da tabela refinado de `item` para `item_estoque`
- inclusão do endpoint de inativar item, coerente com o campo `ativo`
- definição formal de `codigo` como obrigatório e único
- definição dos enums `tipoItem` e `unidadeMedida`
- decisão de não criar endpoint separado de saldo no MVP, pois o detalhamento do item já atende o caso