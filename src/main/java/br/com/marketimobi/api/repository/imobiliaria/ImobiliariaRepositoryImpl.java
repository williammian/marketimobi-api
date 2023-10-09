package br.com.marketimobi.api.repository.imobiliaria;

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

import br.com.marketimobi.api.model.Imobiliaria;
import br.com.marketimobi.api.model.Imobiliaria_;
import br.com.marketimobi.api.model.enums.StatusImobiliaria;
import br.com.marketimobi.api.repository.filter.ImobiliariaFilter;

public class ImobiliariaRepositoryImpl implements ImobiliariaRepositoryQuery {
	
	@Autowired
	private EntityManager manager;

	@Override
	public Page<Imobiliaria> pesquisar(ImobiliariaFilter filter, Pageable pageAble) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Imobiliaria> criteria = builder.createQuery(Imobiliaria.class);
		Root<Imobiliaria> root = criteria.from(Imobiliaria.class);
		
		Predicate[] predicates = criarRestricoes(filter, builder, root);
		criteria.where(predicates);
		
		criteria.orderBy(builder.asc(root.get(Imobiliaria_.nome)));
		
		TypedQuery<Imobiliaria> query = manager.createQuery(criteria);
		adicionarRestricoesDePaginacao(query, pageAble);
				
		return new PageImpl<>(query.getResultList(), pageAble, total(filter));
	}
	
	private Predicate[] criarRestricoes(ImobiliariaFilter filter, CriteriaBuilder builder, Root<Imobiliaria> root) {
		List<Predicate> predicates = new ArrayList<>();
		
		if(!StringUtils.isEmpty(filter.getNome())) {
			predicates.add(builder.like(
					builder.lower(root.get(Imobiliaria_.nome)), "%" + filter.getNome().toLowerCase() + "%"));
		}
		
		if(filter.getDataCadastro() != null) {
			predicates.add(
					builder.greaterThanOrEqualTo(root.get(Imobiliaria_.dataCadastro), filter.getDataCadastro()));
		}
		
		if(filter.getStatus() != null) {
			StatusImobiliaria statusImobiliaria = StatusImobiliaria.valueOf(filter.getStatus());
			predicates.add(
					builder.equal(root.get(Imobiliaria_.status), statusImobiliaria));
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
		
	private Long total(ImobiliariaFilter filter) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Imobiliaria> root = criteria.from(Imobiliaria.class);
		
		Predicate[] predicates = criarRestricoes(filter, builder, root);
		criteria.where(predicates);
		
		criteria.select(builder.count(root));
		return manager.createQuery(criteria).getSingleResult();
	}

}
