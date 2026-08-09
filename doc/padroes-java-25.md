# Padroes de Codigo Java 25

## Objetivo

Definir um conjunto pequeno e objetivo de convencoes para manter o codigo legivel, consistente e facil de evoluir em Java 25 com Spring Boot 4.

## Diretrizes Gerais

- Escrever codigo simples antes de escrever codigo "esperto".
- Preferir nomes claros e comportamento previsivel.
- Evitar duplicacao de regra de negocio.
- Nao retornar `null` para colecoes, DTOs opcionais ou resultados consultaveis.
- Manter regra de negocio fora de controllers e fora de repositories.

## Estrutura de Pacotes

Preferir organizacao por feature, nao por camada global. Exemplo:

```text
br.com.fiap.numberone
  cliente
    ClienteController
    ClienteService
    ClienteRepository
    ClienteRequest
    ClienteResponse
  pedido
    PedidoController
    PedidoService
    PedidoRepository
```

Quando uma feature crescer, separar internamente em subpastas como `api`, `application`, `domain` e `infrastructure`.

## Convencoes de Nomes

- Classes: `PascalCase`
- Metodos e variaveis: `camelCase`
- Constantes: `UPPER_SNAKE_CASE`
- Pacotes: minusculos e sem plural desnecessario quando nao agregar clareza
- Sufixos recomendados:
  - `Controller` para entrada HTTP
  - `Service` para regra de negocio e casos de uso
  - `Repository` para persistencia
  - `Request` e `Response` para contratos HTTP
  - `Mapper` quando a conversao nao for trivial

## Uso de Recursos do Java 25

- Usar `record` para DTOs imutaveis de entrada e saida.
- Nao usar `record` para entidades JPA.
- Usar `switch` expression quando melhorar legibilidade.
- Usar pattern matching com `instanceof` quando evitar cast manual.
- Preferir tipagem explicita em variaveis locais para manter a leitura clara.
- Preferir `List.of`, `Set.of` e `Map.of` para colecoes fixas e imutaveis.
- Considerar `sealed` classes apenas quando o dominio tiver hierarquia fechada e isso trouxer valor real.

## Spring Boot

- Usar injecao por construtor. Nao usar field injection com `@Autowired`.
- Controllers devem apenas:
  - receber a requisicao
  - validar entrada
  - delegar para o servico
  - devolver resposta HTTP
- Services concentram a regra de negocio.
- Repositories devem ficar restritos a acesso a dados.
- DTO HTTP nao deve vazar entidade JPA diretamente.
- Validacoes de entrada devem usar Bean Validation (`jakarta.validation`).
- Excecoes devem ser tratadas de forma centralizada, preferencialmente com `@RestControllerAdvice`.

## JPA e Persistencia

- Entidades devem representar o dominio persistido e nao a API publica.
- Evitar logica complexa em entidades anotadas com JPA.
- Usar transacao no servico, nao no controller.
- Evitar carregar relacionamentos sem necessidade.
- Modelar `equals` e `hashCode` com cuidado em entidades persistentes.

## Lombok

Lombok esta no projeto e pode ser usado com moderacao.

- Aceitavel para reduzir boilerplate em classes simples.
- Evitar Lombok quando esconder regra importante do dominio.
- Nao usar `@Data` automaticamente em tudo.
- Preferir combinacoes explicitas como `@Getter`, `@Setter`, `@Builder` e `@RequiredArgsConstructor` quando fizer sentido.

## Tratamento de Erros

- Criar excecoes de negocio com nome claro.
- Nao devolver stack trace para o cliente.
- Padronizar payload de erro para APIs REST.
- Logar erros tecnicos com contexto suficiente para diagnostico.

## Testes

- Nome do teste deve descrever comportamento.
- Cobrir casos felizes, validacoes e erros de negocio.
- Preferir testes pequenos e objetivos.
- Usar testes de camada quando fizer sentido:
  - controller: foco em contrato HTTP
  - service: foco em regra de negocio
  - repository: foco em persistencia e consultas

## Regras de Estilo

- Um tipo publico por arquivo.
- Metodos curtos e com responsabilidade unica.
- Evitar mais de um nivel forte de identacao quando possivel.
- Comentar apenas quando a intencao nao estiver clara no proprio codigo.
- Preferir early return para reduzir blocos aninhados.
- Usar `Optional` somente como retorno, nao como atributo de entidade ou parametro de metodo.

## Checklist Antes de Commitar

- O nome das classes e metodos comunica a intencao?
- Existe separacao clara entre API, regra de negocio e persistencia?
- DTOs e entidades estao separados?
- Validacoes de entrada foram aplicadas?
- O codigo novo esta coberto por teste adequado?
- O trecho novo segue o padrao definido nesta pasta?
