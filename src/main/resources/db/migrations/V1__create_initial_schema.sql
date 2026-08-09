CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE admin_users (
    id uuid NOT NULL,
    username varchar(100) NOT NULL,
    password_hash varchar(255) NOT NULL,
    role varchar(50) NOT NULL,
    enabled boolean NOT NULL DEFAULT true,
    created_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT admin_users_pkey PRIMARY KEY (id),
    CONSTRAINT admin_users_username_key UNIQUE (username)
);

CREATE TABLE cliente (
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    nome varchar(90) NOT NULL,
    documento varchar(50) NOT NULL,
    tipo_documento varchar(30) NOT NULL,
    email varchar(120) NOT NULL,
    telefone varchar(15) NOT NULL,
    endereco varchar(90) NOT NULL,
    ativo boolean DEFAULT true,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    CONSTRAINT cliente_pkey PRIMARY KEY (id)
);

CREATE TABLE veiculo (
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    id_cliente uuid,
    placa varchar(10),
    marca varchar(100),
    modelo varchar(100),
    ano integer,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    CONSTRAINT veiculo_pkey PRIMARY KEY (id)
);

CREATE TABLE servico_automotivo (
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    codigo varchar(50) NOT NULL,
    nome varchar(150) NOT NULL,
    descricao varchar(255),
    tipo_servico varchar(50),
    valor_base numeric(10,2) NOT NULL,
    tempo_estimado_minutos integer NOT NULL,
    ativo boolean NOT NULL DEFAULT true,
    created_at timestamp without time zone NOT NULL DEFAULT now(),
    updated_at timestamp without time zone,
    CONSTRAINT servico_pkey PRIMARY KEY (id)
);

CREATE TABLE item_estoque (
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    codigo varchar(100),
    nome varchar(100) NOT NULL,
    descricao varchar(255),
    tipo_item varchar(50) NOT NULL,
    unidade_medida varchar(20) NOT NULL,
    quantidade_estoque integer NOT NULL DEFAULT 0,
    custo_unitario numeric(10,2),
    preco_venda numeric(10,2),
    estoque_minimo integer NOT NULL DEFAULT 0,
    marca varchar(100),
    veiculo_aplicavel varchar(255),
    ativo boolean NOT NULL DEFAULT true,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    CONSTRAINT item_pkey PRIMARY KEY (id)
);

CREATE TABLE movimentacao_estoque (
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    id_item_estoque uuid NOT NULL,
    tipo_movimentacao varchar(20) NOT NULL,
    origem_movimentacao varchar(50),
    referencia_origem_id uuid,
    quantidade_antes integer,
    quantidade_depois integer,
    motivo varchar(255),
    observacao text,
    usuario_responsavel_id uuid,
    created_at timestamp without time zone NOT NULL DEFAULT now(),
    CONSTRAINT movimentacao_estoque_pkey PRIMARY KEY (id),
    CONSTRAINT chk_movimentacao_estoque_tipo_movimentacao
        CHECK (tipo_movimentacao IN ('ENTRADA', 'BAIXA', 'AJUSTE'))
);

CREATE TABLE ordem_servico (
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    id_cliente uuid,
    id_veiculo uuid,
    descricao_inicial varchar(255),
    descricao_diagnostico varchar(255),
    descricao_diagnostico_final varchar(255),
    observacao varchar(255),
    status varchar(50),
    data_hora_entrada timestamp without time zone,
    data_hora_prevista timestamp without time zone,
    data_hora_entrega timestamp without time zone,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone,
    CONSTRAINT ordem_servico_pkey PRIMARY KEY (id)
);

CREATE TABLE ordem_servico_orcamento (
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    id_ordem_servico uuid NOT NULL,
    valor_proposto numeric(19,2),
    valor_aprovado numeric(19,2),
    status varchar(30) NOT NULL,
    enviado_em timestamp without time zone,
    aprovado_em timestamp without time zone,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone,
    CONSTRAINT ordem_servico_orcamento_pkey PRIMARY KEY (id)
);

CREATE TABLE ordem_servico_servico (
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    id_ordem_servico uuid NOT NULL,
    id_servico uuid,
    valor numeric(10,2),
    status varchar(50),
    opcional boolean,
    data_hora_inicio timestamp without time zone,
    data_hora_fim timestamp without time zone,
    created_at timestamp without time zone NOT NULL DEFAULT now(),
    updated_at timestamp without time zone,
    CONSTRAINT ordem_servico_servico_pkey PRIMARY KEY (id)
);

CREATE TABLE ordem_servico_servico_item (
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    id_ordem_servico_servico uuid NOT NULL,
    id_item_estoque uuid,
    quantidade_usada bigint,
    created_at timestamp without time zone NOT NULL DEFAULT now(),
    updated_at timestamp without time zone,
    CONSTRAINT ordem_servico_servico_item_pkey PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uk_veiculo_placa_ci
    ON veiculo (lower(placa));

ALTER TABLE veiculo
    ADD CONSTRAINT fk_veiculo_cliente
    FOREIGN KEY (id_cliente)
    REFERENCES cliente(id);

ALTER TABLE movimentacao_estoque
    ADD CONSTRAINT fk_item
    FOREIGN KEY (id_item_estoque)
    REFERENCES item_estoque(id);

ALTER TABLE ordem_servico
    ADD CONSTRAINT fk_os_cliente
    FOREIGN KEY (id_cliente)
    REFERENCES cliente(id);

ALTER TABLE ordem_servico
    ADD CONSTRAINT fk_os_veiculo
    FOREIGN KEY (id_veiculo)
    REFERENCES veiculo(id);

ALTER TABLE ordem_servico_orcamento
    ADD CONSTRAINT fk_orcamento_ordem_servico
    FOREIGN KEY (id_ordem_servico)
    REFERENCES ordem_servico(id);

ALTER TABLE ordem_servico_servico
    ADD CONSTRAINT fk_ordem_servico_servico_ordem_servico
    FOREIGN KEY (id_ordem_servico)
    REFERENCES ordem_servico(id);

ALTER TABLE ordem_servico_servico
    ADD CONSTRAINT fk_ordem_servico_servico_servico
    FOREIGN KEY (id_servico)
    REFERENCES servico_automotivo(id);

ALTER TABLE ordem_servico_servico_item
    ADD CONSTRAINT fk_ordem_servico_servico_item_ordem_servico_servico
    FOREIGN KEY (id_ordem_servico_servico)
    REFERENCES ordem_servico_servico(id);

ALTER TABLE ordem_servico_servico_item
    ADD CONSTRAINT fk_ordem_servico_servico_item_item_estoque
    FOREIGN KEY (id_item_estoque)
    REFERENCES item_estoque(id);
