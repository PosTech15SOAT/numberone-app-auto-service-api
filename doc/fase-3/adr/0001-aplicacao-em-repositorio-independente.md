# ADR-0001 - Aplicacao principal em repositorio independente

- Status: aceito
- Data: 2026-08-09

## Contexto

A Fase 3 exige quatro repositorios independentes para aplicacao, autenticacao serverless, infraestrutura Kubernetes e infraestrutura do banco gerenciado.

## Decisao

A aplicacao principal sera mantida no repositorio `numberone-auto-service-api`. O historico da aplicacao desenvolvida nas fases anteriores sera preservado como base da evolucao.

Manifestos e Terraform de infraestrutura pertencerao aos repositorios especificos. Este repositorio manterá apenas o codigo da aplicacao, testes, Dockerfile, pipeline da aplicacao e documentacao correspondente.

## Consequencias

- A aplicacao podera evoluir e ser implantada independentemente da infraestrutura.
- Contratos com autenticacao, banco e Kubernetes precisam ser documentados.
- Alteracoes coordenadas entre repositorios exigirao versionamento e compatibilidade explicitos.
- O pipeline deste repositorio sera responsavel por testar, empacotar e publicar a imagem da aplicacao.
