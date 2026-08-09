# Contrato de autenticacao

Status: rascunho aguardando validacao com o responsavel pela Lambda e pelo API Gateway.

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

## Decisoes pendentes

- [ ] Nomes definitivos dos claims e headers.
- [ ] Algoritmo e estrategia de rotacao de chaves.
- [ ] Emissor e audiencia esperados.
- [ ] Duracao do token.
- [ ] Semantica de `401` e `403`.
- [ ] Roles e permissions do RBAC.
- [ ] Tratamento de usuario inativo ou inexistente.
- [ ] Mecanismo que impede acesso direto aos pods sem passar pelo Gateway.
