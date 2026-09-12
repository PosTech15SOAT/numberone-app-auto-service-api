-- Massa academica minima e deterministica para demonstracao integrada API + Auth.
-- CPF de teste para POST /auth/login: 52998224725
-- Nao ha senha: o login atual usa somente CPF.

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
) VALUES (
    'f1000000-0000-0000-0000-000000000001',
    'Cliente Demo Academico',
    '52998224725',
    'PESSOA_FISICA',
    'cliente.demo.academico@example.com',
    '11999990001',
    'Rua Demo Academica, 100 - Sao Paulo/SP',
    TRUE,
    NOW(),
    NOW()
) ON CONFLICT (id) DO NOTHING;

INSERT INTO auth_usuario (
    id,
    cliente_id,
    cpf,
    nome,
    email,
    ativo,
    created_at,
    updated_at
) VALUES (
    'f2000000-0000-0000-0000-000000000001',
    'f1000000-0000-0000-0000-000000000001',
    '52998224725',
    'Cliente Demo Academico',
    'cliente.demo.academico@example.com',
    TRUE,
    NOW(),
    NOW()
) ON CONFLICT (cpf) DO UPDATE SET
    cliente_id = EXCLUDED.cliente_id,
    nome = EXCLUDED.nome,
    email = EXCLUDED.email,
    ativo = EXCLUDED.ativo,
    updated_at = NOW();

INSERT INTO auth_usuario_perfil (usuario_id, perfil_id)
SELECT 'f2000000-0000-0000-0000-000000000001', perfil.id
  FROM auth_perfil perfil
 WHERE perfil.nome = 'CUSTOMER'
ON CONFLICT (usuario_id, perfil_id) DO NOTHING;

INSERT INTO veiculo (
    id,
    id_cliente,
    placa,
    marca,
    modelo,
    ano,
    created_at,
    updated_at
) VALUES (
    'f3000000-0000-0000-0000-000000000001',
    'f1000000-0000-0000-0000-000000000001',
    'DEM0A25',
    'Fiat',
    'Argo Drive 1.0',
    2025,
    NOW(),
    NOW()
) ON CONFLICT (id) DO NOTHING;

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
) VALUES (
    'f4000000-0000-0000-0000-000000000001',
    'DEMO-SRV-001',
    'Revisao academica basica',
    'Servico minimo para demonstracao academica do fluxo de oficina.',
    'REVISAO',
    250.00,
    120,
    TRUE,
    NOW(),
    NOW()
) ON CONFLICT (id) DO NOTHING;

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
) VALUES (
    'f5000000-0000-0000-0000-000000000001',
    'DEMO-EST-001',
    'Filtro de oleo demo',
    'Item minimo para testes de estoque e uso em ordem de servico.',
    'PECA',
    'UNIDADE',
    10,
    20.00,
    39.90,
    2,
    'Demo Parts',
    'Veiculos leves',
    TRUE,
    NOW(),
    NOW()
) ON CONFLICT (id) DO NOTHING;

INSERT INTO movimentacao_estoque (
    id,
    id_item_estoque,
    tipo_movimentacao,
    origem_movimentacao,
    referencia_origem_id,
    quantidade_antes,
    quantidade_depois,
    motivo,
    observacao,
    usuario_responsavel_id,
    created_at
) VALUES (
    'f6000000-0000-0000-0000-000000000001',
    'f5000000-0000-0000-0000-000000000001',
    'ENTRADA',
    'MANUAL',
    NULL,
    0,
    10,
    'Carga inicial academica',
    'Movimentacao deterministica criada pela migration V5.',
    'f2000000-0000-0000-0000-000000000001',
    NOW()
) ON CONFLICT (id) DO NOTHING;

INSERT INTO ordem_servico (
    id,
    id_cliente,
    id_veiculo,
    descricao_inicial,
    descricao_diagnostico,
    descricao_diagnostico_final,
    observacao,
    status,
    data_hora_entrada,
    data_hora_prevista,
    data_hora_entrega,
    created_at,
    updated_at
) VALUES (
    'f7000000-0000-0000-0000-000000000001',
    'f1000000-0000-0000-0000-000000000001',
    'f3000000-0000-0000-0000-000000000001',
    'Cliente relata necessidade de revisao basica para demonstracao.',
    'Inspecao inicial sem anomalias criticas.',
    NULL,
    'Ordem de servico academica criada pela migration V5.',
    'WAITING_APPROVAL',
    NOW(),
    NOW() + INTERVAL '2 days',
    NULL,
    NOW(),
    NOW()
) ON CONFLICT (id) DO NOTHING;

INSERT INTO ordem_servico_orcamento (
    id,
    id_ordem_servico,
    valor_proposto,
    valor_aprovado,
    status,
    enviado_em,
    aprovado_em,
    created_at,
    updated_at
) VALUES (
    'f8000000-0000-0000-0000-000000000001',
    'f7000000-0000-0000-0000-000000000001',
    289.90,
    NULL,
    'SENT',
    NOW(),
    NULL,
    NOW(),
    NOW()
) ON CONFLICT (id) DO NOTHING;

INSERT INTO ordem_servico_servico (
    id,
    id_ordem_servico,
    id_servico,
    valor,
    status,
    opcional,
    data_hora_inicio,
    data_hora_fim,
    created_at,
    updated_at
) VALUES (
    'f9000000-0000-0000-0000-000000000001',
    'f7000000-0000-0000-0000-000000000001',
    'f4000000-0000-0000-0000-000000000001',
    250.00,
    'PENDING',
    FALSE,
    NULL,
    NULL,
    NOW(),
    NOW()
) ON CONFLICT (id) DO NOTHING;

INSERT INTO ordem_servico_servico_item (
    id,
    id_ordem_servico_servico,
    id_item_estoque,
    quantidade_usada,
    created_at,
    updated_at
) VALUES (
    'fa000000-0000-0000-0000-000000000001',
    'f9000000-0000-0000-0000-000000000001',
    'f5000000-0000-0000-0000-000000000001',
    1,
    NOW(),
    NOW()
) ON CONFLICT (id) DO NOTHING;
