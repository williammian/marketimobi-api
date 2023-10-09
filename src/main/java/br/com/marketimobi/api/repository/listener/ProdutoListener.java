package br.com.marketimobi.api.repository.listener;

import javax.persistence.PostLoad;

import org.springframework.util.StringUtils;

import br.com.marketimobi.api.MarketimobiApiApplication;
import br.com.marketimobi.api.model.Produto;
import br.com.marketimobi.api.storage.FileStorage;

public class ProdutoListener {
	
	@PostLoad
	public void postLoad(Produto produto) {
		FileStorage fileStorage = MarketimobiApiApplication.getBean(FileStorage.class);
		
		if (StringUtils.hasText(produto.getImagem())) {
			produto.setUrlImagem(fileStorage.configurarUrl(produto.getImagem()));
		}
		
		if (StringUtils.hasText(produto.getImagemCard())) {
			produto.setUrlImagemCard(fileStorage.configurarUrl(produto.getImagemCard()));
		}
		
		if (StringUtils.hasText(produto.getImagemFundo())) {
			produto.setUrlImagemFundo(fileStorage.configurarUrl(produto.getImagemFundo()));
		}
		
	}

}
