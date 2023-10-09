package br.com.marketimobi.api.repository.usuario;

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
import br.com.marketimobi.api.model.Usuario;
import br.com.marketimobi.api.model.Usuario_;
import br.com.marketimobi.api.model.enums.StatusUsuario;
import br.com.marketimobi.api.repository.filter.UsuarioFilter;

public class UsuarioRepositoryImpl implements UsuarioRepositoryQuery {
	
	@Autowired
	private EntityManager manager;

	@Override
	public Page<Usuario> pesquisar(UsuarioFilter filter, Pageable pageAble) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Usuario> criteria = builder.createQuery(Usuario.class);
		Root<Usuario> root = criteria.from(Usuario.class);
		
		Predicate[] predicates = criarRestricoes(filter, builder, root);
		criteria.where(predicates);
		
		criteria.orderBy(builder.asc(root.get(Usuario_.nome)));
		
		TypedQuery<Usuario> query = manager.createQuery(criteria);
		adicionarRestricoesDePaginacao(query, pageAble);
				
		return new PageImpl<>(query.getResultList(), pageAble, total(filter));
	}
	
	private Predicate[] criarRestricoes(UsuarioFilter filter, CriteriaBuilder builder, Root<Usuario> root) {
		List<Predicate> predicates = new ArrayList<>();
		
		if(!StringUtils.isEmpty(filter.getNome())) {
			predicates.add(builder.like(
					builder.lower(root.get(Usuario_.nome)), "%" + filter.getNome().toLowerCase() + "%"));
		}
		
		if(filter.getDataCadastro() != null) {
			predicates.add(
					builder.greaterThanOrEqualTo(root.get(Usuario_.dataCadastro), filter.getDataCadastro()));
		}
		
		if(filter.getStatus() != null) {
			StatusUsuario statusUsuario = StatusUsuario.valueOf(filter.getStatus());
			predicates.add(
					builder.equal(root.get(Usuario_.status), statusUsuario));
		}
		
		if(filter.getImobiliaria() != null) {
			Imobiliaria imob = Imobiliaria.builder().id(filter.getImobiliaria()).build();
			predicates.add(
					builder.equal(root.get(Usuario_.imobiliaria), imob));
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
		
	private Long total(UsuarioFilter filter) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Usuario> root = criteria.from(Usuario.class);
		
		Predicate[] predicates = criarRestricoes(filter, builder, root);
		criteria.where(predicates);
		
		criteria.select(builder.count(root));
		return manager.createQuery(criteria).getSingleResult();
	}

}
