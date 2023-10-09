package br.com.marketimobi.api.repository.usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.marketimobi.api.model.Usuario;
import br.com.marketimobi.api.repository.filter.UsuarioFilter;

public interface UsuarioRepositoryQuery {
	
	public Page<Usuario> pesquisar(UsuarioFilter filter, Pageable pageAble);

}
