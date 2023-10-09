package br.com.marketimobi.api.repository.contato;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.marketimobi.api.model.Contato;
import br.com.marketimobi.api.repository.filter.ContatoFilter;

public interface ContatoRepositoryQuery {
	
	public Page<Contato> pesquisar(ContatoFilter filter, Pageable pageAble);

}
