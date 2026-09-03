# Backlog tecnico

## P0 - Fundacao e seguranca

- [x] Criar o repositorio da aplicacao principal.
- [x] Migrar o codigo legado preservando o historico Git.
- [x] Adicionar CI inicial para testes e build da imagem.
- [ ] Configurar protecao de `main` e `develop` no GitHub.
- [ ] Adicionar os integrantes e o usuario `soat-architecture`.
- [ ] Validar com Marcelo o contrato do Lambda Authorizer.
- [x] Criar `AuthenticatedUser` e `AuthenticatedUserProvider`.
- [x] Criar provider local para desenvolvimento independente.
- [x] Criar provider para o contexto confiavel do API Gateway.
- [ ] Remover login, emissao de JWT e bootstrap administrativo da aplicacao.
- [ ] Remover dependencia da tabela `admin_users` do dominio da aplicacao.
- [ ] Aplicar roles e permissions aos endpoints protegidos.
- [ ] Criar testes de autenticacao e autorizacao com filtros ativos.

## P1 - Operacao e observabilidade

- [ ] Propagar ou gerar correlation ID.
- [ ] Produzir logs estruturados em JSON.
- [ ] Expor metricas de requisicao, latencia e erros.
- [ ] Expor metricas do fluxo de ordens de servico.
- [ ] Definir como medir tempo medio por status.
- [ ] Implementar liveness e readiness.
- [ ] Integrar com a ferramenta de observabilidade escolhida.
- [ ] Ajustar forwarded headers, CORS e base path do API Gateway.

## P1 - Entrega

- [ ] Executar a imagem como usuario sem privilegios.
- [ ] Publicar a imagem no registry definido pela infraestrutura.
- [ ] Automatizar deploy de homologacao e producao.
- [ ] Atualizar OpenAPI para o fluxo de autenticacao externo.
- [ ] Atualizar o README com URLs e instrucoes de deploy.
- [ ] Criar diagrama especifico deste repositorio.
- [ ] Registrar ADRs das decisoes permanentes.
- [ ] Reunir evidencias para PDF e video.
