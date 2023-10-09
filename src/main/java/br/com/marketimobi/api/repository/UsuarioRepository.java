package br.com.marketimobi.api.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.marketimobi.api.model.Imobiliaria;
import br.com.marketimobi.api.model.Usuario;
import br.com.marketimobi.api.model.enums.StatusUsuario;
import br.com.marketimobi.api.repository.projection.UsuarioDto;
import br.com.marketimobi.api.repository.usuario.UsuarioRepositoryQuery;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>, UsuarioRepositoryQuery {
	
	public Optional<Usuario> findByEmail(String email);
	
	public List<Usuario> findByPermissoesDescricao(String permissaoDescricao);
	
	public Page<Usuario> findByNomeContainingAndDataCadastroGreaterThanEqualOrderByNomeAsc(String nome, LocalDate dataCadastro, Pageable pageAble);
	
	public Page<Usuario> findByNomeContainingAndDataCadastroGreaterThanEqualAndImobiliariaOrderByNomeAsc(String nome, LocalDate dataCadastro, Imobiliaria imobiliaria, Pageable pageAble);
	
	public Page<Usuario> findByNomeContainingAndDataCadastroGreaterThanEqualAndStatusOrderByNomeAsc(String nome, LocalDate dataCadastro, StatusUsuario status, Pageable pageAble);
	
	public Page<Usuario> findByNomeContainingAndDataCadastroGreaterThanEqualAndStatusAndImobiliariaOrderByNomeAsc(String nome, LocalDate dataCadastro, StatusUsuario status, Imobiliaria imobiliaria, Pageable pageAble);
	
	@Query("SELECT new br.com.marketimobi.api.repository.projection.UsuarioDto(u.id, u.nome) " +
		   "FROM br.com.marketimobi.api.model.Usuario u ORDER BY u.nome")
	public List<UsuarioDto> findAllUsuarioDto();

}
