# Dicionario de Linguagem Ubiqua

## Objetivo

Este documento define a linguagem ubiqua do projeto para alinhar negocio, API, documentacao e implementacao.

O objetivo e garantir que os principais conceitos sejam nomeados sempre da mesma forma, reduzindo ambiguidades entre os modulos:

- `serviceorder`
- `customer`
- `vehicle`
- `inventory`
- `automotiveservice`

## Principios

- Cada conceito relevante do dominio deve ter um nome oficial.
- Sinônimos devem ser evitados em regras de negocio, contratos de API e documentacao funcional.
- O codigo pode permanecer em ingles internamente, mas a linguagem de dominio exposta deve seguir este glossario em portugues.
- Quando um termo tiver sentidos diferentes em contextos distintos, isso deve estar explicito.

## Glossario Compartilhado

| Termo oficial | Definicao | Evitar |
| --- | --- | --- |
| **Cliente** | Pessoa fisica ou juridica contratante do servico e responsavel pelo relacionamento com a oficina | consumidor, usuario |
| **Veiculo** | Bem atendido pela oficina e vinculado a um cliente | automovel, carro |
| **Ordem de servico** | Registro principal do atendimento realizado para um veiculo | OS em documentacao formal, atendimento |
| **Diagnostico** | Avaliacao tecnica feita sobre a necessidade do veiculo | analise |
| **Orcamento** | Proposta comercial associada a uma ordem de servico | cotacao |
| **Item de servico** | Servico individual incluido em uma ordem de servico | item da OS generico |
| **Servico automotivo** | Servico padronizado do catalogo que pode ser associado a um item de servico | procedimento |
| **Catalogo de servicos** | Conjunto de servicos automotivos disponiveis para uso operacional e comercial | tabela de servicos |
| **Insumo** | Material ou peca consumido na execucao de um item de servico | material, peca |
| **Item de estoque** | Produto controlado no estoque com saldo, custo e preco de venda | produto |
| **Movimentacao de estoque** | Registro de entrada, baixa ou ajuste de estoque | transacao de estoque |
| **Previsao de entrega** | Data e hora esperadas para finalizacao ou entrega da ordem de servico | prazo |
| **Acompanhamento** | Visao publica simplificada da ordem de servico para o cliente | tracking |

## Contexto de Cliente

| Termo oficial | Definicao |
| --- | --- |
| **Cliente** | Entidade responsavel pelo veiculo ou solicitante do servico |
| **Documento** | Identificador civil ou fiscal do cliente |
| **Tipo de documento** | Classificacao do documento, como CPF ou CNPJ |
| **Telefone** | Canal de contato do cliente |
| **E-mail** | Canal de notificacao e aprovacao de orcamento |
| **Endereco** | Localizacao vinculada ao cadastro do cliente |
| **Cliente ativo** | Cliente habilitado para relacionamento operacional |

### Recomendacoes

- Sempre usar **Cliente** como termo canonico.
- Evitar termos tecnicos ou digitacoes incorretas como `client`, `costumer` ou `custumer` em comunicacao de negocio.

## Contexto de Veiculo

| Termo oficial | Definicao |
| --- | --- |
| **Veiculo** | Unidade atendida pela oficina |
| **Placa** | Identificador principal do veiculo |
| **Marca** | Fabricante do veiculo |
| **Modelo** | Modelo comercial do veiculo |
| **Ano** | Ano de fabricacao ou referencia do veiculo |
| **Veiculo do cliente** | Forma explicita de representar o vinculo entre veiculo e cliente |

### Recomendacoes

- Usar **Veiculo** como termo oficial em API, documentacao e mensagens.
- Reservar “carro” para comunicacoes informais, nunca como nome canonico.

## Contexto de Ordem de Servico

| Termo oficial | Definicao |
| --- | --- |
| **Ordem de servico** | Agregado principal do atendimento |
| **Descricao inicial** | Relato informado na abertura da ordem |
| **Descricao do diagnostico** | Registro tecnico do diagnostico em andamento |
| **Diagnostico final** | Conclusao tecnica consolidada da avaliacao |
| **Observacao** | Informacao complementar da ordem de servico |
| **Data/hora de entrada** | Momento de recebimento ou abertura da ordem |
| **Data/hora prevista** | Previsao estimada para conclusao ou entrega |
| **Data/hora de entrega** | Momento efetivo de entrega do veiculo |
| **Valor dos servicos** | Soma dos itens de servico e dos insumos cobrados |
| **Tempo estimado** | Estimativa operacional de execucao da ordem |
| **Tempo medio de execucao** | Media real dos tempos dos itens concluidos |

### Status da ordem de servico

| Status oficial | Significado |
| --- | --- |
| **RECEBIDA** | Ordem aberta e veiculo recebido |
| **EM_DIAGNOSTICO** | Equipe realizando avaliacao tecnica |
| **AGUARDANDO_APROVACAO** | Orcamento enviado, aguardando resposta do cliente |
| **APROVADA** | Orcamento aprovado pelo cliente |
| **REJEITADA** | Orcamento rejeitado pelo cliente |
| **EM_EXECUCAO** | Servicos em execucao |
| **FINALIZADA** | Servicos concluidos, aguardando entrega |
| **CANCELADA** | Ordem encerrada sem conclusao operacional |
| **ENTREGUE** | Veiculo devolvido ao cliente |

## Contexto de Orcamento

| Termo oficial | Definicao |
| --- | --- |
| **Orcamento** | Proposta financeira vinculada a uma ordem de servico |
| **Valor proposto** | Valor originalmente enviado ao cliente |
| **Valor aprovado** | Valor efetivamente aceito pelo cliente |
| **Solicitacao de aprovacao** | Envio formal do orcamento para decisao do cliente |
| **Aprovacao do orcamento** | Aceite do orcamento |
| **Rejeicao do orcamento** | Recusa do orcamento |

### Status do orcamento

| Status oficial | Significado |
| --- | --- |
| **RASCUNHO** | Orcamento ainda em elaboracao |
| **ENVIADO** | Orcamento enviado ao cliente |
| **APROVADO** | Orcamento aprovado |
| **REJEITADO** | Orcamento rejeitado |
| **CANCELADO** | Orcamento invalidado ou descontinuado |

### Recomendacoes

- Preferir “**enviar orcamento para aprovacao**” ao inves de “abrir aprovacao”.
- Preferir “**aprovar orcamento**” ao inves de “aprovar ordem” quando o evento for comercial.

## Contexto de Item de Servico

| Termo oficial | Definicao |
| --- | --- |
| **Item de servico** | Linha executavel dentro da ordem de servico |
| **Servico automotivo** | Servico do catalogo associado ao item |
| **Valor do item** | Preco do servico associado ao item |
| **Item opcional** | Servico sugerido, mas nao obrigatorio |
| **Data/hora de inicio** | Inicio real da execucao |
| **Data/hora de fim** | Conclusao real da execucao |
| **Insumos do item** | Materiais ou pecas consumidos naquele item |

### Status do item de servico

| Status oficial | Significado |
| --- | --- |
| **PENDENTE** | Item ainda nao iniciado |
| **AGUARDANDO_PECAS_E_INSUMOS** | Execucao bloqueada por falta de material ou peca |
| **EM_EXECUCAO** | Item sendo executado |
| **FINALIZADO** | Item concluido |
| **CANCELADO** | Item descontinuado |

### Recomendacoes

- Usar **Item de servico** para a instancia dentro da ordem.
- Usar **Servico automotivo** para o servico padronizado de catalogo.

## Contexto de Servico Automotivo

| Termo oficial | Definicao |
| --- | --- |
| **Servico automotivo** | Servico padronizado cadastrado no catalogo e reutilizado nas ordens de servico |
| **Codigo do servico** | Identificador operacional do servico automotivo |
| **Nome do servico** | Nome principal de exibicao do servico |
| **Descricao do servico** | Descricao funcional ou comercial do servico |
| **Tipo de servico** | Classificacao do servico automotivo |
| **Valor base do servico** | Valor de referencia do servico antes da composicao final da ordem |
| **Tempo estimado em minutos** | Estimativa padrao de execucao do servico |
| **Servico ativo** | Servico disponivel para uso em novas ordens |
| **Servico inativo** | Servico indisponivel para novas ordens, mas preservado historicamente |
| **Catalogo de servicos** | Conjunto de servicos automotivos padronizados cadastrados no sistema |

### Tipos de servico

| Tipo oficial | Significado |
| --- | --- |
| **PREVENTIVO** | Servico voltado a prevencao de falhas e manutencao preventiva |
| **CORRETIVO** | Servico voltado a correcao de problema identificado |
| **REVISAO** | Servico de revisao periodica ou inspecao programada |
| **DIAGNOSTICO** | Servico focado em avaliacao tecnica ou identificacao de causa |
| **INSTALACAO** | Servico de instalacao de componente, acessorio ou conjunto |
| **ALINHAMENTO_BALANCEAMENTO** | Servico de alinhamento e balanceamento |
| **OUTROS** | Servico que nao se enquadra nas categorias padronizadas anteriores |

### Recomendacoes

- Usar **Servico automotivo** para o servico padronizado do catalogo.
- Usar **Item de servico** apenas para a instancia concreta que compoe uma ordem de servico.
- Usar **Valor base do servico** como referencia comercial do catalogo.
- Usar **Tempo estimado em minutos** como referencia operacional para planejamento e previsao.

## Contexto de Estoque

| Termo oficial | Definicao |
| --- | --- |
| **Item de estoque** | Produto controlado no inventario |
| **Codigo do item** | Identificador operacional do item de estoque |
| **Quantidade em estoque** | Saldo disponivel |
| **Quantidade minima** | Limite minimo esperado para reposicao ou alerta |
| **Custo unitario** | Custo interno do item |
| **Preco de venda** | Valor cobrado no uso ou venda do item |
| **Tipo do item** | Classificacao do item de estoque |
| **Unidade de medida** | Unidade de controle ou consumo |
| **Item aplicavel ao veiculo** | Informacao de compatibilidade ou aplicacao |

## Contexto de Movimentacao de Estoque

| Termo oficial | Definicao |
| --- | --- |
| **Movimentacao de estoque** | Registro de alteracao de saldo |
| **Entrada** | Acrescimo de estoque |
| **Baixa** | Consumo ou saida de estoque |
| **Ajuste** | Correcao manual ou operacional de saldo |
| **Origem da movimentacao** | Evento que gerou a movimentacao |
| **Quantidade anterior** | Saldo antes da movimentacao |
| **Quantidade posterior** | Saldo depois da movimentacao |
| **Responsavel** | Usuario que registrou a movimentacao |
| **Observacao da movimentacao** | Informacao complementar sobre o evento |

## Relacoes Entre os Conceitos

- Um **Cliente** pode possuir um ou mais **Veiculos**.
- Um **Veiculo** pertence a um **Cliente**.
- Uma **Ordem de servico** pertence a um **Cliente** e a um **Veiculo**.
- Uma **Ordem de servico** possui um ou mais **Itens de servico**.
- Um **Item de servico** referencia um **Servico automotivo** do **Catalogo de servicos**.
- Um **Item de servico** pode consumir um ou mais **Insumos**.
- Um **Insumo** referencia um **Item de estoque**.
- Uma **Ordem de servico** pode ter um ou mais **Orcamentos** ao longo do fluxo.

## Termos a Evitar

| Evitar | Preferir |
| --- | --- |
| OS | Ordem de servico |
| produto | Item de estoque |
| peca | Insumo ou Item de estoque, conforme contexto |
| servico | Servico automotivo ou Item de servico, conforme contexto |
| prazo | Data/hora prevista ou Previsao de entrega |
| tracking | Acompanhamento |
| budget | Orcamento |
| supply | Insumo |
| movement | Movimentacao de estoque |
| catalogo tecnico | Catalogo de servicos |
| procedimento | Servico automotivo |

## Termos Canonicos Prioritarios

Se houver duvida em novos modulos, endpoints, DTOs ou documentacao, priorizar os seguintes termos como fonte da verdade:

- Cliente
- Veiculo
- Ordem de servico
- Diagnostico
- Orcamento
- Item de servico
- Servico automotivo
- Catalogo de servicos
- Insumo
- Item de estoque
- Movimentacao de estoque
- Previsao de entrega
- Acompanhamento
