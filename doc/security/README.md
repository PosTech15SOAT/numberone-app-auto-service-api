# Analise de Seguranca com SonarQube

## 1. Objetivo

O objetivo deste fluxo e executar uma analise estatica de codigo no projeto NumberOne para apoiar o entregavel de desenvolvimento seguro do Tech Challenge.

A analise busca identificar:

- vulnerabilidades;
- bugs;
- security hotspots;
- code smells;
- duplicacoes;
- cobertura de testes;
- resultado do quality gate.

## 2. O que e SonarQube

SonarQube e uma plataforma de analise de qualidade e seguranca de codigo. Ele examina o codigo-fonte sem executar a aplicacao, tecnica conhecida como analise estatica de codigo ou SAST.

Ele ajuda a encontrar problemas antes que cheguem em producao ou na entrega final. Mesmo assim, o SonarQube nao substitui testes automatizados, testes manuais ou testes dinamicos de API. Ele complementa essas praticas, aumentando a visibilidade sobre qualidade, manutenibilidade e seguranca.

## 3. O que o SonarQube analisa

- Bugs: problemas que podem causar comportamento incorreto ou falhas em tempo de execucao.
- Vulnerabilities: falhas de seguranca com impacto mais direto e conhecido, que podem ser exploradas em determinadas condicoes.
- Security Hotspots: trechos sensiveis do codigo que precisam de revisao humana para confirmar se ha risco real.
- Code Smells: problemas de manutenibilidade, legibilidade ou complexidade que dificultam evolucao futura.
- Coverage: percentual de codigo coberto por testes automatizados, normalmente enviado pelo JaCoCo.
- Duplications: trechos de codigo duplicado que podem aumentar manutencao e risco de inconsistencias.
- Quality Gate: conjunto de criterios que define se o projeto atende ao nivel minimo esperado de qualidade.

A diferenca principal entre `Vulnerabilities` e `Security Hotspots` e que uma vulnerability ja representa uma possivel falha de seguranca identificada pela regra do SonarQube. Um security hotspot indica um ponto que merece revisao, mas que pode ser aceitavel dependendo do contexto e da implementacao.

## 4. Por que usamos um Docker Compose separado

O arquivo `docker-compose.sonar.yml` fica separado do compose principal porque o SonarQube nao faz parte do runtime da aplicacao NumberOne.

Ele e uma ferramenta auxiliar de qualidade e seguranca, usada localmente ou em pipeline CI/CD. Separar os arquivos evita misturar infraestrutura de analise com a infraestrutura necessaria para executar a aplicacao.

## 5. Dependencias necessarias

Para executar o fluxo completo localmente, a maquina precisa ter Docker, Docker Compose, Java, Maven ou Maven Wrapper e um navegador web.

### 5.1 Docker

O Docker e necessario para subir o SonarQube em container.

Comando para validar instalacao:

```bash
docker --version
```

### 5.2 Docker Compose

O Docker Compose e necessario para subir o arquivo `docker-compose.sonar.yml`.

Comando para validar instalacao:

```bash
docker compose version
```

Use preferencialmente `docker compose`, sem hifen. Em instalacoes antigas, pode existir apenas o comando `docker-compose`.

### 5.3 Java/JDK

O Java precisa estar instalado para compilar o projeto e executar Maven.

Comando para validar:

```bash
java -version
```

A versao do Java deve ser compativel com a versao configurada no projeto Spring Boot.

### 5.4 Maven ou Maven Wrapper

O Maven e usado para rodar testes, gerar cobertura e executar o SonarScanner for Maven.

Comando para validar Maven instalado:

```bash
mvn -version
```

Se o projeto possuir Maven Wrapper, ele tambem pode ser usado:

```bash
./mvnw -version
```

Os scripts devem dar preferencia ao Maven Wrapper `./mvnw` quando ele existir, usando `mvn` como fallback.

### 5.5 Navegador Web

Sera necessario acessar o SonarQube no navegador em:

```text
http://localhost:9000
```

### 5.6 Token do SonarQube

O token e obrigatorio para executar o scan com autenticacao.

Exporte o token como variavel de ambiente:

```bash
export SONAR_TOKEN=seu_token_aqui
```

O token nunca deve ser commitado no Git, porque ele permite autenticar execucoes de analise no SonarQube.

### 5.7 JaCoCo

O JaCoCo gera o relatorio de cobertura de testes que sera enviado para o SonarQube.

Quando configurado corretamente, o Maven normalmente gera o XML de cobertura em:

```text
target/site/jacoco/jacoco.xml
```

O SonarQube usa esse XML para exibir a cobertura de testes no dashboard.

### 5.8 SonarScanner for Maven

Nao e necessario instalar o SonarScanner CLI separadamente quando se usa Maven. O scan pode ser executado pelo goal Maven do Sonar, conhecido como SonarScanner for Maven.

Neste projeto, o scan sera executado por comando Maven.

### 5.9 Linux, Mac ou Git Bash

Os scripts `.sh` foram pensados para Linux, Mac ou Git Bash no Windows.

No Linux/Mac, pode ser necessario conceder permissao de execucao:

```bash
chmod +x scripts/*.sh
```

### 5.10 Recursos minimos da maquina

O SonarQube pode consumir memoria e demorar alguns minutos para subir. Evite rodar muitos containers pesados ao mesmo tempo, aguarde alguns minutos antes de acessar `http://localhost:9000` e, caso a maquina esteja lenta, verifique os logs do container.

## 6. Como subir o SonarQube local

Execute:

```bash
./scripts/sonar-up.sh
```

Se necessario, conceda permissao antes:

```bash
chmod +x scripts/*.sh
```

O SonarQube pode demorar alguns minutos para ficar disponivel.

## 7. Como acessar o SonarQube no navegador

Acesse:

```text
http://localhost:9000
```

## 8. Login inicial

Use:

```text
Usuario: admin
Senha: admin
```

No primeiro acesso, o SonarQube pode solicitar a troca de senha.

## 9. Como trocar a senha no primeiro acesso

Depois de acessar com `admin/admin`, siga a tela apresentada pelo SonarQube, informe a senha atual, escolha uma nova senha e confirme. Guarde a nova senha apenas em local seguro do grupo.

## 10. Como criar o token de analise

1. Acesse o SonarQube.
2. Clique no usuario/avatar no canto superior direito.
3. Entre em `My Account`.
4. Acesse a aba `Security`.
5. Crie um token.
6. Copie o token gerado.
7. Guarde temporariamente para exportar no terminal.

O token aparece apenas uma vez. Ele nao deve ser salvo no repositorio.

## 11. Como exportar o token no terminal

Execute:

```bash
export SONAR_TOKEN=seu_token_aqui
```

Essa variavel vale apenas para a sessao atual do terminal.

## 12. Como executar o scan

Execute:

```bash
./scripts/sonar-scan.sh
```

Esse script deve:

1. validar se o token foi informado;
2. detectar `./mvnw` ou `mvn`;
3. rodar testes;
4. gerar cobertura com JaCoCo;
5. enviar o resultado para o SonarQube.

## 13. Como validar se o scan funcionou

Ao final do comando, deve aparecer no terminal uma mensagem de sucesso do Sonar. O projeto tambem deve aparecer no dashboard do SonarQube.

Acesse:

```text
http://localhost:9000
```

Abra o projeto `numberone`.

## 14. Onde ver os resultados no SonarQube

No projeto `numberone`, observe:

- Overview;
- Quality Gate;
- Issues;
- Security Hotspots;
- Measures;
- Coverage;
- Duplications.

## 15. Quais telas capturar como evidencia

Capture:

- `sonar-dashboard.png`: visao geral do projeto e status principal.
- `sonar-quality-gate.png`: resultado do Quality Gate.
- `sonar-issues.png`: lista de issues identificadas.
- `sonar-security-hotspots.png`: hotspots de seguranca para revisao.
- `sonar-coverage.png`: cobertura de testes enviada pelo JaCoCo.

Essas evidencias ajudam a comprovar que o scan foi executado e que o relatorio foi preenchido com base nos dados reais.

## 16. Onde salvar as evidencias

Salve em:

```text
docs/security/evidencias/
```

As evidencias devem ser versionadas no projeto quando forem usadas para a entrega final.

## 17. Como preparar a pasta de evidencias

Execute:

```bash
./scripts/security-evidence.sh
```

Esse script cria a pasta e mostra a checklist do que capturar.

## 18. Como preencher o relatorio de vulnerabilidades

Existe um template em:

```text
docs/security/relatorio-vulnerabilidades-template.md
```

E o relatorio final em:

```text
docs/security/relatorio-vulnerabilidades.md
```

O relatorio deve ser preenchido com base nos resultados reais do SonarQube. Nao invente numeros. Se nao houver vulnerabilidades criticas, isso deve ser informado de forma transparente.

## 19. Como encerrar o SonarQube

Execute:

```bash
./scripts/sonar-down.sh
```

Os volumes sao preservados por padrao, mantendo dados, historico e configuracoes locais.

Para remover tambem os volumes, use apenas quando quiser limpar tudo:

```bash
docker compose -f docker-compose.sonar.yml down -v
```

## 20. Problemas comuns e solucoes

### SonarQube demora para subir

E normal demorar alguns minutos, principalmente na primeira execucao.

### Porta 9000 ja esta em uso

Outro processo pode estar usando a porta. Pare o processo ou ajuste o mapeamento de porta no `docker-compose.sonar.yml`.

### Token nao informado

Exporte novamente:

```bash
export SONAR_TOKEN=seu_token_aqui
```

### Erro de autenticacao

O token pode estar errado, expirado ou nao exportado no terminal atual. Gere outro token em `My Account > Security` se necessario.

### Erro de conexao com localhost:9000

O SonarQube pode ainda nao estar pronto ou o container pode ter falhado. Aguarde alguns minutos e verifique a situacao do container.

### Falha no Maven ou nos testes antes do scan

O scan depende do projeto compilar e dos testes passarem. Corrija falhas de build ou testes antes de tentar novamente.

### Cobertura nao aparece no Sonar

Pode haver problema na configuracao do JaCoCo ou no caminho do XML. O caminho usado pelo script e `target/site/jacoco/jacoco.xml`.

### Permissao negada ao rodar script

Execute:

```bash
chmod +x scripts/*.sh
```

## 21. Observacao sobre relatorio PDF

O SonarQube Community nao gera um relatorio PDF completo nativamente como recurso principal.

Por isso, o relatorio da entrega sera criado manualmente em Markdown/PDF com base:

- no dashboard do SonarQube;
- nas issues identificadas;
- nos security hotspots;
- nas evidencias salvas no projeto.

## 22. Evolucao futura para CI/CD

O mesmo fluxo pode futuramente ser integrado em uma pipeline CI/CD.

Exemplo conceitual:

- push ou pull request;
- pipeline roda build;
- pipeline roda testes;
- pipeline gera cobertura;
- pipeline executa Sonar;
- pipeline valida Quality Gate.

Mesmo sem CI/CD, o SonarQube local faz sentido porque permite que o grupo gere evidencias, revise problemas e documente a qualidade do codigo de forma reproduzivel.
