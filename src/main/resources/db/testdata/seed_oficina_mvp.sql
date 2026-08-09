-- Seed ficticio para ambiente local / desenvolvimento
-- Cenário: oficina mecânica com clientes, veículos, serviços automotivos
-- e insumos / peças mais comuns para uso em testes manuais e demos.
--
-- O script usa IDs fixos para facilitar referência em APIs e testes manuais.
-- Pode ser executado mais de uma vez, pois remove previamente os registros
-- incluídos por este próprio seed.

BEGIN;

-- =========================================================
-- LIMPEZA CONTROLADA
-- =========================================================

DELETE FROM movimentacao_estoque
WHERE id IN (
    'a1000000-0000-0000-0000-000000000001',
    'a1000000-0000-0000-0000-000000000002',
    'a1000000-0000-0000-0000-000000000003',
    'a1000000-0000-0000-0000-000000000004',
    'a1000000-0000-0000-0000-000000000005',
    'a1000000-0000-0000-0000-000000000006',
    'a1000000-0000-0000-0000-000000000007',
    'a1000000-0000-0000-0000-000000000008',
    'a1000000-0000-0000-0000-000000000009',
    'a1000000-0000-0000-0000-000000000010',
    'a1000000-0000-0000-0000-000000000011',
    'a1000000-0000-0000-0000-000000000012'
);

DELETE FROM veiculo
WHERE id IN (
    'b1000000-0000-0000-0000-000000000001',
    'b1000000-0000-0000-0000-000000000002',
    'b1000000-0000-0000-0000-000000000003',
    'b1000000-0000-0000-0000-000000000004',
    'b1000000-0000-0000-0000-000000000005',
    'b1000000-0000-0000-0000-000000000006',
    'b1000000-0000-0000-0000-000000000007'
);

DELETE FROM item_estoque
WHERE id IN (
    'c1000000-0000-0000-0000-000000000001',
    'c1000000-0000-0000-0000-000000000002',
    'c1000000-0000-0000-0000-000000000003',
    'c1000000-0000-0000-0000-000000000004',
    'c1000000-0000-0000-0000-000000000005',
    'c1000000-0000-0000-0000-000000000006',
    'c1000000-0000-0000-0000-000000000007',
    'c1000000-0000-0000-0000-000000000008',
    'c1000000-0000-0000-0000-000000000009',
    'c1000000-0000-0000-0000-000000000010',
    'c1000000-0000-0000-0000-000000000011',
    'c1000000-0000-0000-0000-000000000012'
);

DELETE FROM servico_automotivo
WHERE id IN (
    'd1000000-0000-0000-0000-000000000001',
    'd1000000-0000-0000-0000-000000000002',
    'd1000000-0000-0000-0000-000000000003',
    'd1000000-0000-0000-0000-000000000004',
    'd1000000-0000-0000-0000-000000000005',
    'd1000000-0000-0000-0000-000000000006',
    'd1000000-0000-0000-0000-000000000007',
    'd1000000-0000-0000-0000-000000000008',
    'd1000000-0000-0000-0000-000000000009',
    'd1000000-0000-0000-0000-000000000010'
);

DELETE FROM cliente
WHERE id IN (
    'e1000000-0000-0000-0000-000000000001',
    'e1000000-0000-0000-0000-000000000002',
    'e1000000-0000-0000-0000-000000000003',
    'e1000000-0000-0000-0000-000000000004',
    'e1000000-0000-0000-0000-000000000005',
    'e1000000-0000-0000-0000-000000000006'
);

-- =========================================================
-- CLIENTES
-- =========================================================

INSERT INTO cliente (
    id,
    nome,
    documento,
    tipo_documento,
    email,
    telefone,
    endereco,
    ativo,
    created_at,
    updated_at
) VALUES
(
    'e1000000-0000-0000-0000-000000000001',
    'Carlos Henrique Souza',
    '12345678901',
    'PESSOA_FISICA',
    'carlos.souza@email.com',
    '11987654321',
    'Rua das Oficinas, 120 - Sao Paulo/SP',
    TRUE,
    NOW(),
    NOW()
),
(
    'e1000000-0000-0000-0000-000000000002',
    'Fernanda Lima Rocha',
    '23456789012',
    'PESSOA_FISICA',
    'fernanda.rocha@email.com',
    '11987654322',
    'Av. do Motor, 455 - Sao Paulo/SP',
    TRUE,
    NOW(),
    NOW()
),
(
    'e1000000-0000-0000-0000-000000000003',
    'Ricardo Oliveira Santos',
    '34567890123',
    'PESSOA_FISICA',
    'ricardo.santos@email.com',
    '11987654323',
    'Rua da Revisao, 88 - Santo Andre/SP',
    TRUE,
    NOW(),
    NOW()
),
(
    'e1000000-0000-0000-0000-000000000004',
    'Mariana Alves Costa',
    '45678901234',
    'PESSOA_FISICA',
    'mariana.costa@email.com',
    '11987654324',
    'Rua dos Freios, 210 - Sao Bernardo do Campo/SP',
    TRUE,
    NOW(),
    NOW()
),
(
    'e1000000-0000-0000-0000-000000000005',
    'Eduardo Martins Pereira',
    '56789012345',
    'PESSOA_FISICA',
    'eduardo.pereira@email.com',
    '11987654325',
    'Av. dos Pneus, 980 - Sao Caetano do Sul/SP',
    TRUE,
    NOW(),
    NOW()
),
(
    'e1000000-0000-0000-0000-000000000006',
    'Litoral Entregas Ltda',
    '11222333000199',
    'PESSOA_JURIDICA',
    'frota@litoralentregas.com.br',
    '1133334455',
    'Rod. Anchieta, Km 18 - Sao Paulo/SP',
    TRUE,
    NOW(),
    NOW()
);

-- =========================================================
-- VEICULOS
-- =========================================================

INSERT INTO veiculo (
    id,
    id_cliente,
    placa,
    marca,
    modelo,
    ano,
    created_at,
    updated_at
) VALUES
(
    'b1000000-0000-0000-0000-000000000001',
    'e1000000-0000-0000-0000-000000000001',
    'BRA2E19',
    'Volkswagen',
    'Gol 1.6 MSI',
    2020,
    NOW(),
    NOW()
),
(
    'b1000000-0000-0000-0000-000000000002',
    'e1000000-0000-0000-0000-000000000002',
    'QWE4R56',
    'Chevrolet',
    'Onix LT 1.0 Turbo',
    2021,
    NOW(),
    NOW()
),
(
    'b1000000-0000-0000-0000-000000000003',
    'e1000000-0000-0000-0000-000000000003',
    'HJK7L89',
    'Hyundai',
    'HB20 Comfort 1.0',
    2019,
    NOW(),
    NOW()
),
(
    'b1000000-0000-0000-0000-000000000004',
    'e1000000-0000-0000-0000-000000000004',
    'ZXC1V23',
    'Toyota',
    'Corolla GLi 2.0',
    2018,
    NOW(),
    NOW()
),
(
    'b1000000-0000-0000-0000-000000000005',
    'e1000000-0000-0000-0000-000000000005',
    'MNO5P67',
    'Honda',
    'Civic EX 2.0',
    2017,
    NOW(),
    NOW()
),
(
    'b1000000-0000-0000-0000-000000000006',
    'e1000000-0000-0000-0000-000000000006',
    'LOG1S24',
    'Fiat',
    'Fiorino 1.4',
    2022,
    NOW(),
    NOW()
),
(
    'b1000000-0000-0000-0000-000000000007',
    'e1000000-0000-0000-0000-000000000006',
    'ENT9R88',
    'Renault',
    'Kwid Zen 1.0',
    2023,
    NOW(),
    NOW()
);

-- =========================================================
-- SERVICOS AUTOMOTIVOS
-- =========================================================

INSERT INTO servico_automotivo (
    id,
    codigo,
    nome,
    descricao,
    tipo_servico,
    valor_base,
    tempo_estimado_minutos,
    ativo,
    created_at,
    updated_at
) VALUES
(
    'd1000000-0000-0000-0000-000000000001',
    'SRV-001',
    'Troca de oleo e filtro',
    'Substituicao do oleo do motor e filtro de oleo.',
    'PREVENTIVO',
    180.00,
    60,
    TRUE,
    NOW(),
    NOW()
),
(
    'd1000000-0000-0000-0000-000000000002',
    'SRV-002',
    'Alinhamento e balanceamento',
    'Servico de alinhamento de direcao e balanceamento das rodas.',
    'ALINHAMENTO_BALANCEAMENTO',
    160.00,
    50,
    TRUE,
    NOW(),
    NOW()
),
(
    'd1000000-0000-0000-0000-000000000003',
    'SRV-003',
    'Revisao preventiva 10.000 km',
    'Checklist geral com inspecao dos principais sistemas do veiculo.',
    'REVISAO',
    420.00,
    180,
    TRUE,
    NOW(),
    NOW()
),
(
    'd1000000-0000-0000-0000-000000000004',
    'SRV-004',
    'Troca de pastilhas de freio dianteiras',
    'Substituicao das pastilhas dianteiras e verificacao do sistema.',
    'CORRETIVO',
    280.00,
    90,
    TRUE,
    NOW(),
    NOW()
),
(
    'd1000000-0000-0000-0000-000000000005',
    'SRV-005',
    'Diagnostico de injecao eletronica',
    'Leitura de falhas, testes basicos e analise de funcionamento.',
    'DIAGNOSTICO',
    220.00,
    80,
    TRUE,
    NOW(),
    NOW()
),
(
    'd1000000-0000-0000-0000-000000000006',
    'SRV-006',
    'Troca de bateria',
    'Substituicao de bateria e testes do sistema de carga.',
    'INSTALACAO',
    90.00,
    25,
    TRUE,
    NOW(),
    NOW()
),
(
    'd1000000-0000-0000-0000-000000000007',
    'SRV-007',
    'Higienizacao do ar-condicionado',
    'Limpeza do sistema e troca do filtro de cabine quando aplicavel.',
    'PREVENTIVO',
    150.00,
    45,
    TRUE,
    NOW(),
    NOW()
),
(
    'd1000000-0000-0000-0000-000000000008',
    'SRV-008',
    'Troca de correia de acessorios',
    'Substituicao da correia e verificacao dos componentes auxiliares.',
    'CORRETIVO',
    240.00,
    90,
    TRUE,
    NOW(),
    NOW()
),
(
    'd1000000-0000-0000-0000-000000000009',
    'SRV-009',
    'Limpeza de bicos injetores',
    'Procedimento de limpeza e avaliacao do conjunto de injecao.',
    'CORRETIVO',
    260.00,
    90,
    TRUE,
    NOW(),
    NOW()
),
(
    'd1000000-0000-0000-0000-000000000010',
    'SRV-010',
    'Troca de amortecedores dianteiros',
    'Substituicao dos amortecedores dianteiros e reaperto geral.',
    'CORRETIVO',
    650.00,
    240,
    TRUE,
    NOW(),
    NOW()
);

-- =========================================================
-- ITENS DE ESTOQUE / INSUMOS COMUNS
-- =========================================================

INSERT INTO item_estoque (
    id,
    codigo,
    nome,
    descricao,
    tipo_item,
    unidade_medida,
    quantidade_estoque,
    custo_unitario,
    preco_venda,
    estoque_minimo,
    marca,
    veiculo_aplicavel,
    ativo,
    created_at,
    updated_at
) VALUES
(
    'c1000000-0000-0000-0000-000000000001',
    'EST-001',
    'Filtro de oleo',
    'Filtro de oleo de aplicacao universal para uso em revisoes.',
    'PECA',
    'UNIDADE',
    24,
    18.90,
    34.90,
    4,
    'Bosch',
    'Hatchs e sedans compactos',
    TRUE,
    NOW(),
    NOW()
),
(
    'c1000000-0000-0000-0000-000000000002',
    'EST-002',
    'Oleo 5W30 1L',
    'Lubrificante sintetico 5W30 para motor flex.',
    'LUBRIFICANTE',
    'LITRO',
    60,
    24.50,
    44.90,
    10,
    'Mobil',
    'Motores flex leves',
    TRUE,
    NOW(),
    NOW()
),
(
    'c1000000-0000-0000-0000-000000000003',
    'EST-003',
    'Filtro de ar do motor',
    'Elemento filtrante para manutencao preventiva.',
    'PECA',
    'UNIDADE',
    18,
    22.00,
    42.00,
    4,
    'Tecfil',
    'Compactos nacionais',
    TRUE,
    NOW(),
    NOW()
),
(
    'c1000000-0000-0000-0000-000000000004',
    'EST-004',
    'Filtro de cabine',
    'Filtro do ar-condicionado / cabine.',
    'PECA',
    'UNIDADE',
    18,
    19.00,
    36.00,
    4,
    'Mann',
    'Uso urbano leve',
    TRUE,
    NOW(),
    NOW()
),
(
    'c1000000-0000-0000-0000-000000000005',
    'EST-005',
    'Jogo de pastilhas de freio dianteiras',
    'Kit com pastilhas dianteiras.',
    'PECA',
    'CAIXA',
    12,
    95.00,
    165.00,
    3,
    'Cobreq',
    'Sedans e hatchs compactos',
    TRUE,
    NOW(),
    NOW()
),
(
    'c1000000-0000-0000-0000-000000000006',
    'EST-006',
    'Fluido de freio DOT4 500ml',
    'Fluido para manutencao do sistema de freios.',
    'INSUMO',
    'MILILITRO',
    30,
    11.50,
    24.90,
    6,
    'TRW',
    'Universal',
    TRUE,
    NOW(),
    NOW()
),
(
    'c1000000-0000-0000-0000-000000000007',
    'EST-007',
    'Bateria 60Ah',
    'Bateria automotiva selada 12V 60Ah.',
    'PECA',
    'UNIDADE',
    8,
    290.00,
    420.00,
    2,
    'Moura',
    'Veiculos leves',
    TRUE,
    NOW(),
    NOW()
),
(
    'c1000000-0000-0000-0000-000000000008',
    'EST-008',
    'Jogo de velas de ignicao',
    'Conjunto para manutencao preventiva.',
    'PECA',
    'CAIXA',
    14,
    55.00,
    96.00,
    3,
    'NGK',
    'Motores 1.0 e 1.6',
    TRUE,
    NOW(),
    NOW()
),
(
    'c1000000-0000-0000-0000-000000000009',
    'EST-009',
    'Correia de acessorios',
    'Correia poli-V para sistemas auxiliares.',
    'PECA',
    'UNIDADE',
    10,
    48.00,
    89.90,
    2,
    'Continental',
    'Motores leves',
    TRUE,
    NOW(),
    NOW()
),
(
    'c1000000-0000-0000-0000-000000000010',
    'EST-010',
    'Aditivo para radiador 1L',
    'Aditivo concentrado para sistema de arrefecimento.',
    'INSUMO',
    'LITRO',
    20,
    17.00,
    34.90,
    4,
    'Paraflu',
    'Universal',
    TRUE,
    NOW(),
    NOW()
),
(
    'c1000000-0000-0000-0000-000000000011',
    'EST-011',
    'Descarbonizante spray',
    'Produto para limpeza de admissao e corpo de borboleta.',
    'INSUMO',
    'UNIDADE',
    15,
    16.50,
    32.90,
    3,
    'Wurth',
    'Universal',
    TRUE,
    NOW(),
    NOW()
),
(
    'c1000000-0000-0000-0000-000000000012',
    'EST-012',
    'Par de amortecedores dianteiros',
    'Conjunto de amortecedores dianteiros.',
    'PECA',
    'CAIXA',
    6,
    340.00,
    560.00,
    1,
    'Cofap',
    'Veiculos leves',
    TRUE,
    NOW(),
    NOW()
);

-- =========================================================
-- MOVIMENTACOES INICIAIS DE ESTOQUE
-- =========================================================

INSERT INTO movimentacao_estoque (
    id,
    id_item_estoque,
    tipo_movimentacao,
    origem_movimentacao,
    referencia_origem_id,
    quantidade_antes,
    quantidade_depois,
    observacao,
    usuario_responsavel_id,
    created_at
) VALUES
(
    'a1000000-0000-0000-0000-000000000001',
    'c1000000-0000-0000-0000-000000000001',
    'ENTRADA',
    'COMPRA',
    NULL,
    0,
    24,
    'Carga inicial de filtro de oleo para seed de oficina.',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    NOW()
),
(
    'a1000000-0000-0000-0000-000000000002',
    'c1000000-0000-0000-0000-000000000002',
    'ENTRADA',
    'COMPRA',
    NULL,
    0,
    60,
    'Carga inicial de oleo 5W30 para revisoes e trocas.',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    NOW()
),
(
    'a1000000-0000-0000-0000-000000000003',
    'c1000000-0000-0000-0000-000000000003',
    'ENTRADA',
    'COMPRA',
    NULL,
    0,
    18,
    'Carga inicial de filtro de ar.',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    NOW()
),
(
    'a1000000-0000-0000-0000-000000000004',
    'c1000000-0000-0000-0000-000000000004',
    'ENTRADA',
    'COMPRA',
    NULL,
    0,
    18,
    'Carga inicial de filtro de cabine.',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    NOW()
),
(
    'a1000000-0000-0000-0000-000000000005',
    'c1000000-0000-0000-0000-000000000005',
    'ENTRADA',
    'COMPRA',
    NULL,
    0,
    12,
    'Carga inicial de jogo de pastilhas dianteiras.',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    NOW()
),
(
    'a1000000-0000-0000-0000-000000000006',
    'c1000000-0000-0000-0000-000000000006',
    'ENTRADA',
    'COMPRA',
    NULL,
    0,
    30,
    'Carga inicial de fluido de freio.',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    NOW()
),
(
    'a1000000-0000-0000-0000-000000000007',
    'c1000000-0000-0000-0000-000000000007',
    'ENTRADA',
    'COMPRA',
    NULL,
    0,
    8,
    'Carga inicial de baterias 60Ah.',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    NOW()
),
(
    'a1000000-0000-0000-0000-000000000008',
    'c1000000-0000-0000-0000-000000000008',
    'ENTRADA',
    'COMPRA',
    NULL,
    0,
    14,
    'Carga inicial de jogo de velas.',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    NOW()
),
(
    'a1000000-0000-0000-0000-000000000009',
    'c1000000-0000-0000-0000-000000000009',
    'ENTRADA',
    'COMPRA',
    NULL,
    0,
    10,
    'Carga inicial de correias de acessorios.',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    NOW()
),
(
    'a1000000-0000-0000-0000-000000000010',
    'c1000000-0000-0000-0000-000000000010',
    'ENTRADA',
    'COMPRA',
    NULL,
    0,
    20,
    'Carga inicial de aditivo para radiador.',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    NOW()
),
(
    'a1000000-0000-0000-0000-000000000011',
    'c1000000-0000-0000-0000-000000000011',
    'ENTRADA',
    'COMPRA',
    NULL,
    0,
    15,
    'Carga inicial de descarbonizante spray.',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    NOW()
),
(
    'a1000000-0000-0000-0000-000000000012',
    'c1000000-0000-0000-0000-000000000012',
    'ENTRADA',
    'COMPRA',
    NULL,
    0,
    6,
    'Carga inicial de amortecedores dianteiros.',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    NOW()
);

COMMIT;
