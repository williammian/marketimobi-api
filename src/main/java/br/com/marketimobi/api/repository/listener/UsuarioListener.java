package br.com.marketimobi.api.repository.listener;

import javax.persistence.PostLoad;

import org.springframework.util.StringUtils;

import br.com.marketimobi.api.MarketimobiApiApplication;
import br.com.marketimobi.api.model.Usuario;
import br.com.marketimobi.api.storage.FileStorage;

public class UsuarioListener {
	
	@PostLoad
	public void postLoad(Usuario usuario) {
		FileStorage fileStorage = MarketimobiApiApplication.getBean(FileStorage.class);
		
		if (StringUtils.hasText(usuario.getImagem1())) {
			usuario.setUrlImagem1(fileStorage.configurarUrl(usuario.getImagem1()));
		}
		
		if (StringUtils.hasText(usuario.getImagem2())) {
			usuario.setUrlImagem2(fileStorage.configurarUrl(usuario.getImagem2()));
		}
	}

}
