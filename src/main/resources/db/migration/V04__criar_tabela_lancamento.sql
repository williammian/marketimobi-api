CREATE TABLE lancamento (
	id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
	id_imobiliaria BIGINT(20) NOT NULL,
	id_usuario BIGINT(20) NOT NULL,
	id_produto BIGINT(20) NOT NULL,
	data DATE NOT NULL,
	hora TIME NOT NULL,
	FOREIGN KEY (id_imobiliaria) REFERENCES imobiliaria(id),
	FOREIGN KEY (id_usuario) REFERENCES usuario(id),
	FOREIGN KEY (id_produto) REFERENCES produto(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;