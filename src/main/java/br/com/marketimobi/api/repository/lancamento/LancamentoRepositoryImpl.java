package br.com.marketimobi.api.repository.lancamento;

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

import br.com.marketimobi.api.exception.ValidacaoException;
import br.com.marketimobi.api.model.Imobiliaria;
import br.com.marketimobi.api.model.Lancamento;
import br.com.marketimobi.api.model.Lancamento_;
import br.com.marketimobi.api.model.Produto;
import br.com.marketimobi.api.model.Usuario;
import br.com.marketimobi.api.repository.filter.LancamentoFilter;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {
	
	@Autowired
	private EntityManager manager;

	@Override
	public Page<Lancamento> pesquisar(LancamentoFilter filter, Pageable pageAble) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		Predicate[] predicates = criarRestricoes(filter, builder, root);
		criteria.where(predicates);
		
		criteria.orderBy(builder.desc(root.get(Lancamento_.data)), 
				builder.desc(root.get(Lancamento_.hora)));
		
		TypedQuery<Lancamento> query = manager.createQuery(criteria);
		adicionarRestricoesDePaginacao(query, pageAble);
				
		return new PageImpl<>(query.getResultList(), pageAble, total(filter));
	}
	
	private Predicate[] criarRestricoes(LancamentoFilter filter, CriteriaBuilder builder, Root<Lancamento> root) {
		List<Predicate> predicates = new ArrayList<>();
		
		if(filter.getDataInicial() == null)
			throw new ValidacaoException("Data inicial deve ser informada.");
		
		if(filter.getDataFinal() == null)
			throw new ValidacaoException("Data final deve ser informada.");
		
		predicates.add(builder.between(root.get(Lancamento_.data), filter.getDataInicial(), filter.getDataFinal()));
		
		if(filter.getImobiliaria() != null) {
			Imobiliaria imobiliaria = Imobiliaria.builder().id(filter.getImobiliaria()).build();
			predicates.add(builder.equal(root.get(Lancamento_.imobiliaria), imobiliaria));
		}
		
		if(filter.getUsuario() != null) {
			Usuario usuario = Usuario.builder().id(filter.getUsuario()).build();
			predicates.add(builder.equal(root.get(Lancamento_.usuario), usuario));
		}
		
		if(filter.getProduto() != null) {
			Produto produto = Produto.builder().id(filter.getProduto()).build();
			predicates.add(builder.equal(root.get(Lancamento_.produto), produto));
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
	
	private Long total(LancamentoFilter filter) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		Predicate[] predicates = criarRestricoes(filter, builder, root);
		criteria.where(predicates);
		
		criteria.select(builder.count(root));
		return manager.createQuery(criteria).getSingleResult();
	}

}
