package br.com.marketimobi.api.repository;

import java.time.LocalDate;
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
import br.com.marketimobi.api.model.Imobiliaria;
import br.com.marketimobi.api.model.enums.StatusImobiliaria;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ImobiliariaRepositoryTest {
	
	@Autowired
	ImobiliariaRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void devePersistirUmaImobiliariaNaBaseDeDados() {
		//cenario
		Imobiliaria imobiliaria = criarImobiliaria();
		
		//acao
		Imobiliaria imobiliariaSalva = repository.save(imobiliaria);
		
		//verificacao
		Assertions.assertThat(imobiliariaSalva.getId()).isNotNull();
	}
	
	@Test
	public void deveBuscarUmaImobiliariaPorId() {
		//cenario
		Imobiliaria imobiliaria = criarImobiliaria();
		entityManager.persist(imobiliaria);
		
		//acao
		Optional<Imobiliaria> result = repository.findById(imobiliaria.getId());
		
		//verificacao
		Assertions.assertThat(result.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioAoBuscarImobiliariaPorIdQuandoNaoExisteNaBase() {
		//cenario
		
		//acao
		Optional<Imobiliaria> result = repository.findById(999L);
		
		//verificacao
		Assertions.assertThat(result.isPresent()).isFalse();
	}
	
	@Test(expected = ConstraintViolationException.class)
	public void deveRetornarConstraintViolationExceptionAoSalvarImobiliariaSemNome() {
		//cenario
		Imobiliaria imobiliaria = criarImobiliaria();
		imobiliaria.setNome(null);
		
		//acao
		entityManager.persist(imobiliaria);
	}
	
	@Test(expected = ConstraintViolationException.class)
	public void deveRetornarConstraintViolationExceptionAoSalvarImobiliariaSemStatus() {
		//cenario
		Imobiliaria imobiliaria = criarImobiliaria();
		imobiliaria.setStatus(null);
		
		//acao
		entityManager.persist(imobiliaria);
	}
	
	@Test(expected = ConstraintViolationException.class)
	public void deveRetornarConstraintViolationExceptionAoSalvarImobiliariaSemDataCadastro() {
		//cenario
		Imobiliaria imobiliaria = criarImobiliaria();
		imobiliaria.setDataCadastro(null);
		
		//acao
		entityManager.persist(imobiliaria);
	}
	
	public static Imobiliaria criarImobiliaria() {		
		return Imobiliaria.builder()
				.nome("Teste Imobiliaria")
				.status(StatusImobiliaria.ATIVO)
				.dataCadastro(LocalDate.now())
				.build();
	}

}
