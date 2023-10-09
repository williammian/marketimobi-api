package br.com.marketimobi.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import br.com.marketimobi.api.dto.AlterarSenhaDto;
import br.com.marketimobi.api.dto.EsqueceuSenhaDto;
import br.com.marketimobi.api.exception.ValidacaoException;
import br.com.marketimobi.api.mail.Mailer;
import br.com.marketimobi.api.model.Usuario;
import br.com.marketimobi.api.model.enums.StatusUsuario;
import br.com.marketimobi.api.repository.UsuarioRepository;
import br.com.marketimobi.api.repository.projection.UsuarioDto;
import br.com.marketimobi.api.storage.FileStorage;

@Service
public class UsuarioService {
	
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private LancamentoService lancamentoService;
	
	@Autowired
	private FileStorage fileStorage;
	
	@Autowired
	private Mailer mailer;
	
	public List<UsuarioDto> listar(Integer primeiroRegNull) {
		List<UsuarioDto> usaurios = usuarioRepository.findAllUsuarioDto();
		
		if(primeiroRegNull != null && primeiroRegNull.equals(1)) {
			if(usaurios == null) usaurios = new ArrayList<>();
			usaurios.add(0, null);
		}
		return usaurios;
	}
	
	public Usuario salvar(Usuario usuario) {
		if (StringUtils.hasText(usuario.getImagem1())) {
			fileStorage.salvar(usuario.getImagem1());
		}
		
		String senha = usuario.getSenhaInformada();
		if(senha == null) throw new ValidacaoException("Não foi informada a senha do usuário.");
		senha = encoder.encode(senha);
		usuario.setSenha(senha);
		
		return usuarioRepository.save(usuario);
	}
	
	public Usuario atualizar(Long id, Usuario usuario) {
		Usuario usuarioSalvo = buscarUsuarioPeloId(id);
		
		usuarioSalvo.getPermissoes().clear();
		usuarioSalvo.getPermissoes().addAll(usuario.getPermissoes());
		
		if (StringUtils.isEmpty(usuario.getImagem1())
				&& StringUtils.hasText(usuarioSalvo.getImagem1())) {
			fileStorage.remover(usuarioSalvo.getImagem1());
		} else if (StringUtils.hasText(usuario.getImagem1())
				&& !usuario.getImagem1().equals(usuarioSalvo.getImagem1())) {
			fileStorage.substituir(usuarioSalvo.getImagem1(), usuario.getImagem1());
		}

		BeanUtils.copyProperties(usuario, usuarioSalvo, "id", "senha", "permissoes");
		return usuarioRepository.save(usuarioSalvo);
	}
	
	public void remover(Long id) {
		Usuario usuario = buscarUsuarioPeloId(id);

		lancamentoService.validarExistenciaLctoUsuario(usuario);
		
		if (usuario.getImagem1() != null) fileStorage.remover(usuario.getImagem1());
		
		usuarioRepository.delete(usuario);
	}
	
	public void atualizarPropriedadeStatus(Long id, String status) {
		Usuario usuarioSalvo = buscarUsuarioPeloId(id);
		usuarioSalvo.setStatus(StatusUsuario.valueOf(status));
		usuarioRepository.save(usuarioSalvo);
	}
	
	public void alterarSenha(Long id, AlterarSenhaDto dto) {
		if(dto.getSenhaAtual() == null) throw new ValidacaoException("Não foi informada a senha atual.");
		
		if(dto.getSenhaNova() == null) throw new ValidacaoException("Não foi informada a nova senha.");
		
		Usuario usuarioSalvo = buscarUsuarioPeloId(id);
		
		if(!encoder.matches(dto.getSenhaAtual(), usuarioSalvo.getSenha())) 
			throw new ValidacaoException("A senha atual informada não confere com a registrada no usuário.");
		
		String senhaNovaCrypt = encoder.encode(dto.getSenhaNova());
		
		usuarioSalvo.setSenha(senhaNovaCrypt);
		
		usuarioRepository.save(usuarioSalvo);
	}
	
	public void setarSenha(Long id, String senhaNova) {
		if(senhaNova == null) throw new ValidacaoException("Não foi informada a nova senha.");
		
		String senhaNovaCrypt = encoder.encode(senhaNova);
		
		Usuario usuarioSalvo = buscarUsuarioPeloId(id);
		
		usuarioSalvo.setSenha(senhaNovaCrypt);
		
		usuarioRepository.save(usuarioSalvo);
	}
	
	public void esqueceuSenha(EsqueceuSenhaDto dto) {
		if(dto.getEmail() == null) throw new ValidacaoException("Login/E-mail não informado.");
		if(dto.getDataNascimento() == null) throw new ValidacaoException("Não foi informada a data de nascimento.");
		
		Optional<Usuario> usuario = usuarioRepository.findByEmail(dto.getEmail());
		if(!usuario.isPresent()) {
			throw new ValidacaoException("Login/E-mail informado não encontrado no sistema ou data de nascimento não confere.");
		}
		
		Usuario user = usuario.get();
		
		if(!user.getDataNascimento().equals(dto.getDataNascimento())) {
			throw new ValidacaoException("Login/E-mail informado não encontrado no sistema ou data de nascimento não confere.");
		}
		
		String novaSenha = gerarSenha();
		
		setarSenha(user.getId(), novaSenha);
		
		mailer.esqueceuSenha(user, novaSenha);
	}
	
	public Usuario buscarUsuarioPeloId(Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		if (!usuario.isPresent()) {
			throw new EmptyResultDataAccessException(1);
		}
		return usuario.get();
	}
	
	private String gerarSenha() {
		String letras = "0123456789abcdefghijklmnopqrstuvxwyzABCDEFGHIJKLMNOPQRSTUVXWYZ$&(@)(!";
		
		String palavra = "";
		for(int i = 0; i < 10; i++){
			int aleatorio = (int) (Math.random() * (letras.length() -1) );
			String letra = letras.substring(aleatorio, aleatorio+1);
			palavra = palavra + letra;
		}
		
		return palavra;
	}


}
