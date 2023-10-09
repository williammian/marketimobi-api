package br.com.marketimobi.api.repository.imobiliaria;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.marketimobi.api.model.Imobiliaria;
import br.com.marketimobi.api.repository.filter.ImobiliariaFilter;

public interface ImobiliariaRepositoryQuery {
	
	public Page<Imobiliaria> pesquisar(ImobiliariaFilter filter, Pageable pageAble);

}
