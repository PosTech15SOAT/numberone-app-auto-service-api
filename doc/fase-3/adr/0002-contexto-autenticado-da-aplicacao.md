# ADR 0002 — Contexto autenticado da aplicação

- Status: Aceito
- Data: 2026-09-13

## Contexto

Na arquitetura integrada, a autenticação ocorre antes de a requisição alcançar a Auto Service API. A aplicação precisa proteger rotas e aplicar regras de autorização sem assumir a responsabilidade de login ou de emissão de credenciais.

## Decisão

Delegar autenticação ao API Gateway e ao Lambda Authorizer. A Auto Service API não realiza login e não emite JWT. Ela recebe o contexto autenticado confiável pelos headers `X-Authenticated-*`, converte-o para o contexto usado pelo Spring Security e aplica roles e permissions na proteção das rotas.

As regras contextuais e de domínio que dependem da identidade recebida continuam pertencendo à aplicação.

## Consequências

- Endpoints administrativos exigem o papel `ADMIN` e fluxos específicos usam as permissões recebidas no contexto autenticado.
- O contrato de headers é parte da integração entre a borda de autenticação e a aplicação.
- A aplicação não registra JWT, secrets ou CPF completo em logs.
