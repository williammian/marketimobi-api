ALTER TABLE categoria ADD COLUMN categoria_pai_id BIGINT(20);

ALTER TABLE categoria ADD CONSTRAINT fk_categoria_pai
FOREIGN KEY(categoria_pai_id) REFERENCES categoria (id);