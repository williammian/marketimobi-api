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

import br.com.marketimobi.api.model.Imobiliaria;
import br.com.marketimobi.api.model.Usuario;
import br.com.marketimobi.api.model.enums.Sexo;
import br.com.marketimobi.api.model.enums.StatusUsuario;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {
	
	@Autowired
	UsuarioRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void devePersistirUmUsuarioNaBaseDeDados() {
		//cenario
		Usuario usuario = criarUsuario();
		
		//acao
		Usuario usuarioSalvo = repository.save(usuario);
		
		//verificacao
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
	}
	
	@Test
	public void deveBuscarUmUsuarioPorEmail() {
		//cenario
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		//acao
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");
		
		//verificacao
		Assertions.assertThat(result.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExisteNaBase() {
		//cenario
		
		//acao
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");
		
		//verificacao
		Assertions.assertThat(result.isPresent()).isFalse();
	}
	
	@Test(expected = ConstraintViolationException.class)
	public void deveRetornarConstraintViolationExceptionAoSalvarUsuarioSemNome() {
		//cenario
		Usuario usuario = criarUsuario();
		usuario.setNome(null);
		
		//acao
		entityManager.persist(usuario);
	}
	
	@Test(expected = ConstraintViolationException.class)
	public void deveRetornarConstraintViolationExceptionAoSalvarUsuarioSemSobrenome() {
		//cenario
		Usuario usuario = criarUsuario();
		usuario.setSobrenome(null);
		
		//acao
		entityManager.persist(usuario);
	}
	
	@Test(expected = ConstraintViolationException.class)
	public void deveRetornarConstraintViolationExceptionAoSalvarUsuarioSemSexo() {
		//cenario
		Usuario usuario = criarUsuario();
		usuario.setSexo(null);
		
		//acao
		entityManager.persist(usuario);
	}
	
	@Test(expected = ConstraintViolationException.class)
	public void deveRetornarConstraintViolationExceptionAoSalvarUsuarioSemStatus() {
		//cenario
		Usuario usuario = criarUsuario();
		usuario.setStatus(null);
		
		//acao
		entityManager.persist(usuario);
	}
	
	@Test(expected = ConstraintViolationException.class)
	public void deveRetornarConstraintViolationExceptionAoSalvarUsuarioSemDataNascimento() {
		//cenario
		Usuario usuario = criarUsuario();
		usuario.setDataNascimento(null);
		
		//acao
		entityManager.persist(usuario);
	}
	
	@Test(expected = ConstraintViolationException.class)
	public void deveRetornarConstraintViolationExceptionAoSalvarUsuarioSemDataCadastro() {
		//cenario
		Usuario usuario = criarUsuario();
		usuario.setDataCadastro(null);
		
		//acao
		entityManager.persist(usuario);
	}
	
	@Test(expected = ConstraintViolationException.class)
	public void deveRetornarConstraintViolationExceptionAoSalvarUsuarioSemEmail() {
		//cenario
		Usuario usuario = criarUsuario();
		usuario.setEmail(null);
		
		//acao
		entityManager.persist(usuario);
	}
	
	@Test(expected = ConstraintViolationException.class)
	public void deveRetornarConstraintViolationExceptionAoSalvarUsuarioSemImobiliaria() {
		//cenario
		Usuario usuario = criarUsuario();
		usuario.setImobiliaria(null);
		
		//acao
		entityManager.persist(usuario);
	}
	
	public static Usuario criarUsuario() {		
		return Usuario.builder()
				.nome("usuario")
				.sobrenome("sobrenome")
				.sexo(Sexo.MASCULINO)
				.status(StatusUsuario.ATIVO)
				.dataCadastro(LocalDate.now())
				.dataNascimento(LocalDate.now())
				.email("usuario@email.com")
				.senha("senha")
				.imobiliaria(Imobiliaria.builder().id(1L).build())
				.build();
	}

}
