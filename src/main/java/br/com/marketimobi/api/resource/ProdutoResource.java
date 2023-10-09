package br.com.marketimobi.api.resource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.marketimobi.api.event.RecursoCriadoEvent;
import br.com.marketimobi.api.model.Categoria;
import br.com.marketimobi.api.model.Produto;
import br.com.marketimobi.api.repository.ProdutoRepository;
import br.com.marketimobi.api.repository.filter.PortfolioFilter;
import br.com.marketimobi.api.repository.filter.ProdutoFilter;
import br.com.marketimobi.api.repository.projection.ProdutoDto;
import br.com.marketimobi.api.repository.projection.ProdutoPortfolio;
import br.com.marketimobi.api.service.ProdutoService;

@RestController
@RequestMapping("/produtos")
public class ProdutoResource {
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private ProdutoService produtoService;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@GetMapping("/listar")
	public List<Produto> listar() {
		return produtoRepository.findAll(Sort.by(Sort.Direction.ASC, "codigo"));
	}
	
	@GetMapping("/listar/{primeiroRegNull}")
	public List<ProdutoDto> listar(@PathVariable Integer primeiroRegNull) {
		return produtoService.listarProdutoDto(primeiroRegNull);
	}
	
	@GetMapping("/principais")
	public List<Produto> principais() {
		return produtoRepository.findByPrincipalOrderByCodigoAsc(true);
	}
	
	@GetMapping("/portfolio/principais")
	public List<ProdutoPortfolio> portfolioPrincipais() {
		return produtoRepository.findPortfolioPrincipais(true);
	}
	
	@GetMapping("/categoria/{id}")
	public List<Produto> categoria(@PathVariable Long id) {
		return produtoRepository.findByCategoriaOrderByCodigoAsc(Categoria.builder().id(id).build());
	}
	
	@GetMapping("/portfolio/categoria/{id}")
	public List<ProdutoPortfolio> portfolioPorCategoria(@PathVariable Long id) {
		return produtoRepository.findPortfolioPorCategoria(id);
	}
	
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('write')")
	public ResponseEntity<Produto> criar(@Valid @RequestBody Produto produto, HttpServletResponse response) {
		Produto produtoSalvo = produtoService.salvar(produto);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, produtoSalvo.getId()));
		return ResponseEntity.status(HttpStatus.CREATED).body(produtoSalvo);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Produto> buscarPeloId(@PathVariable Long id) {
		Optional<Produto> produto = produtoRepository.findById(id);
		return produto.isPresent() ? ResponseEntity.ok(produto.get()) : ResponseEntity.notFound().build();
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('write')")
	public void remover(@PathVariable Long id) {
		produtoService.remover(id);
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('write')")
	public ResponseEntity<Produto> atualizar(@PathVariable Long id, @Valid @RequestBody Produto produto) {
		Produto produtoSalvo = produtoService.atualizar(id, produto);
		return ResponseEntity.ok(produtoSalvo);
	}
	
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('read')")
	public Page<Produto> pesquisar(@RequestParam(required = false, defaultValue = "%") String codigo,
			@RequestParam(required = false, defaultValue = "%") String nome, 
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataCadastro, 
			@RequestParam(required = false) Integer principal,
			@RequestParam(required = false) Long categoria,
			Pageable pageable) {
		
		if(dataCadastro == null) dataCadastro = LocalDate.of(2020, 1, 1);
		
		if(principal != null) {
			if(categoria == null) {
				return produtoRepository.findByCodigoContainingAndNomeContainingAndDataCadastroGreaterThanEqualAndPrincipalOrderByCodigoAsc(codigo, nome, dataCadastro, principal.equals(1), pageable);
			}else {
				Categoria cat = Categoria.builder().id(categoria).build();
				return produtoRepository.findByCodigoContainingAndNomeContainingAndDataCadastroGreaterThanEqualAndPrincipalAndCategoriaOrderByCodigoAsc(codigo, nome, dataCadastro, principal.equals(1), cat, pageable);
			}
		}else {
			if(categoria == null) {
				return produtoRepository.findByCodigoContainingAndNomeContainingAndDataCadastroGreaterThanEqualOrderByCodigoAsc(codigo, nome, dataCadastro, pageable);
			}else {
				Categoria cat = Categoria.builder().id(categoria).build();
				return produtoRepository.findByCodigoContainingAndNomeContainingAndDataCadastroGreaterThanEqualAndCategoriaOrderByCodigoAsc(codigo, nome, dataCadastro, cat, pageable);
			}
		}
	}
	
	@GetMapping("pesquisar")
	@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') and #oauth2.hasScope('read')")
	public Page<Produto> pesquisar(ProdutoFilter produtoFilter, Pageable pageable) {
		return produtoRepository.pesquisar(produtoFilter, pageable);
	}
	
	@GetMapping("portfolio")
	public Page<ProdutoPortfolio> pesquisar(PortfolioFilter portfolioFilter, Pageable pageable) {
		return produtoRepository.pesquisar(portfolioFilter, pageable);
	}
	
	@PostMapping("gerar")
	@ResponseBody
    public ResponseEntity<Resource> gerar(
    		@RequestParam String tipoArquivo,
    		@RequestParam Long idUsuario,
    		@RequestParam Long idImobiliaria,
    		@RequestBody Produto produto) {
		
		Resource file = produtoService.gerar(tipoArquivo, idUsuario, idImobiliaria, produto);
		
		BodyBuilder bodyBuilder = ResponseEntity.ok();
		bodyBuilder.contentType(tipoArquivo.equals("PNG") ? MediaType.IMAGE_PNG : MediaType.APPLICATION_PDF);
		return bodyBuilder.body(file);
	}

}
