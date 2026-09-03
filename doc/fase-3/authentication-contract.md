# Contrato de autenticacao

Status: contrato provisório implementado, aguardando validação com o responsável pela Lambda e pelo API Gateway.

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

## Campos propostos

| Campo | Obrigatorio | Descricao |
|---|---:|---|
| `subject` | sim | Identificador imutavel do usuario autenticado. |
| `customerId` | sim para cliente | Identificador do cliente na aplicacao. |
| `cpf` | a confirmar | CPF normalizado; evitar propagacao se nao for necessario. |
| `status` | sim | Situacao do usuario ou cliente. |
| `roles` | sim | Papeis atribuidos ao usuario. |
| `permissions` | sim | Permissoes efetivas. |
| `correlationId` | sim | Identificador de correlacao da requisicao. |

## Headers provisórios do API Gateway

Os nomes são configuráveis por variáveis de ambiente para que o contrato possa
ser ajustado sem alterar código. Os valores padrão são:

| Campo | Header padrão | Formato |
|---|---|---|
| `subject` | `X-Authenticated-Subject` | texto não vazio |
| `customerId` | `X-Authenticated-Customer-Id` | UUID; opcional para identidades sem cliente |
| `status` | `X-Authenticated-Status` | texto; `ACTIVE` representa usuário ativo |
| `roles` | `X-Authenticated-Roles` | lista separada por vírgulas |
| `permissions` | `X-Authenticated-Permissions` | lista separada por vírgulas; pode ser vazia |
| `correlationId` | `X-Correlation-Id` | texto não vazio |

O CPF não é propagado neste contrato provisório. A aplicação considera uma
requisição anônima quando `X-Authenticated-Subject` não está presente. Se o
subject estiver presente, todos os demais headers obrigatórios devem ser
válidos; contexto parcial é rejeitado.

Em produção, esses headers só são confiáveis se o tráfego direto aos pods for
bloqueado e o API Gateway remover ou sobrescrever valores enviados pelo cliente.

## Decisoes pendentes

- [ ] Nomes definitivos dos claims e headers.
- [ ] Algoritmo e estrategia de rotacao de chaves.
- [ ] Emissor e audiencia esperados.
- [ ] Duracao do token.
- [ ] Semantica de `401` e `403`.
- [ ] Roles e permissions do RBAC.
- [ ] Tratamento de usuario inativo ou inexistente.
- [ ] Mecanismo que impede acesso direto aos pods sem passar pelo Gateway.
