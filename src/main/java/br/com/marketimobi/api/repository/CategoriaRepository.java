package br.com.marketimobi.api.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.marketimobi.api.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

	public Page<Categoria> findByNomeContaining(String nome, Pageable page);
	
	public List<Categoria> findByCategoriaPaiIsNullOrderByCodigoAsc();
	
}
