package br.com.marketimobi.api.repository.lancamento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.marketimobi.api.model.Lancamento;
import br.com.marketimobi.api.repository.filter.LancamentoFilter;

public interface LancamentoRepositoryQuery {
	
	public Page<Lancamento> pesquisar(LancamentoFilter filter, Pageable pageAble);

}
