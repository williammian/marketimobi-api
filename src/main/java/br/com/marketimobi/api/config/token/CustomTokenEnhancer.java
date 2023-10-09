package br.com.marketimobi.api.config.token;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import br.com.marketimobi.api.model.Imobiliaria;
import br.com.marketimobi.api.security.UsuarioSistema;

public class CustomTokenEnhancer implements TokenEnhancer {

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		UsuarioSistema usuarioSistema = (UsuarioSistema) authentication.getPrincipal();
		
		//Adicionando informações no Token
		Map<String, Object> addInfo = new HashMap<>();
		addInfo.put("usuario_id", usuarioSistema.getUsuario().getId());
		addInfo.put("usuario_nome", usuarioSistema.getUsuario().getNome());
		
		Imobiliaria imobiliaria = usuarioSistema.getUsuario().getImobiliaria();
		addInfo.put("imobiliaria_id", imobiliaria != null ? imobiliaria.getId() : null);
		addInfo.put("imobiliaria_nome", imobiliaria != null ? imobiliaria.getNome() : null);
		
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(addInfo);
		return accessToken;
	}

}
