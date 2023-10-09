package br.com.marketimobi.api.resource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.marketimobi.api.dto.AlterarSenhaDto;
import br.com.marketimobi.api.dto.EsqueceuSenhaDto;
import br.com.marketimobi.api.event.RecursoCriadoEvent;
import br.com.marketimobi.api.model.Imobiliaria;
import br.com.marketimobi.api.model.Usuario;
import br.com.marketimobi.api.model.enums.StatusUsuario;
import br.com.marketimobi.api.repository.UsuarioRepository;
import br.com.marketimobi.api.repository.filter.UsuarioFilter;
import br.com.marketimobi.api.repository.projection.UsuarioDto;
import br.com.marketimobi.api.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioResource {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@GetMapping("/listar")
	public List<Usuario> listar() {
		return usuarioRepository.findAll(Sort.by(Sort.Direction.ASC, "nome"));
	}
	
	@GetMapping("/listar/{primeiroRegNull}")
	public List<UsuarioDto> listar(@PathVariable Integer primeiroRegNull) {
		return usuarioService.listar(primeiroRegNull);
	}
	
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('write')")
	public ResponseEntity<Usuario> criar(@Valid @RequestBody Usuario usuario, HttpServletResponse response) {		
		Usuario usuarioSalvo = usuarioService.salvar(usuario);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, usuarioSalvo.getId()));
		return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSalvo);
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_MEUS_DADOS') and #oauth2.hasScope('read')")
	public ResponseEntity<Usuario> buscarPeloId(@PathVariable Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		return usuario.isPresent() ? ResponseEntity.ok(usuario.get()) : ResponseEntity.notFound().build();
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('write')")
	public void remover(@PathVariable Long id) {
		usuarioService.remover(id);
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_MEUS_DADOS') and #oauth2.hasScope('write')")
	public ResponseEntity<Usuario> atualizar(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
		Usuario usuarioSalvo = usuarioService.atualizar(id, usuario);
		return ResponseEntity.ok(usuarioSalvo);
	}
	
	@PutMapping("/{id}/status")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('write')")
	public void atualizarPropriedadeStatus(@PathVariable Long id, @RequestBody String status) {
		usuarioService.atualizarPropriedadeStatus(id, status);
	}
	
	@PutMapping("/{id}/alterarSenha")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_MEUS_DADOS') and #oauth2.hasScope('write')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void alterarSenha(@PathVariable Long id, @RequestBody AlterarSenhaDto dto) {
		usuarioService.alterarSenha(id, dto);
	}
	
	@PutMapping("/{id}/setarSenha")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('write')")
	public void setarSenha(@PathVariable Long id, @RequestBody String senhaNova) {
		usuarioService.setarSenha(id, senhaNova);
	}
	
	@PutMapping("/esqueceuSenha")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void esqueceuSenha(@RequestBody EsqueceuSenhaDto dto) {
		usuarioService.esqueceuSenha(dto);
	}
	
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('read')")
	public Page<Usuario> pesquisar(@RequestParam(required = false, defaultValue = "%") String nome, 
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataCadastro, 
			@RequestParam(required = false) String status, 
			@RequestParam(required = false) Long imobiliaria,
			Pageable pageable) {
		
		if(dataCadastro == null) dataCadastro = LocalDate.of(2020, 1, 1);
		
		if(status != null) {
			StatusUsuario statusUsuario = StatusUsuario.valueOf(status);
			if(imobiliaria == null) {
				return usuarioRepository.findByNomeContainingAndDataCadastroGreaterThanEqualAndStatusOrderByNomeAsc(nome, dataCadastro, statusUsuario, pageable);
			}else {
				Imobiliaria imob = Imobiliaria.builder().id(imobiliaria).build();
				return usuarioRepository.findByNomeContainingAndDataCadastroGreaterThanEqualAndStatusAndImobiliariaOrderByNomeAsc(nome, dataCadastro, statusUsuario, imob, pageable);
			}
		}else {
			if(imobiliaria == null) {
				return usuarioRepository.findByNomeContainingAndDataCadastroGreaterThanEqualOrderByNomeAsc(nome, dataCadastro, pageable);
			}else {
				Imobiliaria imob = Imobiliaria.builder().id(imobiliaria).build();
				return usuarioRepository.findByNomeContainingAndDataCadastroGreaterThanEqualAndImobiliariaOrderByNomeAsc(nome, dataCadastro, imob, pageable);
			}
		}
	}
	
	@GetMapping("pesquisar")
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('read')")
	public Page<Usuario> pesquisar(UsuarioFilter usuarioFilter, Pageable pageAble) {
		return usuarioRepository.pesquisar(usuarioFilter, pageAble);
	}

}
