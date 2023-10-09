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

import br.com.marketimobi.api.event.RecursoCriadoEvent;
import br.com.marketimobi.api.model.Contato;
import br.com.marketimobi.api.repository.ContatoRepository;
import br.com.marketimobi.api.repository.filter.ContatoFilter;
import br.com.marketimobi.api.service.ContatoService;

@RestController
@RequestMapping("/contatos")
public class ContatoResource {
	
	@Autowired
	private ContatoRepository contatoRepository;
	
	@Autowired
	private ContatoService contatoService;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@GetMapping("/listar")
	public List<Contato> listar() {
		return contatoRepository.findAll(Sort.by(Sort.Direction.ASC, "nome"));
	}
	
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('write')")
	public ResponseEntity<Contato> criar(@Valid @RequestBody Contato contato, HttpServletResponse response) {
		return criarNovoContato(contato, false, response);
	}

	@PostMapping("/novo")
	public ResponseEntity<Contato> novo(@Valid @RequestBody Contato contato, HttpServletResponse response) {
		return criarNovoContato(contato, true, response);
	}
	
	private ResponseEntity<Contato> criarNovoContato(Contato contato, boolean enviarEmail, HttpServletResponse response) {
		Contato contatoSalvo = contatoService.salvar(contato, enviarEmail);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, contatoSalvo.getId()));
		return ResponseEntity.status(HttpStatus.CREATED).body(contatoSalvo);
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('read')")
	public ResponseEntity<Contato> buscarPeloId(@PathVariable Long id) {
		Optional<Contato> contato = contatoRepository.findById(id);
		return contato.isPresent() ? ResponseEntity.ok(contato.get()) : ResponseEntity.notFound().build();
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('write')")
	public void remover(@PathVariable Long id) {
		contatoService.remover(id);
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('write')")
	public ResponseEntity<Contato> atualizar(@PathVariable Long id, @Valid @RequestBody Contato contato) {
		Contato contatoSalvo = contatoService.atualizar(id, contato);
		return ResponseEntity.ok(contatoSalvo);
	}
	
	@PutMapping("/{id}/conferido")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('write')")
	public void atualizarPropriedadeConferido(@PathVariable Long id, @RequestBody Boolean conferido) {
		contatoService.atualizarPropriedadeConferido(id, conferido);
	}
	
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('read')")
	public Page<Contato> pesquisar(@RequestParam(required = false, defaultValue = "%") String nome, 
			@RequestParam(required = false) Integer conferido,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataCadastro, 
			Pageable pageable) {
		
		if(dataCadastro == null) dataCadastro = LocalDate.of(2020, 1, 1);
		
		if(conferido != null) {
			return contatoRepository.findByNomeContainingAndDataCadastroGreaterThanEqualAndConferidoOrderByDataCadastroDesc(nome, dataCadastro, conferido.equals(1), pageable);
		}else {
			return contatoRepository.findByNomeContainingAndDataCadastroGreaterThanEqualOrderByDataCadastroDesc(nome, dataCadastro, pageable);
		}
	}
	
	@GetMapping("pesquisar")
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('read')")
	public Page<Contato> pesquisar(ContatoFilter contatoFilter, Pageable pageable) {	
		return contatoRepository.pesquisar(contatoFilter, pageable);
	}

}
