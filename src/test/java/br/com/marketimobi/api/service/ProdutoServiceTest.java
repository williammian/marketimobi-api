package br.com.marketimobi.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.marketimobi.api.exception.ValidacaoException;
import br.com.marketimobi.api.model.Categoria;
import br.com.marketimobi.api.model.ItemProduto;
import br.com.marketimobi.api.model.Produto;
import br.com.marketimobi.api.model.enums.AlinhamentoTexto;
import br.com.marketimobi.api.model.enums.Fonte;
import br.com.marketimobi.api.model.enums.Origem;
import br.com.marketimobi.api.model.enums.Tipo;
import br.com.marketimobi.api.model.enums.TipoFonte;
import br.com.marketimobi.api.model.enums.TipoImagemSistema;
import br.com.marketimobi.api.model.enums.TipoTextoSistema;
import br.com.marketimobi.api.repository.ProdutoRepository;
import br.com.marketimobi.api.storage.FileStorage;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class ProdutoServiceTest {
	
	@SpyBean
	ProdutoService service;
	
	@MockBean
	ProdutoRepository repository;
	
	@MockBean
	private FileStorage fileStorage;
	
	@MockBean
	private UsuarioService usuarioService;
	
	@MockBean
	private ImobiliariaService imobiliariaService;
	
	@MockBean
	private LancamentoService lancamentoService;
	
	@Test
	public void deveLancarErrosAoValidarUmProduto() {
		Produto produto = new Produto();
		
		validar(produto, "Necessário informar o código do produto.");
		produto.setCodigo("999");
		
		validar(produto, "Necessário informar o nome do produto.");
		produto.setNome("Produto Teste 999");
		
		validar(produto, "Necessário informar a descrição do produto.");
		produto.setDescricao("Descrição Produto Teste 999");
		
		validar(produto, "Necessário informar a categoria do produto.");
		produto.setCategoria(Categoria.builder().id(1L).build());
		
		validar(produto, "Necessário informar se produto gera ou não PNG.");
		produto.setGerarPNG(false);
		
		validar(produto, "Necessário informar se produto gera ou não PDF.");
		produto.setGerarPDF(false);
		
		validar(produto, "Necessário informar se produto gera PNG e/ou PDF.");
		produto.setGerarPNG(true);
		produto.setGerarPDF(true);
		
		validar(produto, "Necessário informar a imagem do produto.");
		produto.setImagem("imagem.png");
		
		validar(produto, "Necessário informar a imagem do cartão do produto.");
		produto.setImagemCard("imagemCard.png");
		
		validar(produto, "Necessário informar a imagem de fundo do produto.");
		produto.setImagemFundo("imagemFundo.png");
		
		validar(produto, "Necessário informar a largura da imagem do produto.");
		produto.setLarguraImagem(500);
		
		validar(produto, "Necessário informar a altura da imagem do produto.");
		produto.setAlturaImagem(500);
		
		validar(produto, "Necessário informar a largura da imagem Card do produto.");
		produto.setLarguraImagemCard(211);
		
		validar(produto, "A largura da imagem Card do produto deve ser no máximo 210.");
		produto.setLarguraImagemCard(210);
		
		validar(produto, "Necessário informar a altura da imagem Card do produto.");
		produto.setAlturaImagemCard(211);
		
		validar(produto, "A altura da imagem Card do produto deve ser no máximo 210.");
		produto.setAlturaImagemCard(210);
		
		validar(produto, "Necessário informar a largura da imagem de amostra.");
		produto.setLarguraImagemAmostra(500);
		
		validar(produto, "Necessário informar a altura da imagem de amostra.");
		produto.setAlturaImagemAmostra(500);
		
		validar(produto, "Necessário informar a data de cadastro do produto.");
		produto.setDataCadastro(LocalDate.now());
		
		validar(produto, "Necessário informar se produto estará ou não na lista principal de produtos.");
		produto.setPrincipal(true);
		
		List<ItemProduto> items = new ArrayList<>();
		
		ItemProduto item1 = new ItemProduto();
		items.add(item1);
		produto.setItensProduto(items);
		validarItem1(produto, item1);
		
		ItemProduto item2 = new ItemProduto();
		items.add(item2);
		produto.setItensProduto(items);
		validarItem2(produto, item2);
	}
	
	private void validarItem2(Produto produto, ItemProduto item2) {
		item2.setSequencia(2);
		item2.setDescricao("Descrição Item 2");
		item2.setTipo(Tipo.TEXTO);
		item2.setOrigem(Origem.SISTEMA);
		item2.setLargura(200);
		item2.setAltura(50);
		item2.setPosicaoX(1);
		item2.setPosicaoY(50);
		
		validar(produto, "Necessário informar a fonte do item produto.\nSequência: " + item2.getSequencia() + ".");
		item2.setFonte(Fonte.ARIAL);
		
		validar(produto, "Necessário informar o tamanho da fonte do item produto.\nSequência: " + item2.getSequencia() + ".");
		item2.setTamanhoFonte(1000);
		
		validar(produto, "Tamanho da fonte do item produto deve estar entre 1 e 999.\nSequência: " + item2.getSequencia() + ".");
		item2.setTamanhoFonte(18);
		
		validar(produto, "Necessário informar a cor da fonte (R) do item produto.\nSequência: " + item2.getSequencia() + ".");
		item2.setCorFonteR(256);
		
		validar(produto, "A cor da fonte (R) do item produto deve estar entre 0 e 255.\nSequência: " + item2.getSequencia() + ".");
		item2.setCorFonteR(0);
		
		validar(produto, "Necessário informar a cor da fonte (G) do item produto.\nSequência: " + item2.getSequencia() + ".");
		item2.setCorFonteG(256);
		
		validar(produto, "A cor da fonte (G) do item produto deve estar entre 0 e 255.\nSequência: " + item2.getSequencia() + ".");
		item2.setCorFonteG(0);
		
		validar(produto, "Necessário informar a cor da fonte (B) do item produto.\nSequência: " + item2.getSequencia() + ".");
		item2.setCorFonteB(256);
		
		validar(produto, "A cor da fonte (B) do item produto deve estar entre 0 e 255.\nSequência: " + item2.getSequencia() + ".");
		item2.setCorFonteB(0);
		
		validar(produto, "Necessário informar o tipo da fonte do item produto.\nSequência: " + item2.getSequencia() + ".");
		item2.setTipoFonte(TipoFonte.NORMAL);
		
		validar(produto, "Necessário informar o alinhamento do texto do item produto.\nSequência: " + item2.getSequencia() + ".");
		item2.setAlinhamentoTexto(AlinhamentoTexto.ESQUERDA);
		
		validar(produto, "Necessário informar o tipo do texto sistema do item produto.\nSequência: " + item2.getSequencia() + ".");
		item2.setTipoTextoSistema(TipoTextoSistema.FIXO_INFORMADO);
		
		validar(produto, "Necessário informar o texto sistema do item produto.\nSequência: " + item2.getSequencia() + ".");
		item2.setTextoSistema("Teste");
		
		item2.setOrigem(Origem.USUARIO);
		validar(produto, "Necessário informar a etiqueta do item produto.\nSequência: " + item2.getSequencia() + ".");
		item2.setEtiqueta("Teste");
		
		validar(produto, "Necessário informar a quantidade máxima de caracteres do item produto.\nSequência: " + item2.getSequencia() + ".");
		item2.setQtdMaxCaracteres(20);
	}

	private void validarItem1(Produto produto, ItemProduto item1) {
		validar(produto, "Necessário informar a sequência do item produto.");
		item1.setSequencia(1);
		
		validar(produto, "Necessário informar a descrição do item produto.\nSequência: " + item1.getSequencia() + ".");
		item1.setDescricao("Descrição Item 1");
		
		validar(produto, "Necessário informar o tipo do item produto.\nSequência: " + item1.getSequencia() + ".");
		item1.setTipo(Tipo.IMAGEM);
		
		validar(produto, "Necessário informar a origem do item produto.\nSequência: " + item1.getSequencia() + ".");
		item1.setOrigem(Origem.SISTEMA);
		
		validar(produto, "Necessário informar a largura do item produto.\nSequência: " + item1.getSequencia() + ".");
		item1.setLargura(200);
		
		validar(produto, "Necessário informar a altura do item produto.\nSequência: " + item1.getSequencia() + ".");
		item1.setAltura(50);
		
		validar(produto, "Necessário informar a posição X do item produto.\nSequência: " + item1.getSequencia() + ".");
		item1.setPosicaoX(1);
		
		validar(produto, "Necessário informar a posição Y do item produto.\nSequência: " + item1.getSequencia() + ".");
		item1.setPosicaoY(1);
		
		validar(produto, "Necessário informar o tipo da imagem sistema do item produto.\nSequência: " + item1.getSequencia() + ".");
		item1.setTipoImagemSistema(TipoImagemSistema.FIXO_INFORMADO);
		
		validar(produto, "Necessário informar a imagem sistema do item produto.\nSequência: " + item1.getSequencia() + ".");
		item1.setImagemSistema("imagemSistema.png");
	}

	private void validar(Produto produto, String msg) {
		Throwable erro = catchThrowable(() -> service.validarProduto(produto));
		assertThat(erro).isInstanceOf(ValidacaoException.class).hasMessage(msg);
	}
	

}
