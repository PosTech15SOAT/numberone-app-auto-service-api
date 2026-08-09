# README - Serviço Automotivo

## Objetivo
Cadastrar e manter os serviços oferecidos pela oficina mecânica.

## Convenções
- Banco de dados em português
- Código Java em inglês
- Payloads em português
- Valores dos enums em português

## Tabela
`servico_automotivo`

## Entidade no código
`AutomotiveService`

## Campos

| Campo | Descrição | Exemplo |
|---|---|---|
| id | Identificador único do serviço | UUID |
| codigo | Código único do serviço | `SRV-001` |
| nome | Nome do serviço | `Troca de óleo` |
| descricao | Descrição complementar | `Substituição do óleo e filtro` |
| tipoServico | Classificação do serviço | `REVISAO` |
| valorBase | Valor base cobrado | `150.00` |
| tempoEstimadoMinutos | Duração estimada em minutos | `60` |
| ativo | Indica se o serviço está ativo | `true` |
| createdAt | Data/hora de criação | `2026-04-21T10:00:00` |
| updatedAt | Data/hora da última atualização | `2026-04-21T10:30:00` |

## Enum `ServiceType`

```java
MANUTENCAO_PREVENTIVA
MANUTENCAO_CORRETIVA
REVISAO
DIAGNOSTICO
INSTALACAO
ALINHAMENTO_BALANCEAMENTO
OUTROS
```

## Endpoints

### `POST /servicos`
Cria um novo serviço automotivo.

### `PUT /servicos/{id}`
Atualiza os dados cadastrais de um serviço.

### `GET /servicos`
Lista os serviços ativos.

### `GET /servicos/{id}`
Detalha um serviço específico, inclusive se estiver inativo.

### `PATCH /servicos/{id}/inativar`
Inativa logicamente um serviço.

### `PATCH /servicos/{id}/ativar`
Reativa logicamente um serviço.

## Regras de negócio
- código obrigatório
- código único
- nome obrigatório
- tipoServico obrigatório
- valorBase maior ou igual a zero
- tempoEstimadoMinutos obrigatório e maior que zero
- não excluir fisicamente
- inativação lógica por `ativo = false`
- reativação lógica por `ativo = true`
- serviço inativo pode ser atualizado
- serviço inativo não aparece na listagem padrão
- serviço inativo pode ser detalhado

## Validação contra o escopo inicial
Itens do escopo original cobertos:
- migration de serviço
- entidade de domínio de serviço
- repository/gateway de serviço
- service de serviço
- endpoint de criar serviço
- endpoint de atualizar serviço
- endpoint de listar serviços
- endpoint de detalhar serviço
- endpoint de inativar serviço

Ajustes realizados em relação ao escopo inicial:
- nome da tabela refinado de `servico` para `servico_automotivo`
- inclusão do endpoint de ativar serviço
- definição formal de `codigo` como obrigatório e único
- definição de enum de tipo de serviço
- formalização da regra de listagem sem inativos por padrão
