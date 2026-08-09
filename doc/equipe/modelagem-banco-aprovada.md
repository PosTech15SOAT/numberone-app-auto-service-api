# Modelagem de Banco Aprovada

Neste documento, registro a modelagem de banco que defini para o projeto nesta etapa, com foco em manter a estrutura original o maximo possivel e fazer apenas os ajustes necessarios para atender melhor ao desafio.

## Objetivo

Minha ideia foi seguir uma linha simples:

- preservar o que ja estava bom
- mudar apenas o que era realmente necessario
- evitar redesenhar o banco inteiro
- deixar a modelagem mais aderente ao MVP

## Resumo das decisoes

Mantive sem mudanca estrutural:

- `ordemServico`
- `ordemServicoServico`
- `ordemServicoServicoItens`
- `EStatusOS`
- `EStatusServico`

Ajustei:

- `cliente`
- `veiculo`
- `servico`
- `item`
- `orcamento`

Adicionei:

- `movimentacaoEstoque`

## 1. Tabela `cliente`

### Estrutura final

- `id`
- `nome`
- `documento`
- `tipoDocumento`
- `telefone`
- `email`
- `endereco`
- `ativo`
- `createdAt`
- `updatedAt`

### Decisao

Adicionei:

- `tipoDocumento`

### Valores definidos para `tipoDocumento`

- `CPF`
- `CNPJ`

### Motivo

- a estrutura ja atende bem o MVP
- o campo ajuda a deixar explicito se o documento do cliente e CPF ou CNPJ
- melhora a validacao e evita regra implicita so no codigo

## 2. Tabela `veiculo`

### Estrutura final

- `id`
- `placa`
- `marca`
- `modelo`
- `ano`
- `idCliente`
- `createdAt`
- `updatedAt`

### Decisao

Adicionei:

- `placa`
- `ano`

Tambem defini `placa` como unica.

### Motivo

- o desafio exige `placa`, `marca`, `modelo` e `ano`
- a modelagem anterior nao fechava completamente esse requisito
- foi a menor mudanca possivel para adequar a tabela

## 3. Tabela `servicoAutomotivo`

### Estrutura final

- `id`
- `codigo`
- `nome`
- `descricao`
- `tipoServico`
- `valorBase`
- `tempoEstimadoMinutos`
- `ativo`
- `createdAt`
- `updatedAt`

### Decisao

Adicionei novos atributos para tornar o servico mais descritivo e aderente ao contexto de oficina mecanica automotiva.

### Valores definidos para `tipoServico`

- `MANUTENCAO_PREVENTIVA`
- `MANUTENCAO_CORRETIVA`
- `REVISAO`
- `DIAGNOSTICO`
- `INSTALACAO`
- `ALINHAMENTO_BALANCEAMENTO`
- `OUTROS`

### Motivo

- alterei o nome da tabela de `servico` para `servicoAutomotivo` para deixar explicito o contexto de negocio
- `nome` melhora listagem, busca e identificacao do servico
- `tipoServico` permite classificacao e filtros mais claros
- `tempoEstimadoMinutos` faz mais sentido do que um campo generico de tempo, porque:
    - evita ambiguidades
    - facilita ordenacao, comparacao e calculo
    - permite representar qualquer duracao em uma unidade simples
- `ativo` permite inativacao sem exclusao fisica
- `createdAt` e `updatedAt` ajudam em rastreabilidade e auditoria


## 4. Tabela `itemEstoque`

### Estrutura final

- `id`
- `codigo`
- `nome`
- `descricao`
- `tipoItem`
- `unidadeMedida`
- `custoUnitario`
- `precoVenda`
- `quantidadeEstoque`
- `estoqueMinimo`
- `marca`
- `veiculoAplicavel`
- `ativo`
- `createdAt`
- `updatedAt`

### Decisao

Refatorei a estrutura para deixar o cadastro de itens mais aderente ao contexto de estoque da mecanica.

### Valores definidos para `tipoItem`

- `PECA`
- `INSUMO`
- `LUBRIFICANTE`
- `ACESSORIO`

### Valores definidos para `unidadeMedida`

- `UNIDADE`
- `LITRO`
- `MILILITRO`
- `QUILO`
- `GRAMA`
- `CAIXA`

### Motivo

- alterei o nome da tabela de `item` para `itemEstoque` para deixar explicito que a entidade representa itens controlados em estoque
- substitui `valorBase` por:
    - `custoUnitario`
    - `precoVenda`
- essa separacao evita ambiguidade entre custo e valor cobrado do cliente
- `codigo` melhora identificacao e busca
- `estoqueMinimo` permite futuro controle de reposicao (criar alertas caso quantidadeEstoque esteja menor ou igual que estoqueMinimo)
- `marca` faz sentido no contexto de pecas, insumos e lubrificantes
- `veiculoAplicavel` substitui um campo generico de aplicacao e deixa mais claro o uso no contexto automotivo
- `ativo` permite inativacao sem exclusao fisica
- `createdAt` e `updatedAt` ajudam no controle e auditoria


## 5. Tabela `movimentacaoEstoque`

### Estrutura final

- `id`
- `idItemEstoque`
- `tipoMovimentacao`
- `origemMovimentacao`
- `referenciaOrigemId`
- `quantidadeAntes`
- `quantidadeDepois`
- `observacao`
- `usuarioResponsavelId`
- `createdAt`

### Decisao

Ajustei a estrutura para melhorar rastreabilidade, historico e auditoria das movimentacoes de estoque.

### Valores definidos para `tipoMovimentacao`

- `ENTRADA`
- `BAIXA`
- `AJUSTE`

### Valores definidos para `origemMovimentacao`

- `COMPRA`
- `ORDEM_SERVICO`
- `AJUSTE_MANUAL`
- `DEVOLUCAO`
- `PERDA`
- `INVENTARIO`

### Motivo

- mantive o nome `movimentacaoEstoque` porque ele ja representa bem o historico das alteracoes de saldo
- substitui `quantidade` por:
    - `quantidadeAntes`
    - `quantidadeDepois`
- isso melhora auditoria e investigacao de divergencias
- `origemMovimentacao` estrutura a origem real da alteracao
- `referenciaOrigemId` permite rastrear a entidade responsavel pela movimentacao, como uma ordem de servico
- `observacao` permite guardar detalhes livres sem poluir os enums
- `usuarioResponsavelId` permite saber quem realizou a movimentacao
- `createdAt` registra quando a movimentacao aconteceu
- `quantidadeEstoque` em `itemEstoque` resolve o saldo atual
- `movimentacaoEstoque` resolve o historico
- assim fica possivel manter simplicidade sem perder rastreabilidade

## 6. Tabela `ordemServico`

### Estrutura final

- `id`
- `descricaoInicial`
- `descricaoDiagnostico`
- `observacao`
- `idCliente`
- `idVeiculo`
- `status`
- `dataHoraEntrada`
- `dataHoraPrevista`
- `dataHoraEntrega`
- `createdAt`
- `updatedAt`

### Decisao

Mantive a tabela como estava.

### Motivo

- considerei suficiente para seguir com o MVP
- preferi nao aumentar o escopo com novos campos nesse momento

## 7. Tabela `orcamento`

### Estrutura final

- `id`
- `idOrdemServico`
- `valorProposto`
- `valorAceito`
- `status`
- `enviadoEm`
- `respondidoEm`

### Decisao

Adicionei:

- `status`
- `enviadoEm`
- `respondidoEm`

### Status definidos

- `RASCUNHO`
- `ENVIADO`
- `APROVADO`
- `REPROVADO`

### Motivo

- a estrutura anterior era simples demais para o fluxo de aprovacao
- esses campos resolvem bem o ciclo de envio e resposta do orcamento

## 8. Tabela `ordemServicoServico`

### Estrutura final

- `id`
- `idOrdemServico`
- `idServico`
- `valorReal`
- `status`
- `opcional`
- `dataHoraInicio`
- `dataHoraFinal`

### Decisao

Mantive a tabela como estava.

### Motivo

- ela ja representa bem cada servico executado dentro da OS
- o modelo atual ja ajuda no acompanhamento operacional

## 9. Tabela `ordemServicoServicoItens`

### Estrutura final

- `id`
- `idOrdemServicoServico`
- `idItem`
- `quantidadeUsada`

### Decisao

Mantive a tabela como estava.

### Motivo

- ela ja resolve o vinculo entre item consumido e servico executado
- para o MVP, considerei suficiente

## 10. Enum `EStatusOS`

### Valores finais

- `RECEBIDA`
- `EM_DIAGNOSTICO`
- `AGUARDANDO_APROVACAO`
- `EM_EXECUCAO`
- `FINALIZADA`
- `ENTREGUE`

### Decisao

Mantive exatamente esses status.

### Motivo

- eles estao alinhados diretamente com o desafio
- preferi nao aumentar o escopo com status extras

## 11. Enum `EStatusServico`

### Valores finais

- `PENDENTE`
- `AGUARDANDO_PECAS_INSUMOS`
- `EM_TRABALHO`
- `CONCLUIDO`

### Decisao

Mantive exatamente esses status.

### Motivo

- eles representam bem o andamento interno de cada servico da OS
- separam corretamente o status do servico do status geral da ordem

## Principais mudancas realizadas

As principais mudancas que defini nesta modelagem foram:

- adicionar `placa` e `ano` em `veiculo`
- tornar `placa` unica
- renomear `tempoEstimado` para `tempoEstimadoMinuto`
- adicionar `quantidadeEstoque` em `item`
- criar `movimentacaoEstoque`
- adicionar `status`, `enviadoEm` e `respondidoEm` em `orcamento`

## Conclusao

Minha decisao foi seguir uma abordagem de menor impacto:

- manter o que ja estava bom
- corrigir apenas o necessario
- reforcar estoque e orcamento
- evitar complexidade desnecessaria na fase atual

Com isso, a modelagem continua simples, mas fica mais adequada aos requisitos do desafio.
