package br.com.marketimobi.api.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import br.com.marketimobi.api.exception.ValidacaoException;
import br.com.marketimobi.api.model.Imobiliaria;
import br.com.marketimobi.api.model.Lancamento;
import br.com.marketimobi.api.model.Produto;
import br.com.marketimobi.api.model.Usuario;
import br.com.marketimobi.api.repository.LancamentoRepository;

@Service
public class LancamentoService {
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	public void validarExistenciaLctoImobiliaria(Imobiliaria imobiliaria) {
		Long lctos = lancamentoRepository.countByImobiliaria(imobiliaria);
		if(lctos != null && lctos.compareTo(0L) > 0)
			throw new ValidacaoException("Há lançamentos para esta imobiliária.");
	}
	
	public void validarExistenciaLctoUsuario(Usuario usuario) {
		Long lctos = lancamentoRepository.countByUsuario(usuario);
		if(lctos != null && lctos.compareTo(0L) > 0)
			throw new ValidacaoException("Há lançamentos para este usuário.");
	}
	
	public void validarExistenciaLctoProduto(Produto produto) {
		Long lctos = lancamentoRepository.countByProduto(produto);
		if(lctos != null && lctos.compareTo(0L) > 0)
			throw new ValidacaoException("Há lançamentos para este produto.");
	}
	
	public void gravarLancamento(Imobiliaria imobiliaria, Usuario usuario, Produto produto) {
		if(usuario.contemPermissao("ROLE_ADMINISTRADOR")) return;
		
		Lancamento lancamento = new Lancamento();
		lancamento.setImobiliaria(imobiliaria);
		lancamento.setUsuario(usuario);
		lancamento.setProduto(produto);
		
		LocalDateTime dataHora = LocalDateTime.now();
		lancamento.setData(dataHora.toLocalDate());
		lancamento.setHora(dataHora.toLocalTime());
		
		lancamentoRepository.save(lancamento);
	}
	
	public void remover(Long id) {
		Lancamento lancamento = buscarLancamentoPeloId(id);
		
		lancamentoRepository.delete(lancamento);
	}
	
	public Lancamento buscarLancamentoPeloId(Long id) {
		Optional<Lancamento> lancamento = lancamentoRepository.findById(id);
		if (!lancamento.isPresent()) {
			throw new EmptyResultDataAccessException(1);
		}
		return lancamento.get();
	}

}
