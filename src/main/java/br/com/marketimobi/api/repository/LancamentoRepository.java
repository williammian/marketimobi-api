package br.com.marketimobi.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.marketimobi.api.model.Imobiliaria;
import br.com.marketimobi.api.model.Lancamento;
import br.com.marketimobi.api.model.Produto;
import br.com.marketimobi.api.model.Usuario;
import br.com.marketimobi.api.repository.lancamento.LancamentoRepositoryQuery;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>, LancamentoRepositoryQuery {

	public Long countByImobiliaria(Imobiliaria imobiliaria);
	
	public Long countByUsuario(Usuario usuario);
	
	public Long countByProduto(Produto produto);
	
}
