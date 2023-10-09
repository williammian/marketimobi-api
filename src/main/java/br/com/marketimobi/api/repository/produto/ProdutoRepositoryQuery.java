package br.com.marketimobi.api.repository.produto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.marketimobi.api.model.Produto;
import br.com.marketimobi.api.repository.filter.PortfolioFilter;
import br.com.marketimobi.api.repository.filter.ProdutoFilter;
import br.com.marketimobi.api.repository.projection.ProdutoPortfolio;

public interface ProdutoRepositoryQuery {

	public Page<Produto> pesquisar(ProdutoFilter filter, Pageable pageAble);
	
	public Page<ProdutoPortfolio> pesquisar(PortfolioFilter filter, Pageable pageAble);
	
}
