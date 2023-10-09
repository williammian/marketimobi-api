package br.com.marketimobi.api.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.PngImage;

import br.com.marketimobi.api.exception.ValidacaoException;
import br.com.marketimobi.api.model.Imobiliaria;
import br.com.marketimobi.api.model.ItemProduto;
import br.com.marketimobi.api.model.Produto;
import br.com.marketimobi.api.model.Usuario;
import br.com.marketimobi.api.model.enums.AlinhamentoTexto;
import br.com.marketimobi.api.model.enums.Origem;
import br.com.marketimobi.api.model.enums.Tipo;
import br.com.marketimobi.api.model.enums.TipoFonte;
import br.com.marketimobi.api.model.enums.TipoImagemSistema;
import br.com.marketimobi.api.model.enums.TipoTextoSistema;
import br.com.marketimobi.api.repository.ProdutoRepository;
import br.com.marketimobi.api.repository.projection.ProdutoDto;
import br.com.marketimobi.api.storage.FileStorage;

@Service
public class ProdutoService {
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private FileStorage fileStorage;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private ImobiliariaService imobiliariaService;
	
	@Autowired
	private LancamentoService lancamentoService;
	
	public List<ProdutoDto> listarProdutoDto(Integer primeiroRegNull) {
		List<ProdutoDto> produtos = produtoRepository.findAllProdutoDto();
		
		if(primeiroRegNull != null && primeiroRegNull.equals(1)) {
			if(produtos == null) produtos = new ArrayList<>();
			produtos.add(0, null);
		}
		return produtos;
	}
	
	public Produto salvar(Produto produto) {
		validarProduto(produto);
		
		produto.getItensProduto().forEach(i -> i.setProduto(produto));
		
		salvarImagem(produto.getImagem());
		salvarImagem(produto.getImagemCard());
		salvarImagem(produto.getImagemFundo());
		
		salvarImagemItensProduto(produto.getItensProduto());
		
		return produtoRepository.save(produto);
	}
	
	public Produto atualizar(Long id, Produto produto) {
		validarProduto(produto);
		
		Produto produtoSalvo = buscarProdutoPeloId(id);
		
		atualizarImagemItensProdutos(produtoSalvo.getItensProduto(), produto.getItensProduto());
		
		produtoSalvo.getItensProduto().clear();
		produtoSalvo.getItensProduto().addAll(produto.getItensProduto());
		produtoSalvo.getItensProduto().forEach(i -> i.setProduto(produtoSalvo));
		
		atualizarImagem(produtoSalvo.getImagem(), produto.getImagem());
		atualizarImagem(produtoSalvo.getImagemCard(), produto.getImagemCard());
		atualizarImagem(produtoSalvo.getImagemFundo(), produto.getImagemFundo());

		BeanUtils.copyProperties(produto, produtoSalvo, "id", "itensProduto");
		return produtoRepository.save(produtoSalvo);
	}
	
	public void remover(Long id) {
		Produto produto = buscarProdutoPeloId(id);
		
		lancamentoService.validarExistenciaLctoProduto(produto);
		
		removerImagem(produto.getImagem());
		removerImagem(produto.getImagemCard());
		removerImagem(produto.getImagemFundo());
		
		removerImagemItensProduto(produto.getItensProduto());
		
		produtoRepository.delete(produto);
	}
	
	public Produto buscarProdutoPeloId(Long id) {
		Optional<Produto> produto = produtoRepository.findById(id);
		if (!produto.isPresent()) {
			throw new EmptyResultDataAccessException(1);
		}
		return produto.get();
	}
	
	public void validarProduto(Produto produto) {
		if(StringUtils.isEmpty(produto.getCodigo())) throw new ValidacaoException("Necessário informar o código do produto.");
		
		if(StringUtils.isEmpty(produto.getNome())) throw new ValidacaoException("Necessário informar o nome do produto.");
		
		if(StringUtils.isEmpty(produto.getDescricao())) throw new ValidacaoException("Necessário informar a descrição do produto.");
		
		if(produto.getCategoria() == null || produto.getCategoria().getId() == null) {
			throw new ValidacaoException("Necessário informar a categoria do produto.");
		}
		
		if(produto.getGerarPNG() == null) throw new ValidacaoException("Necessário informar se produto gera ou não PNG.");
		
		if(produto.getGerarPDF() == null) throw new ValidacaoException("Necessário informar se produto gera ou não PDF.");
		
		if(!produto.getGerarPNG() && !produto.getGerarPDF()) throw new ValidacaoException("Necessário informar se produto gera PNG e/ou PDF.");
		
		if(StringUtils.isEmpty(produto.getImagem())) throw new ValidacaoException("Necessário informar a imagem do produto.");
		
		if(StringUtils.isEmpty(produto.getImagemCard())) throw new ValidacaoException("Necessário informar a imagem do cartão do produto.");
		
		if(StringUtils.isEmpty(produto.getImagemFundo())) throw new ValidacaoException("Necessário informar a imagem de fundo do produto.");
		
		if(produto.getLarguraImagem() == null) throw new ValidacaoException("Necessário informar a largura da imagem do produto.");
		
		if(produto.getAlturaImagem() == null) throw new ValidacaoException("Necessário informar a altura da imagem do produto.");
		
		if(produto.getLarguraImagemCard() == null) throw new ValidacaoException("Necessário informar a largura da imagem Card do produto.");
		
		if(produto.getLarguraImagemCard().compareTo(210) > 0) throw new ValidacaoException("A largura da imagem Card do produto deve ser no máximo 210.");
		
		if(produto.getAlturaImagemCard() == null) throw new ValidacaoException("Necessário informar a altura da imagem Card do produto.");
		
		if(produto.getAlturaImagemCard().compareTo(210) > 0) throw new ValidacaoException("A altura da imagem Card do produto deve ser no máximo 210.");
		
		if(produto.getLarguraImagemAmostra() == null) throw new ValidacaoException("Necessário informar a largura da imagem de amostra.");
		
		if(produto.getAlturaImagemAmostra() == null) throw new ValidacaoException("Necessário informar a altura da imagem de amostra.");
		
		if(produto.getDataCadastro() == null) throw new ValidacaoException("Necessário informar a data de cadastro do produto.");
		
		if(produto.getPrincipal() == null) throw new ValidacaoException("Necessário informar se produto estará ou não na lista principal de produtos.");
		
		if(!CollectionUtils.isEmpty(produto.getItensProduto())) {
			produto.getItensProduto().forEach(itemProduto -> validarItemProduto(itemProduto));
		}
	}
	
	private void validarItemProduto(ItemProduto itemProduto) {
		if(itemProduto.getSequencia() == null) throw new ValidacaoException("Necessário informar a sequência do item produto.");
		
		try {
			
			if(StringUtils.isEmpty(itemProduto.getDescricao())) throw new ValidacaoException("Necessário informar a descrição do item produto.");
			
			if(itemProduto.getTipo() == null) throw new ValidacaoException("Necessário informar o tipo do item produto.");
			
			if(itemProduto.getOrigem() == null) throw new ValidacaoException("Necessário informar a origem do item produto.");
			
			if(itemProduto.getLargura() == null) throw new ValidacaoException("Necessário informar a largura do item produto.");
			
			if(itemProduto.getAltura() == null) throw new ValidacaoException("Necessário informar a altura do item produto.");
			
			if(itemProduto.getPosicaoX() == null) throw new ValidacaoException("Necessário informar a posição X do item produto.");
			
			if(itemProduto.getPosicaoY() == null) throw new ValidacaoException("Necessário informar a posição Y do item produto.");
			
			if(itemProduto.getOrigem().equals(Origem.USUARIO)) {
				if(StringUtils.isEmpty(itemProduto.getEtiqueta())) throw new ValidacaoException("Necessário informar a etiqueta do item produto.");
			}
			
			//IMAGEM
			if(itemProduto.getTipo().equals(Tipo.IMAGEM)) {
				//SISTEMA
				if(itemProduto.getOrigem().equals(Origem.SISTEMA)) {
					if(itemProduto.getTipoImagemSistema() == null) {
						throw new ValidacaoException("Necessário informar o tipo da imagem sistema do item produto.");
					}
					
					if(itemProduto.getTipoImagemSistema().equals(TipoImagemSistema.FIXO_INFORMADO)) {
						if(itemProduto.getImagemSistema() == null) {
							throw new ValidacaoException("Necessário informar a imagem sistema do item produto.");
						}
					}
				}
				
			//TEXTO
			}else {
				if(itemProduto.getFonte() == null) {
					throw new ValidacaoException("Necessário informar a fonte do item produto.");
				}
				
				if(itemProduto.getTamanhoFonte() == null) {
					throw new ValidacaoException("Necessário informar o tamanho da fonte do item produto.");
				}
				if(itemProduto.getTamanhoFonte().compareTo(1) < 0 || itemProduto.getTamanhoFonte().compareTo(999) > 0) {
					throw new ValidacaoException("Tamanho da fonte do item produto deve estar entre 1 e 999.");
				}
				
				if(itemProduto.getCorFonteR() == null) {
					throw new ValidacaoException("Necessário informar a cor da fonte (R) do item produto.");
				}
				if(itemProduto.getCorFonteR().compareTo(0) < 0 || itemProduto.getCorFonteR().compareTo(255) > 0) {
					throw new ValidacaoException("A cor da fonte (R) do item produto deve estar entre 0 e 255.");
				}
				
				if(itemProduto.getCorFonteG() == null) {
					throw new ValidacaoException("Necessário informar a cor da fonte (G) do item produto.");
				}
				if(itemProduto.getCorFonteG().compareTo(0) < 0 || itemProduto.getCorFonteG().compareTo(255) > 0) {
					throw new ValidacaoException("A cor da fonte (G) do item produto deve estar entre 0 e 255.");
				}
				
				if(itemProduto.getCorFonteB() == null) {
					throw new ValidacaoException("Necessário informar a cor da fonte (B) do item produto.");
				}
				if(itemProduto.getCorFonteB().compareTo(0) < 0 || itemProduto.getCorFonteB().compareTo(255) > 0) {
					throw new ValidacaoException("A cor da fonte (B) do item produto deve estar entre 0 e 255.");
				}
				
				if(itemProduto.getTipoFonte() == null) {
					throw new ValidacaoException("Necessário informar o tipo da fonte do item produto.");
				}
				
				if(itemProduto.getAlinhamentoTexto() == null) {
					throw new ValidacaoException("Necessário informar o alinhamento do texto do item produto.");
				}
				
				//SISTEMA
				if(itemProduto.getOrigem().equals(Origem.SISTEMA)) {
					if(itemProduto.getTipoTextoSistema() == null) {
						throw new ValidacaoException("Necessário informar o tipo do texto sistema do item produto.");
					}
					
					if(itemProduto.getTipoTextoSistema().equals(TipoTextoSistema.FIXO_INFORMADO)) {
						if(itemProduto.getTextoSistema() == null) {
							throw new ValidacaoException("Necessário informar o texto sistema do item produto.");
						}
					}
					
				//USUARIO
				}else {				
					if(itemProduto.getQtdMaxCaracteres() == null) {
						throw new ValidacaoException("Necessário informar a quantidade máxima de caracteres do item produto.");
					}
				}
			}
			
		}catch(ValidacaoException err) {
			throw new ValidacaoException(err.getMessage() + "\nSequência: " + itemProduto.getSequencia() + ".");
		}
	}
	
	private void salvarImagem(String imagem) {
		if (StringUtils.hasText(imagem)) {
			fileStorage.salvar(imagem);
		}
	}
	
	private void atualizarImagem(String imagemSalva, String imagem) {
		if (StringUtils.isEmpty(imagem)
				&& StringUtils.hasText(imagemSalva)) {
			fileStorage.remover(imagemSalva);
		} else if (StringUtils.hasText(imagem)
				&& !imagem.equals(imagemSalva)) {
			fileStorage.substituir(imagemSalva, imagem);
		}
	}
	
	private void removerImagem(String imagem) {
		if (imagem != null) fileStorage.remover(imagem);
	}
	
	private void salvarImagemItensProduto(List<ItemProduto> itensProduto) {
		if(itensProduto != null && itensProduto.size() > 0) {
			for(ItemProduto itemProduto: itensProduto) {
				salvarImagem(itemProduto.getImagemSistema());
			}
		}
	}
	
	private void removerImagemItensProduto(List<ItemProduto> itensProduto) {
		if(itensProduto != null && itensProduto.size() > 0) {
			for(ItemProduto itemProduto: itensProduto) {
				removerImagem(itemProduto.getImagemSistema());
			}
		}
	}
	
	private void atualizarImagemItensProdutos(List<ItemProduto> itensProdutoSalvo, List<ItemProduto> itensProduto) {
		if(!CollectionUtils.isEmpty(itensProduto)) {
			for(ItemProduto itemProduto : itensProduto) {
				boolean contemItemProduto = false;
				
				if(!CollectionUtils.isEmpty(itensProdutoSalvo)) {
					for(ItemProduto itemProdutoSalvo : itensProdutoSalvo) {
						if(itemProduto.getId() == null) continue;
						
						if(itemProduto.getId().equals(itemProdutoSalvo.getId())) {
							atualizarImagem(itemProdutoSalvo.getImagemSistema(), itemProduto.getImagemSistema());					
							contemItemProduto = true;
							break;
						}
					}
				}
				
				if(!contemItemProduto) salvarImagem(itemProduto.getImagemSistema());
			}
		}
		
		if(!CollectionUtils.isEmpty(itensProdutoSalvo)) {
			for(ItemProduto itemProdutoSalvo : itensProdutoSalvo) {
				boolean contemItemProdutoSalvo = false;
				
				if(!CollectionUtils.isEmpty(itensProduto)) {
					for(ItemProduto itemProduto : itensProduto) {
						if(itemProdutoSalvo.getId().equals(itemProduto.getId())) {
							contemItemProdutoSalvo = true;
							break;
						}
					}
				}
				
				if(!contemItemProdutoSalvo) removerImagem(itemProdutoSalvo.getImagemSistema());
			}
		}
	}
	
	public Resource gerar(String tipoArquivo, Long idUsuario, Long idImobiliaria, Produto produto) {
		BufferedImage bufImgPrincipal = null;
		Graphics2D graphics = null;
		ByteArrayOutputStream baos = null;
		ByteArrayResource br = null;
		try {
			Usuario usuario = usuarioService.buscarUsuarioPeloId(idUsuario);
			
			Imobiliaria imobiliaria = imobiliariaService.buscarImobiliariaPeloId(idImobiliaria);
			
			if(produto.getImagemFundo() == null) 
				throw new ValidacaoException("Não foi definida a imagem de fundo para gerar o arquivo.");
			
			bufImgPrincipal = fileStorage.loadFile(produto.getImagemFundo());
			
			graphics = bufImgPrincipal.createGraphics();
			
			//Percorrendo item a item do produto
			if(!CollectionUtils.isEmpty(produto.getItensProduto())) {
				TreeSet<ItemProduto> itensProdutoOrdem = new TreeSet<ItemProduto>(new Comparator<ItemProduto>() {
					public int compare(ItemProduto o1, ItemProduto o2) {
						if(o1.getSequencia().compareTo(o2.getSequencia()) == 0) {
							return o1.getId().compareTo(o2.getId());
						}else {
							return o1.getSequencia().compareTo(o2.getSequencia());
						}
					}
				});
				itensProdutoOrdem.addAll(produto.getItensProduto());
				
				for(ItemProduto itemProduto : itensProdutoOrdem) {
					adicionarItemProdutoNaImagem(itemProduto, usuario, imobiliaria, graphics);
				}
			}
			
			baos = new ByteArrayOutputStream();
			ImageIO.write(bufImgPrincipal, "png", baos);
			
			if(tipoArquivo.equals("PNG")) {
				br = new ByteArrayResource(baos.toByteArray());
			}else {
				br = gerarPDF(baos);
			}
		    
		    try {
		    	lancamentoService.gravarLancamento(imobiliaria, usuario, produto);
		    }catch (Exception e) {
				e.printStackTrace();
			}
			
			return br;
		}catch (Exception e) {
			throw new RuntimeException("Erro ao gerar arquivo.", e);
		}finally {
			try {
				if(bufImgPrincipal != null) {
					bufImgPrincipal.flush();
					bufImgPrincipal = null;
				}
				
				if(graphics != null) {
					graphics.dispose();
					graphics = null;
				}
				
				if(baos != null) {
					baos.flush();
					baos.close();
					baos = null;
				}
				
				br = null;
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void adicionarItemProdutoNaImagem(ItemProduto itemProduto, Usuario usuario, Imobiliaria imobiliaria, Graphics2D graphics) throws Exception {
		if(itemProduto.getTipo().equals(Tipo.IMAGEM)) {
			//IMAGEM
			String strImagemItem = null;
			
			if(itemProduto.getOrigem().equals(Origem.USUARIO)) {
				strImagemItem = itemProduto.getImagemUsuario();
			}else {
				strImagemItem = obterImagemSistema(itemProduto, usuario, imobiliaria);
			}
			
			if(strImagemItem == null) return;
			
			BufferedImage bufImgItem = fileStorage.loadFile(strImagemItem);
			
			graphics.drawImage(bufImgItem, itemProduto.getPosicaoX(), itemProduto.getPosicaoY(), itemProduto.getLargura(), itemProduto.getAltura(), null);
			
			bufImgItem.flush();
			bufImgItem = null;
			
		}else {
			//TEXTO
			String strTextoItem = null;
			
			if(itemProduto.getOrigem().equals(Origem.USUARIO)) {
				strTextoItem = itemProduto.getTextoUsuario();
			}else {
				strTextoItem = obterTextoSistema(itemProduto, usuario, imobiliaria);
			}
			
			if(strTextoItem == null) return;
			
			Color color = new Color(itemProduto.getCorFonteR(), itemProduto.getCorFonteG(), itemProduto.getCorFonteB());
			graphics.setColor(color);
			
			int style = Font.PLAIN;
			if(itemProduto.getTipoFonte().equals(TipoFonte.NEGRITO)) {
				style = Font.BOLD;
			}else if(itemProduto.getTipoFonte().equals(TipoFonte.ITALICO)) {
				style = Font.ITALIC;
			}else if(itemProduto.getTipoFonte().equals(TipoFonte.NEGRITO_ITALICO)) {
				style = Font.BOLD + Font.ITALIC;
			}
			
			Font font = new Font(itemProduto.getFonte().getDescricao(), style, itemProduto.getTamanhoFonte());
			graphics.setFont(font);
			
			if(itemProduto.getAlinhamentoTexto().equals(AlinhamentoTexto.ESQUERDA)) {
				graphics.drawString(strTextoItem, itemProduto.getPosicaoX(), itemProduto.getPosicaoY());
			}else if(itemProduto.getAlinhamentoTexto().equals(AlinhamentoTexto.CENTRO)) {
				printCenterString(graphics, strTextoItem, itemProduto.getLargura(), itemProduto.getPosicaoX(), itemProduto.getPosicaoY());
			}else if(itemProduto.getAlinhamentoTexto().equals(AlinhamentoTexto.CENTRO)) {
				printRightString(graphics, strTextoItem, itemProduto.getLargura(), itemProduto.getPosicaoX(), itemProduto.getPosicaoY());
			}
			
		}
	}
	
	private static void printCenterString(Graphics2D g2d, String s, int width, int XPos, int YPos){
        int stringLen = (int)g2d.getFontMetrics().getStringBounds(s, g2d).getWidth();
        int start = width/2 - stringLen/2;
        g2d.drawString(s, start + XPos, YPos);
	}
	
	private static void printRightString(Graphics2D g2d, String s, int width, int XPos, int YPos) {
		int stringLen = (int)g2d.getFontMetrics().getStringBounds(s, g2d).getWidth();
		g2d.drawString(s, width - (stringLen+2), YPos);		
	}

	private String obterTextoSistema(ItemProduto itemProduto, Usuario usuario, Imobiliaria imobiliaria) {
		String strTextoItem = null;
		
		switch (itemProduto.getTipoTextoSistema()) {
			case FIXO_INFORMADO:
				strTextoItem = itemProduto.getTextoSistema(); break;
	
			case IMOBILIARIA_NOME:
				strTextoItem = imobiliaria.getNome(); break;
				
			case IMOBILIARIA_RAZAO_SOCIAL:
				strTextoItem = imobiliaria.getRazaoSocial(); break;
				
			case IMOBILIARIA_TELEFONE_FIXO:
				strTextoItem = imobiliaria.getTelefoneFixo(); break;
				
			case IMOBILIARIA_TELEFONE_CELULAR:
				strTextoItem = imobiliaria.getTelefoneCelular(); break;
				
			case IMOBILIARIA_LOGRADOURO:
				strTextoItem = imobiliaria.getLogradouro(); break;
				
			case IMOBILIARIA_COMPLEMENTO:
				strTextoItem = imobiliaria.getComplemento(); break;
				
			case IMOBILIARIA_BAIRRO:
				strTextoItem = imobiliaria.getBairro(); break;
				
			case IMOBILIARIA_CEP:
				strTextoItem = imobiliaria.getCep(); break;
				
			case IMOBILIARIA_CIDADE:
				strTextoItem = imobiliaria.getCidade(); break;
				
			case IMOBILIARIA_ESTADO:
				strTextoItem = imobiliaria.getEstado(); break;
				
			case IMOBILIARIA_EMAIL:
				strTextoItem = imobiliaria.getEmail(); break;
				
			case IMOBILIARIA_SITE:
				strTextoItem = imobiliaria.getSite(); break;
				
			case IMOBILIARIA_CRECI:
				strTextoItem = imobiliaria.getCreci(); break;
				
			case IMOBILIARIA_CNPJ:
				strTextoItem = imobiliaria.getCnpj(); break;
				
			case USUARIO_NOME:
				strTextoItem = usuario.getNome(); break;
				
			case USUARIO_SOBRENOME:
				strTextoItem = usuario.getSobrenome(); break;
				
			case USUARIO_NOME_PROFISSIONAL:
				strTextoItem = usuario.getNomeProfissional(); break;
				
			case USUARIO_TELEFONE_FIXO:
				strTextoItem = usuario.getTelefoneFixo(); break;
				
			case USUARIO_TELEFONE_CELULAR:
				strTextoItem = usuario.getTelefoneCelular(); break;
				
			case USUARIO_CARGO:
				strTextoItem = usuario.getCargo(); break;
				
			case USUARIO_CRECI:
				strTextoItem = usuario.getCreci(); break;
				
			case USUARIO_EMAIL:
				strTextoItem = usuario.getEmail(); break;
			
			case USUARIO_SITE:
				strTextoItem = usuario.getSite(); break;
		}

		return strTextoItem;
	}

	private String obterImagemSistema(ItemProduto itemProduto, Usuario usuario, Imobiliaria imobiliaria) {
		String strImagemItem = null;
		
		switch (itemProduto.getTipoImagemSistema()) {
			case FIXO_INFORMADO:
				strImagemItem = itemProduto.getImagemSistema(); break;
				
			case IMOBILIARIA_IMAGEM1:
				strImagemItem = imobiliaria.getImagem1(); break;
				
			case IMOBILIARIA_IMAGEM2:
				strImagemItem = imobiliaria.getImagem2(); break;
				
			case USUARIO_IMAGEM1:
				strImagemItem = usuario.getImagem1(); break;
				
			case USUARIO_IMAGEM2:
				strImagemItem = usuario.getImagem2(); break;
		}
		
		return strImagemItem;
	}

	private ByteArrayResource gerarPDF(ByteArrayOutputStream baos) {
		ByteArrayInputStream bais = null;
		Image image = null;
		Rectangle A4 = null;
		ByteArrayOutputStream baosPDF = null;
		Document document = null;
		try {
			bais = new ByteArrayInputStream(baos.toByteArray());
			image = PngImage.getImage(bais);
			
			A4 = PageSize.A4;

	        float scalePortrait = Math.min(A4.getWidth() / image.getWidth(),
	                A4.getHeight() / image.getHeight());
	        
	        float w = image.getWidth() * scalePortrait;
	        float h = image.getHeight() * scalePortrait;
	        
	        image.scaleAbsolute(w, h);
            float posH = (A4.getHeight() - h) / 2;
            float posW = (A4.getWidth() - w) / 2;

            image.setAbsolutePosition(posW, posH);
            image.setBorder(Image.NO_BORDER);
            image.setBorderWidth(0);
			
			baosPDF = new ByteArrayOutputStream();
			
			document = new Document(PageSize.A4, 0, 0, 0, 0);
		    PdfWriter.getInstance(document, baosPDF);
		    
		    document.open();
		    document.add(image);
		    document.close();
		    
		    ByteArrayResource br = new ByteArrayResource(baosPDF.toByteArray());
		    
		    return br;
		}catch (Exception err) {
			throw new RuntimeException("Erro ao gerar PDF.", err);
		}finally {
			try {
				document = null;
				
				image = null;
				
				A4 = null;
				
				if(baosPDF != null) {
					baosPDF.flush();
					baosPDF.close();
					baosPDF = null;
				}
				
				if(bais != null) {
					bais.close();
					bais = null;
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	

}
