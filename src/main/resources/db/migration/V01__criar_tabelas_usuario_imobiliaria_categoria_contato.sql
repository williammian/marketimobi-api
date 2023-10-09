CREATE TABLE contato (
	id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
	nome VARCHAR(50) NOT NULL,
	data_cadastro DATE NOT NULL,
	email VARCHAR(50) NOT NULL,
	telefone VARCHAR(20),
	assunto VARCHAR(50),
	mensagem VARCHAR(500),
	conferido BOOLEAN NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE imobiliaria (
	id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
	nome VARCHAR(50) NOT NULL,
	razao_social VARCHAR(50),
	status VARCHAR(20) NOT NULL,
	data_cadastro DATE NOT NULL,
	telefone_fixo VARCHAR(20),
	telefone_celular VARCHAR(20),
	logradouro VARCHAR(50),
	complemento VARCHAR(30),
	bairro VARCHAR(30),
	cep VARCHAR(10),
	cidade VARCHAR(30),
	estado VARCHAR(30),
	email VARCHAR(50),
	site VARCHAR(50),
	creci VARCHAR(20),
	cnpj VARCHAR(20),
	imagem1 VARCHAR(250),
	imagem2 VARCHAR(250),
	CONSTRAINT uk_imobiliaria_nome UNIQUE (nome)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE usuario (
	id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
	nome VARCHAR(50) NOT NULL,
	sobrenome VARCHAR(50) NOT NULL,
	nome_profissional VARCHAR(50),
	sexo VARCHAR(20) NOT NULL,
	status VARCHAR(20) NOT NULL,
	data_nascimento DATE NOT NULL,
	data_cadastro DATE NOT NULL,
	telefone_fixo VARCHAR(20),
	telefone_celular VARCHAR(20),
	cargo VARCHAR(50),
	creci VARCHAR(20),
	site VARCHAR(50),
	email VARCHAR(50) NOT NULL,
	senha VARCHAR(150) NOT NULL,
	imagem1 VARCHAR(250),
	imagem2 VARCHAR(250),
	id_imobiliaria BIGINT(20) NOT NULL,
	CONSTRAINT uk_usuario_nome UNIQUE (nome),
	CONSTRAINT uk_usuario_email UNIQUE (email),
	FOREIGN KEY (id_imobiliaria) REFERENCES imobiliaria(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE permissao (
	id BIGINT(20) PRIMARY KEY,
	descricao VARCHAR(50) NOT NULL,
	CONSTRAINT uk_permissao_descricao UNIQUE (descricao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE usuario_permissao (
	id_usuario BIGINT(20) NOT NULL,
	id_permissao BIGINT(20) NOT NULL,
	PRIMARY KEY (id_usuario, id_permissao),
	FOREIGN KEY (id_usuario) REFERENCES usuario(id),
	FOREIGN KEY (id_permissao) REFERENCES permissao(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE categoria (
	id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
	codigo VARCHAR(10) NOT NULL,
	nome VARCHAR(30) NOT NULL,
	CONSTRAINT uk_categoria_codigo UNIQUE (codigo),
	CONSTRAINT uk_categoria_nome UNIQUE (nome)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO imobiliaria (nome, status, data_cadastro) 
VALUES ('Market Imobi', 'ATIVO', '2020-01-01');

INSERT INTO permissao (id, descricao) VALUES (1, 'ROLE_ADMINISTRADOR');
INSERT INTO permissao (id, descricao) VALUES (2, 'ROLE_ALTERAR_SENHA');
INSERT INTO permissao (id, descricao) VALUES (3, 'ROLE_MEUS_DADOS');
INSERT INTO permissao (id, descricao) VALUES (4, 'ROLE_MINHA_IMOBILIARIA');
INSERT INTO permissao (id, descricao) VALUES (5, 'ROLE_PORTFOLIO');

INSERT INTO usuario (nome, sobrenome, nome_profissional, sexo, status, data_nascimento, data_cadastro, id_imobiliaria, email, senha) 
VALUES ('Administrador', 'Market Imobi', 'Adminstrador Market Imobi', 'MASCULINO', 'ATIVO', '2020-01-01', '2020-01-01', 1, 
'marketimobi@gmail.com', '$2a$10$/N1WLFfjwPEiFp.wD1YMK.i7kpCYiwi5S99ltb9y2t1op3x0vu1pu');

INSERT INTO usuario_permissao (id_usuario, id_permissao) values (1, 1);
INSERT INTO usuario_permissao (id_usuario, id_permissao) values (1, 2);
INSERT INTO usuario_permissao (id_usuario, id_permissao) values (1, 3);
INSERT INTO usuario_permissao (id_usuario, id_permissao) values (1, 4);