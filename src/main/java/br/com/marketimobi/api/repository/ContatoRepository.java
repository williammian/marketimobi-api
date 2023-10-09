package br.com.marketimobi.api.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.marketimobi.api.model.Contato;
import br.com.marketimobi.api.repository.contato.ContatoRepositoryQuery;

public interface ContatoRepository extends JpaRepository<Contato, Long>, ContatoRepositoryQuery {
	
	public Page<Contato> findByNomeContainingAndDataCadastroGreaterThanEqualOrderByDataCadastroDesc(String nome, LocalDate dataCadastro, Pageable pageAble);
	
	public Page<Contato> findByNomeContainingAndDataCadastroGreaterThanEqualAndConferidoOrderByDataCadastroDesc(String nome, LocalDate dataCadastro, Boolean conferido, Pageable pageAble);

}
