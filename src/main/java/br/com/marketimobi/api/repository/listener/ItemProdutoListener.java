package br.com.marketimobi.api.repository.listener;

import javax.persistence.PostLoad;

import org.springframework.util.StringUtils;

import br.com.marketimobi.api.MarketimobiApiApplication;
import br.com.marketimobi.api.model.ItemProduto;
import br.com.marketimobi.api.storage.FileStorage;

public class ItemProdutoListener {
	
	@PostLoad
	public void postLoad(ItemProduto itemProduto) {
		FileStorage fileStorage = MarketimobiApiApplication.getBean(FileStorage.class);
		
		if (StringUtils.hasText(itemProduto.getImagemSistema())) {
			itemProduto.setUrlImagemSistema(fileStorage.configurarUrl(itemProduto.getImagemSistema()));
		}
		
	}

}
