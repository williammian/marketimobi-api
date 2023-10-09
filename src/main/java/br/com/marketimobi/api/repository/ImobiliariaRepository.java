package br.com.marketimobi.api.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.marketimobi.api.model.Imobiliaria;
import br.com.marketimobi.api.model.enums.StatusImobiliaria;
import br.com.marketimobi.api.repository.imobiliaria.ImobiliariaRepositoryQuery;
import br.com.marketimobi.api.repository.projection.ImobiliariaDto;

public interface ImobiliariaRepository extends JpaRepository<Imobiliaria, Long>, ImobiliariaRepositoryQuery {
	
	public Page<Imobiliaria> findByNomeContainingAndDataCadastroGreaterThanEqualOrderByNomeAsc(String nome, LocalDate dataCadastro, Pageable pageAble);
	
	public Page<Imobiliaria> findByNomeContainingAndDataCadastroGreaterThanEqualAndStatusOrderByNomeAsc(String nome, LocalDate dataCadastro, StatusImobiliaria status, Pageable pageAble);

	@Query("SELECT new br.com.marketimobi.api.repository.projection.ImobiliariaDto(i.id, i.nome) " +
			   "FROM br.com.marketimobi.api.model.Imobiliaria i ORDER BY i.nome")
	public List<ImobiliariaDto> findAllImobiliariaDto();
}
