# Dependencias do Projeto

Fonte: `pom.xml`

## Stack Base

- Java: `25`
- Spring Boot Parent: `org.springframework.boot:spring-boot-starter-parent:4.0.5`
- Build: Maven

## Dependencias de Aplicacao

| Dependencia | Escopo | Finalidade |
| --- | --- | --- |
| `org.springframework.boot:spring-boot-starter-data-jpa` | compile | Persistencia com JPA e integracao com Spring Data. |
| `org.springframework.boot:spring-boot-starter-flyway` | compile | Versionamento e execucao de migracoes de banco. |
| `org.springframework.boot:spring-boot-starter-validation` | compile | Validacao de entrada com Bean Validation. |
| `org.springframework.boot:spring-boot-starter-webmvc` | compile | API REST e camada web baseada em Spring MVC. |
| `org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2` | compile | Geracao de documentacao OpenAPI e interface Swagger UI. |
| `org.projectlombok:lombok` | optional | Reducao de boilerplate em classes Java. |

## Dependencias de Teste

| Dependencia | Escopo | Finalidade |
| --- | --- | --- |
| `org.springframework.boot:spring-boot-starter-data-jpa-test` | test | Suporte a testes de persistencia e JPA. |
| `org.springframework.boot:spring-boot-starter-flyway-test` | test | Apoio a testes envolvendo migracoes Flyway. |
| `org.springframework.boot:spring-boot-starter-restdocs` | test | Geracao de documentacao de API a partir de testes. |
| `org.springframework.boot:spring-boot-starter-validation-test` | test | Utilitarios para testar validacoes. |
| `org.springframework.boot:spring-boot-starter-webmvc-test` | test | Testes da camada web com suporte Spring MVC. |
| `org.springframework.restdocs:spring-restdocs-mockmvc` | test | Integracao entre Spring REST Docs e MockMvc. |

## Plugins de Build Relevantes

| Plugin | Finalidade |
| --- | --- |
| `org.asciidoctor:asciidoctor-maven-plugin:2.2.1` | Gera documentacao AsciiDoc em HTML no ciclo de build. |
| `org.springframework.boot:spring-boot-maven-plugin` | Empacotamento e suporte ao ciclo de build do Spring Boot. |
| `org.apache.maven.plugins:maven-compiler-plugin` | Compilacao do projeto com annotation processing do Lombok. |

## Observacoes

- O projeto ja esta configurado para Java 25.
- Lombok tambem esta configurado como annotation processor no `maven-compiler-plugin`.
- A documentacao REST pode ser evoluida com Spring REST Docs e Asciidoctor conforme os testes forem crescendo.
