package br.com.marketimobi.api.repository.projection;

import org.springframework.util.StringUtils;

import br.com.marketimobi.api.MarketimobiApiApplication;
import br.com.marketimobi.api.storage.FileStorage;

public class ProdutoPortfolio {
	
	private Long id;
	
	private String codigo;
	
	private String nome;
	
	private String descricao;
	
	private String imagemCard;
	
	private String urlImagemCard;
	
	private Integer larguraImagemCard;
	
	private Integer alturaImagemCard;
	
	private String imagem;
	
	private String urlImagem;
	
	private Integer larguraImagemAmostra;
	
	private Integer alturaImagemAmostra;
	
	public ProdutoPortfolio(Long id, String codigo, String nome, String descricao, 
			String imagemCard, Integer larguraImagemCard, Integer alturaImagemCard,
			String imagem, Integer larguraImagemAmostra, Integer alturaImagemAmostra) {
		super();
		
		this.id = id;
		this.codigo = codigo;
		this.nome = nome;
		this.descricao = descricao;
		
		this.imagemCard = imagemCard;
		this.larguraImagemCard = larguraImagemCard;
		this.alturaImagemCard = alturaImagemCard;
		
		this.imagem = imagem;
		this.larguraImagemAmostra = larguraImagemAmostra;
		this.alturaImagemAmostra = alturaImagemAmostra;
		
		FileStorage fileStorage = MarketimobiApiApplication.getBean(FileStorage.class);
		
		if (StringUtils.hasText(imagemCard)) {
			this.setUrlImagemCard(fileStorage.configurarUrl(imagemCard));
		}
		
		if (StringUtils.hasText(imagem)) {
			this.setUrlImagem(fileStorage.configurarUrl(imagem));
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getImagemCard() {
		return imagemCard;
	}

	public void setImagemCard(String imagemCard) {
		this.imagemCard = imagemCard;
	}

	public String getUrlImagemCard() {
		return urlImagemCard;
	}

	public void setUrlImagemCard(String urlImagemCard) {
		this.urlImagemCard = urlImagemCard;
	}

	public Integer getLarguraImagemCard() {
		return larguraImagemCard;
	}

	public void setLarguraImagemCard(Integer larguraImagemCard) {
		this.larguraImagemCard = larguraImagemCard;
	}

	public Integer getAlturaImagemCard() {
		return alturaImagemCard;
	}

	public void setAlturaImagemCard(Integer alturaImagemCard) {
		this.alturaImagemCard = alturaImagemCard;
	}

	public String getUrlImagem() {
		return urlImagem;
	}

	public void setUrlImagem(String urlImagem) {
		this.urlImagem = urlImagem;
	}

	public Integer getLarguraImagemAmostra() {
		return larguraImagemAmostra;
	}

	public void setLarguraImagemAmostra(Integer larguraImagemAmostra) {
		this.larguraImagemAmostra = larguraImagemAmostra;
	}

	public Integer getAlturaImagemAmostra() {
		return alturaImagemAmostra;
	}

	public void setAlturaImagemAmostra(Integer alturaImagemAmostra) {
		this.alturaImagemAmostra = alturaImagemAmostra;
	}

	public String getImagem() {
		return imagem;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}

}
