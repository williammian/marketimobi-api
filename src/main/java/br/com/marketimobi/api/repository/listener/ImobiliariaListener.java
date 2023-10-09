package br.com.marketimobi.api.repository.listener;

import javax.persistence.PostLoad;

import org.springframework.util.StringUtils;

import br.com.marketimobi.api.MarketimobiApiApplication;
import br.com.marketimobi.api.model.Imobiliaria;
import br.com.marketimobi.api.storage.FileStorage;

public class ImobiliariaListener {
	
	@PostLoad
	public void postLoad(Imobiliaria imobiliaria) {
		FileStorage fileStorage = MarketimobiApiApplication.getBean(FileStorage.class);
		
		if (StringUtils.hasText(imobiliaria.getImagem1())) {
			imobiliaria.setUrlImagem1(fileStorage.configurarUrl(imobiliaria.getImagem1()));
		}
		
		if (StringUtils.hasText(imobiliaria.getImagem2())) {
			imobiliaria.setUrlImagem2(fileStorage.configurarUrl(imobiliaria.getImagem2()));
		}
	}

}
