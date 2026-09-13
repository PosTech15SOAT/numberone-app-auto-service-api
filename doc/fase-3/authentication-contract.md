# Contrato de autenticacao

Status: contrato implementado para a entrega da Fase 3.

## Responsabilidades

### Lambda de autenticacao

- Validar o CPF.
- Consultar existencia e status do cliente.
- Emitir JWT com expiracao definida.

### Lambda Authorizer e API Gateway

- Validar assinatura, emissor, expiracao e claims obrigatorias.
- Bloquear tokens invalidos antes de encaminhar a requisicao.
- Remover ou sobrescrever headers de identidade enviados pelo cliente.
- Encaminhar para a aplicacao somente um contexto confiavel.

### Aplicacao principal

- Converter o contexto do Gateway em `AuthenticatedUser`.
- Aplicar roles e permissions aos endpoints protegidos.
- Nao emitir nem validar credenciais de clientes.
- Nao registrar JWT, secrets ou CPF completo em logs.

## Campos do contexto autenticado

| Campo | Obrigatorio | Descricao |
|---|---:|---|
| `subject` | sim | Identificador imutavel do usuario autenticado. |
| `customerId` | sim para cliente | Identificador do cliente na aplicacao. |
| `status` | sim | Situacao do usuario ou cliente. |
| `roles` | sim | Papeis atribuidos ao usuario. |
| `permissions` | sim | Permissoes efetivas. |
| `correlationId` | sim | Identificador de correlacao da requisicao. |

## Headers do API Gateway

Os nomes são configuráveis por variáveis de ambiente para que o contrato possa
ser ajustado sem alterar codigo. Os valores padrao sao:

| Campo | Header padrão | Formato |
|---|---|---|
| `subject` | `X-Authenticated-Subject` | texto não vazio |
| `customerId` | `X-Authenticated-Customer-Id` | UUID; opcional para identidades sem cliente |
| `status` | `X-Authenticated-Status` | texto; `ACTIVE` representa usuário ativo |
| `roles` | `X-Authenticated-Roles` | lista separada por vírgulas |
| `permissions` | `X-Authenticated-Permissions` | lista separada por vírgulas; pode ser vazia |
| `correlationId` | `X-Correlation-Id` | texto não vazio |

O CPF nao e propagado para esta aplicacao. A aplicacao considera uma
requisição anônima quando `X-Authenticated-Subject` não está presente. Se o
subject estiver presente, todos os demais headers obrigatórios devem ser
válidos; contexto parcial é rejeitado.

Em produção, esses headers só são confiáveis se o tráfego direto aos pods for
bloqueado e o API Gateway remover ou sobrescrever valores enviados pelo cliente.
