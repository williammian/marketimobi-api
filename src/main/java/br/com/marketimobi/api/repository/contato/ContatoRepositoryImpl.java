package br.com.marketimobi.api.repository.contato;

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

import br.com.marketimobi.api.model.Contato;
import br.com.marketimobi.api.model.Contato_;
import br.com.marketimobi.api.repository.filter.ContatoFilter;

public class ContatoRepositoryImpl implements ContatoRepositoryQuery {
	
	@Autowired
	private EntityManager manager;

	@Override
	public Page<Contato> pesquisar(ContatoFilter filter, Pageable pageAble) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Contato> criteria = builder.createQuery(Contato.class);
		Root<Contato> root = criteria.from(Contato.class);
		
		Predicate[] predicates = criarRestricoes(filter, builder, root);
		criteria.where(predicates);
		
		criteria.orderBy(builder.desc(root.get(Contato_.dataCadastro)));
		
		TypedQuery<Contato> query = manager.createQuery(criteria);
		adicionarRestricoesDePaginacao(query, pageAble);
				
		return new PageImpl<>(query.getResultList(), pageAble, total(filter));
	}
	
	private Predicate[] criarRestricoes(ContatoFilter filter, CriteriaBuilder builder, Root<Contato> root) {
		List<Predicate> predicates = new ArrayList<>();
		
		if(!StringUtils.isEmpty(filter.getNome())) {
			predicates.add(builder.like(
					builder.lower(root.get(Contato_.nome)), "%" + filter.getNome().toLowerCase() + "%"));
		}
		
		if(filter.getDataCadastro() != null) {
			predicates.add(
					builder.greaterThanOrEqualTo(root.get(Contato_.dataCadastro), filter.getDataCadastro()));
		}
		
		if(filter.getConferido() != null) {
			predicates.add(
					builder.equal(root.get(Contato_.conferido), filter.getConferido().equals(1)));
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
		
	private Long total(ContatoFilter filter) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Contato> root = criteria.from(Contato.class);
		
		Predicate[] predicates = criarRestricoes(filter, builder, root);
		criteria.where(predicates);
		
		criteria.select(builder.count(root));
		return manager.createQuery(criteria).getSingleResult();
	}

}
