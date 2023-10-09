package br.com.marketimobi.api.repository;

import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.marketimobi.api.model.Categoria;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CategoriaRepositoryTest {
	
	@Autowired
	CategoriaRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void devePersistirUmaCategoriaNaBaseDeDados() {
		//cenario
		Categoria categoria = criarCategoria();
		
		//acao
		Categoria categoriaSalva = repository.save(categoria);
		
		//verificacao
		Assertions.assertThat(categoriaSalva.getId()).isNotNull();
	}
	
	@Test
	public void deveBuscarUmaCategoriaPorId() {
		//cenario
		Categoria categoria = criarCategoria();
		entityManager.persist(categoria);
		
		//acao
		Optional<Categoria> result = repository.findById(categoria.getId());
		
		//verificacao
		Assertions.assertThat(result.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioAoBuscarCategoriaPorIdQuandoNaoExisteNaBase() {
		//cenario
		
		//acao
		Optional<Categoria> result = repository.findById(999L);
		
		//verificacao
		Assertions.assertThat(result.isPresent()).isFalse();
	}
	
	@Test(expected = ConstraintViolationException.class)
	public void deveRetornarConstraintViolationExceptionAoSalvarCategoriaSemNome() {
		//cenario
		Categoria categoria = criarCategoria();
		categoria.setNome(null);
		
		//acao
		entityManager.persist(categoria);
	}
	
	@Test(expected = ConstraintViolationException.class)
	public void deveRetornarConstraintViolationExceptionAoSalvarCategoriaSemCodigo() {
		//cenario
		Categoria categoria = criarCategoria();
		categoria.setCodigo(null);
		
		//acao
		entityManager.persist(categoria);
	}
	
	public static Categoria criarCategoria() {		
		return Categoria.builder()
				.codigo("999")
				.nome("Teste 999")
				.build();
	}

}
