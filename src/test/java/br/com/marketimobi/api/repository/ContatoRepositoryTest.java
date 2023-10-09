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

import br.com.marketimobi.api.model.Contato;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ContatoRepositoryTest {

	@Autowired
	ContatoRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void devePersistirUmContatoNaBaseDeDados() {
		//cenario
		Contato contato = criarContato();
		
		//acao
		Contato contatoSalvo = repository.save(contato);
		
		//verificacao
		Assertions.assertThat(contatoSalvo.getId()).isNotNull();
	}
	
	@Test
	public void deveBuscarUmContatoPorId() {
		//cenario
		Contato contato = criarContato();
		entityManager.persist(contato);
		
		//acao
		Optional<Contato> result = repository.findById(contato.getId());
		
		//verificacao
		Assertions.assertThat(result.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioAoBuscarContatoPorIdQuandoNaoExisteNaBase() {
		//cenario
		
		//acao
		Optional<Contato> result = repository.findById(999L);
		
		//verificacao
		Assertions.assertThat(result.isPresent()).isFalse();
	}
	
	@Test(expected = ConstraintViolationException.class)
	public void deveRetornarConstraintViolationExceptionAoSalvarContatoSemNome() {
		//cenario
		Contato contato = criarContato();
		contato.setNome(null);
		
		//acao
		entityManager.persist(contato);
	}
	
	@Test(expected = ConstraintViolationException.class)
	public void deveRetornarConstraintViolationExceptionAoSalvarContatoSemEmail() {
		//cenario
		Contato contato = criarContato();
		contato.setEmail(null);
		
		//acao
		entityManager.persist(contato);
	}
	
	@Test(expected = ConstraintViolationException.class)
	public void deveRetornarConstraintViolationExceptionAoSalvarContatoSemConferido() {
		//cenario
		Contato contato = criarContato();
		contato.setConferido(null);
		
		//acao
		entityManager.persist(contato);
	}
	
	@Test(expected = ConstraintViolationException.class)
	public void deveRetornarConstraintViolationExceptionAoSalvarContatoSemDataCadastro() {
		//cenario
		Contato contato = criarContato();
		contato.setDataCadastro(null);
		
		//acao
		entityManager.persist(contato);
	}
	
	public static Contato criarContato() {		
		return Contato.builder()
				.nome("Teste Contato")
				.email("testecontato@email.com")
				.conferido(true)
				.dataCadastro(LocalDate.now())
				.build();
	}
	
}
