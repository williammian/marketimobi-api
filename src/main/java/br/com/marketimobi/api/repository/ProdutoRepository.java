package br.com.marketimobi.api.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.marketimobi.api.model.Categoria;
import br.com.marketimobi.api.model.Produto;
import br.com.marketimobi.api.repository.produto.ProdutoRepositoryQuery;
import br.com.marketimobi.api.repository.projection.ProdutoDto;
import br.com.marketimobi.api.repository.projection.ProdutoPortfolio;

public interface ProdutoRepository  extends JpaRepository<Produto, Long>, ProdutoRepositoryQuery {
	
	public List<Produto> findByPrincipalOrderByCodigoAsc(boolean principal);
	
	public List<Produto> findByCategoriaOrderByCodigoAsc(Categoria categoria);
	
	public Page<Produto> findByCodigoContainingAndNomeContainingAndDataCadastroGreaterThanEqualOrderByCodigoAsc(String codigo, String nome, LocalDate dataCadastro, Pageable pageAble);
	
	public Page<Produto> findByCodigoContainingAndNomeContainingAndDataCadastroGreaterThanEqualAndPrincipalOrderByCodigoAsc(String codigo, String nome, LocalDate dataCadastro, Boolean principal, Pageable pageAble);
	
	public Page<Produto> findByCodigoContainingAndNomeContainingAndDataCadastroGreaterThanEqualAndCategoriaOrderByCodigoAsc(String codigo, String nome, LocalDate dataCadastro, Categoria categoria, Pageable pageAble);
	
	public Page<Produto> findByCodigoContainingAndNomeContainingAndDataCadastroGreaterThanEqualAndPrincipalAndCategoriaOrderByCodigoAsc(String codigo, String nome, LocalDate dataCadastro, Boolean principal, Categoria categoria, Pageable pageAble);
	
	@Query("SELECT new br.com.marketimobi.api.repository.projection.ProdutoPortfolio(p.id, p.codigo, p.nome, p.descricao, p.imagemCard, p.larguraImagemCard, p.alturaImagemCard, p.imagem, p.larguraImagemAmostra, p.alturaImagemAmostra) " +
		   "FROM br.com.marketimobi.api.model.Produto p WHERE p.principal = :principal ORDER BY p.codigo")
    public List<ProdutoPortfolio> findPortfolioPrincipais(boolean principal);
	
	@Query("SELECT new br.com.marketimobi.api.repository.projection.ProdutoPortfolio(p.id, p.codigo, p.nome, p.descricao, p.imagemCard, p.larguraImagemCard, p.alturaImagemCard, p.imagem, p.larguraImagemAmostra, p.alturaImagemAmostra) " +
		   "FROM br.com.marketimobi.api.model.Produto p WHERE p.categoria.id = :idCategoria ORDER BY p.codigo")
    public List<ProdutoPortfolio> findPortfolioPorCategoria(Long idCategoria);
	
	@Query("SELECT new br.com.marketimobi.api.repository.projection.ProdutoDto(p.id, p.codigo, p.nome) " +
		   "FROM br.com.marketimobi.api.model.Produto p ORDER BY p.codigo")
	public List<ProdutoDto> findAllProdutoDto();

}
