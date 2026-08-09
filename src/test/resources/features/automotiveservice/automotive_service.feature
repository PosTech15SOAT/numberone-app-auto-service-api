Feature: Automotive service management

  Scenario: Create and retrieve an automotive service by id
    Given que existe um servico automotivo valido para cadastro
    When eu cadastro o servico automotivo
    Then o servico automotivo deve ser cadastrado com sucesso
    When eu consulto o servico automotivo cadastrado por id
    Then a consulta deve retornar o servico automotivo cadastrado
