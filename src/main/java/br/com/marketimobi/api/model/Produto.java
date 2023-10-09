package br.com.marketimobi.api.model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.com.marketimobi.api.repository.listener.ProdutoListener;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EntityListeners(ProdutoListener.class)
@Entity(name = "produto")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Produto {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@Column(unique = true)
	@Size(min = 3, max = 10)
	private String codigo;
	
	@NotNull
	@Size(min = 3, max = 30)
	private String nome;
	
	@NotNull
	@Size(min = 3, max = 50)
	private String descricao;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "id_categoria")
	@JsonIgnoreProperties({"categoriaPai", "subcategorias"})
	private Categoria categoria;
	
	@NotNull
	@Column(name = "gerar_png")
	private Boolean gerarPNG;
	
	@NotNull
	@Column(name = "gerar_pdf")
	private Boolean gerarPDF;
	
	@Size(max = 250)
	@NotNull
	private String imagem;
	
	@Transient
	private String urlImagem;
	
	@Size(max = 250)
	@NotNull
	@Column(name = "imagem_Card")
	private String imagemCard;
	
	@Transient
	private String urlImagemCard;
	
	@NotNull
	@Column(name = "largura_imagem")
	private Integer larguraImagem;
	
	@NotNull
	@Column(name = "altura_imagem")
	private Integer alturaImagem;
	
	@NotNull
	@Column(name = "largura_imagem_card")
	private Integer larguraImagemCard;
	
	@NotNull
	@Column(name = "altura_imagem_card")
	private Integer alturaImagemCard;
	
	@NotNull
	@Column(name = "largura_imagem_amostra")
	private Integer larguraImagemAmostra;
	
	@NotNull
	@Column(name = "altura_imagem_amostra")
	private Integer alturaImagemAmostra;
	
	@Size(max = 250)
	@NotNull
	@Column(name = "imagem_fundo")
	private String imagemFundo;
	
	@Transient
	private String urlImagemFundo;
	
	@NotNull
	@Column(name = "data_cadastro")
	private LocalDate dataCadastro;
	
	@NotNull
	private Boolean principal;
	
	@JsonIgnoreProperties("produto")
	@Valid
	@OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ItemProduto> itensProduto;

}
