package br.com.marketimobi.api.repository.produto;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import br.com.marketimobi.api.model.Categoria;
import br.com.marketimobi.api.model.Produto;
import br.com.marketimobi.api.model.Produto_;
import br.com.marketimobi.api.repository.filter.PortfolioFilter;
import br.com.marketimobi.api.repository.filter.ProdutoFilter;
import br.com.marketimobi.api.repository.projection.ProdutoPortfolio;

public class ProdutoRepositoryImpl implements ProdutoRepositoryQuery {
	
	@Autowired
	private EntityManager manager;

	@Override
	public Page<Produto> pesquisar(ProdutoFilter filter, Pageable pageAble) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Produto> criteria = builder.createQuery(Produto.class);
		Root<Produto> root = criteria.from(Produto.class);
		
		Predicate[] predicates = criarRestricoes(filter, builder, root);
		criteria.where(predicates);
		
		criteria.orderBy(builder.asc(root.get(Produto_.codigo)));
		
		TypedQuery<Produto> query = manager.createQuery(criteria);
		adicionarRestricoesDePaginacao(query, pageAble);
				
		return new PageImpl<>(query.getResultList(), pageAble, total(filter));
	}
	
	private Predicate[] criarRestricoes(ProdutoFilter filter, CriteriaBuilder builder, Root<Produto> root) {
		List<Predicate> predicates = new ArrayList<>();
		
		if(!StringUtils.isEmpty(filter.getCodigo())) {
			predicates.add(builder.like(
					builder.lower(root.get(Produto_.codigo)), "%" + filter.getCodigo().toLowerCase() + "%"));
		}
		
		if(!StringUtils.isEmpty(filter.getNome())) {
			predicates.add(builder.like(
					builder.lower(root.get(Produto_.nome)), "%" + filter.getNome().toLowerCase() + "%"));
		}
		
		if(filter.getDataCadastro() != null) {
			predicates.add(
					builder.greaterThanOrEqualTo(root.get(Produto_.dataCadastro), filter.getDataCadastro()));
		}
		
		if(filter.getPrincipal() != null) {
			predicates.add(
					builder.equal(root.get(Produto_.principal), filter.getPrincipal().equals(1)));
		}
		
		if(filter.getCategoria() != null) {
			Categoria cat = Categoria.builder().id(filter.getCategoria()).build();
			predicates.add(
					builder.equal(root.get(Produto_.categoria), cat));
		}
		
		return predicates.toArray(new Predicate[predicates.size()]);
	}
	
	private void adicionarRestricoesDePaginacao(TypedQuery<?> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;
		
		query.setFirstResult(primeiroRegistroDaPagina);
		query.setMaxResults(totalRegistrosPorPagina);
	}
		
	private Long total(ProdutoFilter filter) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Produto> root = criteria.from(Produto.class);
		
		Predicate[] predicates = criarRestricoes(filter, builder, root);
		criteria.where(predicates);
		
		criteria.select(builder.count(root));
		return manager.createQuery(criteria).getSingleResult();
	}
	
	@Override
	public Page<ProdutoPortfolio> pesquisar(PortfolioFilter filter, Pageable pageAble) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<ProdutoPortfolio> criteria = builder.createQuery(ProdutoPortfolio.class);
		Root<Produto> root = criteria.from(Produto.class);
		
		criteria.select(builder.construct(ProdutoPortfolio.class
				, root.get(Produto_.id), root.get(Produto_.codigo)
				, root.get(Produto_.nome), root.get(Produto_.descricao)
				, root.get(Produto_.imagemCard), root.get(Produto_.larguraImagemCard)
				, root.get(Produto_.alturaImagemCard), root.get(Produto_.imagem)
				, root.get(Produto_.larguraImagemAmostra), root.get(Produto_.alturaImagemAmostra)));
		
		Predicate[] predicates = criarRestricoes(filter, builder, root);
		criteria.where(predicates);
		
		criteria.orderBy(builder.asc(root.get(Produto_.codigo)));
		
		TypedQuery<ProdutoPortfolio> query = manager.createQuery(criteria);
		adicionarRestricoesDePaginacao(query, pageAble);
				
		return new PageImpl<>(query.getResultList(), pageAble, total(filter));
	}
	
	private Predicate[] criarRestricoes(PortfolioFilter filter, CriteriaBuilder builder, Root<Produto> root) {
		List<Predicate> predicates = new ArrayList<>();
		
		if(filter.getPrincipal() != null) {
			predicates.add(
					builder.equal(root.get(Produto_.principal), filter.getPrincipal().equals(1)));
		}
		
		if(filter.getCategoria() != null) {
			Categoria cat = Categoria.builder().id(filter.getCategoria()).build();
			predicates.add(
					builder.equal(root.get(Produto_.categoria), cat));
		}
		
		return predicates.toArray(new Predicate[predicates.size()]);
	}
	
	private Long total(PortfolioFilter filter) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Produto> root = criteria.from(Produto.class);
		
		Predicate[] predicates = criarRestricoes(filter, builder, root);
		criteria.where(predicates);
		
		criteria.select(builder.count(root));
		return manager.createQuery(criteria).getSingleResult();
	}

}
