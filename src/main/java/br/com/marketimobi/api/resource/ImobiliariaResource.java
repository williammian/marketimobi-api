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
import br.com.marketimobi.api.model.Imobiliaria;
import br.com.marketimobi.api.model.enums.StatusImobiliaria;
import br.com.marketimobi.api.repository.ImobiliariaRepository;
import br.com.marketimobi.api.repository.filter.ImobiliariaFilter;
import br.com.marketimobi.api.repository.projection.ImobiliariaDto;
import br.com.marketimobi.api.service.ImobiliariaService;

@RestController
@RequestMapping("/imobiliarias")
public class ImobiliariaResource {
	
	@Autowired
	private ImobiliariaRepository imobiliariaRepository;
	
	@Autowired
	private ImobiliariaService imobiliariaService;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@GetMapping("/listar")
	public List<Imobiliaria> listar() {
		return imobiliariaRepository.findAll(Sort.by(Sort.Direction.ASC, "nome"));
	}
	
	@GetMapping("/listar/{primeiroRegNull}")
	public List<ImobiliariaDto> listar(@PathVariable Integer primeiroRegNull) {
		return imobiliariaService.listar(primeiroRegNull);
	}
	
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('write')")
	public ResponseEntity<Imobiliaria> criar(@Valid @RequestBody Imobiliaria imobiliaria, HttpServletResponse response) {
		Imobiliaria imobiliariaSalva = imobiliariaService.salvar(imobiliaria);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, imobiliariaSalva.getId()));
		return ResponseEntity.status(HttpStatus.CREATED).body(imobiliariaSalva);
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_MINHA_IMOBILIARIA') and #oauth2.hasScope('read')")
	public ResponseEntity<Imobiliaria> buscarPeloId(@PathVariable Long id) {
		Optional<Imobiliaria> imobiliaria = imobiliariaRepository.findById(id);
		return imobiliaria.isPresent() ? ResponseEntity.ok(imobiliaria.get()) : ResponseEntity.notFound().build();
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('write')")
	public void remover(@PathVariable Long id) {
		imobiliariaService.remover(id);
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_MINHA_IMOBILIARIA') and #oauth2.hasScope('write')")
	public ResponseEntity<Imobiliaria> atualizar(@PathVariable Long id, @Valid @RequestBody Imobiliaria imobiliaria) {
		Imobiliaria imobiliariaSalva = imobiliariaService.atualizar(id, imobiliaria);
		return ResponseEntity.ok(imobiliariaSalva);
	}
	
	@PutMapping("/{id}/status")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('write')")
	public void atualizarPropriedadeStatus(@PathVariable Long id, @RequestBody String status) {
		imobiliariaService.atualizarPropriedadeStatus(id, status);
	}
	
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('read')")
	public Page<Imobiliaria> pesquisar(@RequestParam(required = false, defaultValue = "%") String nome, 
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataCadastro, 
			@RequestParam(required = false) String status, Pageable pageable) {
		
		if(dataCadastro == null) dataCadastro = LocalDate.of(2020, 1, 1);
		
		if(status != null) {
			StatusImobiliaria statusImobiliaria = StatusImobiliaria.valueOf(status);
			return imobiliariaRepository.findByNomeContainingAndDataCadastroGreaterThanEqualAndStatusOrderByNomeAsc(nome, dataCadastro, statusImobiliaria, pageable);
		}else {
			return imobiliariaRepository.findByNomeContainingAndDataCadastroGreaterThanEqualOrderByNomeAsc(nome, dataCadastro, pageable);
		}
	}
	
	@GetMapping("pesquisar")
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('read')")
	public Page<Imobiliaria> pesquisar(ImobiliariaFilter imobiliariaFilter, Pageable pageable) {
		return imobiliariaRepository.pesquisar(imobiliariaFilter, pageable);
	}

}
