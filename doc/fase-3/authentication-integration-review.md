# Especificacao para revisao da integracao de autenticacao

## Objetivo

Validar entre os repositorios o contrato usado pela API principal para receber uma identidade ja autenticada pelo API Gateway/Lambda Authorizer. A implementacao da API usa um contrato provisorio para permitir desenvolvimento paralelo; os itens abaixo precisam ser confirmados antes da integracao em ambiente compartilhado.

## Estado implementado na API principal

- A API nao emite e nao valida JWT.
- O endpoint interno `POST /api/public/auth/login`, o usuario administrativo local no banco e as dependencias JJWT foram removidos.
- Em producao, o provider `gateway` le headers confiaveis e cria `AuthenticatedUser`.
- Contexto parcial ou malformado retorna `401`.
- Usuario com status diferente de `ACTIVE` retorna `403`.
- Rotas `/api/admin/**` exigem role `ADMIN`.
- Acompanhamento de OS exige `ADMIN` ou `SERVICE_ORDER_TRACK_OWN`, seguido da verificacao de propriedade pelo `customerId`.
- Aprovar ou rejeitar orcamento pelo fluxo do cliente exige `ADMIN` ou `BUDGET_RESPOND_OWN`, seguido da verificacao de propriedade.
- O provider `local` permite desenvolvimento sem Lambda/Gateway e deve permanecer desabilitado em producao.

## Contrato provisorio API Gateway -> API

| Header | Obrigatoriedade | Regra atual |
|---|---|---|
| `X-Authenticated-Subject` | identidade autenticada | texto nao vazio |
| `X-Authenticated-Customer-Id` | cliente | UUID; ausente para admin tecnico |
| `X-Authenticated-Status` | autenticado | somente `ACTIVE` acessa recursos |
| `X-Authenticated-Roles` | autenticado | CSV; exemplo `ADMIN` ou `CUSTOMER` |
| `X-Authenticated-Permissions` | autenticado | CSV, presente mesmo quando vazio |
| `X-Correlation-Id` | autenticado | texto nao vazio |

Esses headers nunca podem ser aceitos diretamente da internet. O Gateway deve remover ou sobrescrever todos eles com dados produzidos pelo Authorizer.

## Analise de Marcelo — autenticacao e Authorizer

Entregaveis esperados:

1. Confirmar o payload definitivo do JWT: `iss`, `aud`, `sub`, `customer_id`, `status`, `roles`, `permissions`, `iat`, `exp` e `jti`.
2. Confirmar algoritmo assimetrico, identificador de chave (`kid`), armazenamento e rotacao de chaves. Proposta: RS256, sem secret compartilhado com a API.
3. Definir duracao do access token e comportamento para cliente inexistente ou inativo.
4. Confirmar os nomes e formatos dos headers da tabela acima, ou devolver o mapeamento definitivo para ajuste na API.
5. Garantir que JWT ausente, expirado, adulterado, com emissor/audiencia invalidos ou claims obrigatorias ausentes nao alcance a API.
6. Garantir que CPF completo, token e chaves nao sejam gravados em logs.
7. Fornecer dois exemplos sanitizados: payload valido de cliente e contexto/header gerado pelo Authorizer.

Decisao solicitada: o CPF nao e propagado para a API principal, porque `subject` e `customerId` atendem a autorizacao atual. Marcelo deve confirmar se existe algum caso funcional que exija alterar essa decisao.

## Analise de Julio — API Gateway e infraestrutura

Entregaveis esperados:

1. Confirmar que todas as rotas protegidas usam o Authorizer e que somente `/api/public/health` permanece anonima.
2. Remover ou sobrescrever os seis headers de identidade recebidos do cliente antes de encaminhar a requisicao.
3. Mapear o contexto do Authorizer para os headers definitivos e preservar/gerar o correlation ID.
4. Bloquear acesso direto ao container/pod/load balancer por origem externa, permitindo somente o caminho aprovado via Gateway/VPC Link/rede interna.
5. Confirmar TLS, security groups/network policies e destino interno usados entre Gateway e API.
6. Executar teste negativo tentando forjar `X-Authenticated-Roles: ADMIN` diretamente contra a API.

## Analise de Anderson — banco de dados

Entregaveis esperados:

1. Confirmar que nenhuma tabela da API principal e necessaria para emissao ou validacao de credenciais.
2. Avaliar a tabela legada `admin_users`, ainda criada por migracao antiga. A aplicacao nao a utiliza mais.
3. Decidir entre mante-la temporariamente por compatibilidade ou criar uma nova migracao Flyway que a remova. Nao editar uma migracao ja aplicada.
4. Confirmar que o `customerId` recebido no contexto corresponde ao UUID persistido em `cliente.id`.

## Cenarios de aceite conjunto

- CPF valido gera token e permite ao cliente consultar somente sua propria OS.
- Cliente nao consegue consultar OS ou responder orcamento de outro cliente.
- Administrador consegue acessar rotas administrativas e pode ignorar a restricao de propriedade.
- Token ausente, expirado, adulterado, com `iss` ou `aud` invalidos e bloqueado antes da API.
- Cliente inativo recebe `403`.
- Header de identidade parcial ou UUID de cliente invalido recebe `401`.
- Header `ADMIN` forjado pelo consumidor nao e encaminhado.
- Uma chamada direta que contorne o Gateway e bloqueada pela infraestrutura.
- O correlation ID pode ser rastreado entre Gateway, Authorizer e API sem registrar token ou CPF completo.

## Criterio para fechar a revisao

A revisao termina quando Marcelo e Julio aprovarem o mesmo contrato de claims/headers, Julio comprovar o bloqueio do acesso direto, Anderson registrar a decisao sobre `admin_users` e os cenarios de aceite forem executados no ambiente integrado. Qualquer alteracao nos nomes de headers pode ser aplicada por variavel de ambiente; alteracoes semanticas de roles e permissions exigem ajuste de codigo e testes na API.
