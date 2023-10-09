package br.com.marketimobi.api.model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.thymeleaf.spring5.processor.SpringUErrorsTagProcessor;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.marketimobi.api.model.enums.Sexo;
import br.com.marketimobi.api.model.enums.StatusUsuario;
import br.com.marketimobi.api.repository.listener.UsuarioListener;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EntityListeners(UsuarioListener.class)
@Entity
@Table(name = "usuario")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Usuario {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(unique = true)
	@Size(min = 3, max = 50)
	private String nome;
	
	@NotNull
	@Size(min = 3, max = 50)
	private String sobrenome;
	
	@Column(name = "nome_profissional")
	@Size(max = 50)
	private String nomeProfissional;
	
	@Enumerated(value = EnumType.STRING)
	@NotNull
	private Sexo sexo;
	
	@Enumerated(value = EnumType.STRING)
	@NotNull
	private StatusUsuario status;
	
	@NotNull
	@Column(name = "data_nascimento")
	private LocalDate dataNascimento;
	
	@NotNull
	@Column(name = "data_cadastro")
	private LocalDate dataCadastro;
	
	@Size(max = 20)
	@Column(name = "telefone_fixo")
	private String telefoneFixo;
	
	@Size(max = 20)
	@Column(name = "telefone_celular")
	private String telefoneCelular;
	
	@Size(max = 50)
	private String cargo;
	
	@Size(max = 20)
	private String creci;
	
	@Size(max = 50)
	private String site;
	
	@NotNull
	@Column(unique = true)
	@Email
	@Size(min = 3, max = 50)
	private String email;

	//@NotNull
	@Size(min = 3, max = 150)
	@JsonIgnore
	private String senha;
	
	@Transient
	private String senhaInformada;
	
	@Size(min = 3, max = 250)
	private String imagem1;
	
	@Transient
	private String urlImagem1;
	
	@Size(min = 3, max = 250)
	private String imagem2;
	
	@Transient
	private String urlImagem2;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "id_imobiliaria")
	private Imobiliaria imobiliaria;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "usuario_permissao", joinColumns = @JoinColumn(name = "id_usuario")
		, inverseJoinColumns = @JoinColumn(name = "id_permissao"))
	private List<Permissao> permissoes;
	
	public boolean contemPermissao(String descricaoPermissao) {
		boolean contemPermissao = false;
		if(this.getPermissoes() == null || this.getPermissoes().size() == 0) return contemPermissao;
		
		for(Permissao permissao : this.getPermissoes()) {
			if(permissao.getDescricao().equalsIgnoreCase(descricaoPermissao)) {
				contemPermissao = true;
				break;
			}
		}
		return contemPermissao;
	}
	
}
