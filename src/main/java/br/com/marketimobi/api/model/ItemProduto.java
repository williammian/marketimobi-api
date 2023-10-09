package br.com.marketimobi.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.marketimobi.api.model.enums.AlinhamentoTexto;
import br.com.marketimobi.api.model.enums.Fonte;
import br.com.marketimobi.api.model.enums.Origem;
import br.com.marketimobi.api.model.enums.Tipo;
import br.com.marketimobi.api.model.enums.TipoFonte;
import br.com.marketimobi.api.model.enums.TipoImagemSistema;
import br.com.marketimobi.api.model.enums.TipoTextoSistema;
import br.com.marketimobi.api.repository.listener.ItemProdutoListener;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EntityListeners(ItemProdutoListener.class)
@Entity
@Table(name = "item_produto")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ItemProduto {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	private Integer sequencia;
	
	@Size(min = 3, max = 50)
	@NotNull
	private String descricao;
	
	@Size(min = 3, max = 30)
	private String etiqueta;
	
	@Size(min = 3, max = 30)
	@Column(name = "orientacao_usuario")
	private String orientacaoUsuario;
	
	@Enumerated(value = EnumType.STRING)
	@NotNull
	private Tipo tipo;
	
	@Enumerated(value = EnumType.STRING)
	@NotNull
	private Origem origem;
	
	@NotNull
	private Integer largura;
	
	@NotNull
	private Integer altura;
	
	@NotNull
	@Column(name = "posicao_x")
	private Integer posicaoX;
	
	@NotNull
	@Column(name = "posicao_y")
	private Integer posicaoY;
	
	@Enumerated(value = EnumType.STRING)
	@Column(name = "tipo_imagem_sistema")
	private TipoImagemSistema tipoImagemSistema;
	
	@Size(max = 250)
	@Column(name = "imagem_sistema")
	private String imagemSistema;
	
	@Transient
	private String urlImagemSistema;
	
	@Transient
	private String imagemUsuario;
	
	@Transient
	private String urlImagemUsuario;
	
	@Enumerated(value = EnumType.STRING)
	@Column(name = "tipo_texto_sistema")
	private TipoTextoSistema tipoTextoSistema;
	
	@Size(max = 100)
	@Column(name = "texto_sistema")
	private String textoSistema;
	
	@Transient
	private String textoUsuario;
	
	@Column(name = "qtd_max_caracteres")
	private Integer qtdMaxCaracteres;
	
	@Enumerated(value = EnumType.STRING)
	private Fonte fonte;
	
	@Column(name = "tamanho_fonte")
	private Integer tamanhoFonte;
	
	@Column(name = "cor_fonte_r")
	private Integer corFonteR;
	
	@Column(name = "cor_fonte_g")
	private Integer corFonteG;
	
	@Column(name = "cor_fonte_b")
	private Integer corFonteB;
	
	@Enumerated(value = EnumType.STRING)
	@Column(name = "tipo_fonte")
	private TipoFonte tipoFonte;
	
	@Enumerated(value = EnumType.STRING)
	@Column(name = "alinhamento_texto")
	private AlinhamentoTexto alinhamentoTexto;
	
	@ManyToOne
	@JoinColumn(name = "id_produto")
	private Produto produto;
	
}
