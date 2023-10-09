package br.com.marketimobi.api.resource;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import br.com.marketimobi.api.model.Categoria;
import br.com.marketimobi.api.repository.CategoriaRepository;
import br.com.marketimobi.api.service.CategoriaService;

@RestController
@RequestMapping("/categorias")
public class CategoriaResource {
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@Autowired
	private CategoriaService categoriaService;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@GetMapping("/listar/{primeiroRegNull}")
	public List<Categoria> listar(@PathVariable Integer primeiroRegNull) {
		return categoriaService.listar(primeiroRegNull);
	}
	
	@GetMapping("/listar/categoriaPaiVazia")
	public List<Categoria> listarByCategoriaPaiVazia() {
		return categoriaRepository.findByCategoriaPaiIsNullOrderByCodigoAsc();
	}
	
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('write')")
	public ResponseEntity<Categoria> criar(@Valid @RequestBody Categoria categoria, HttpServletResponse response) {
		Categoria categoriaSalva = categoriaService.salvar(categoria);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, categoriaSalva.getId()));
		return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Categoria> buscarPeloId(@PathVariable Long id) {
		Optional<Categoria> categoria = categoriaRepository.findById(id);
		return categoria.isPresent() ? ResponseEntity.ok(categoria.get()) : ResponseEntity.notFound().build();
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('write')")
	public void remover(@PathVariable Long id) {
		categoriaService.remover(id);
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('write')")
	public ResponseEntity<Categoria> atualizar(@PathVariable Long id, @Valid @RequestBody Categoria categoria) {
		Categoria categoriaSalva = categoriaService.atualizar(id, categoria);
		return ResponseEntity.ok(categoriaSalva);
	}
	
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('read')")
	public Page<Categoria> pesquisar(@RequestParam(required = false, defaultValue = "%") String nome, @PageableDefault(sort = "codigo") Pageable pageable) {
		return categoriaRepository.findByNomeContaining(nome, pageable);
	}

}
