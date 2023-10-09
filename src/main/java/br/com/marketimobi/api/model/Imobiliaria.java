package br.com.marketimobi.api.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.marketimobi.api.model.enums.StatusImobiliaria;
import br.com.marketimobi.api.repository.listener.ImobiliariaListener;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EntityListeners(ImobiliariaListener.class)
@Entity(name = "imobiliaria")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Imobiliaria {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@Column(unique = true)
	@Size(min = 3, max = 50)
	private String nome;
	
	@Column(name = "razao_social")
	@Size(max = 50)
	private String razaoSocial;
	
	@Enumerated(value = EnumType.STRING)
	@NotNull
	private StatusImobiliaria status;
	
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
	private String logradouro;
	
	@Size(max = 30)
	private String complemento;
	
	@Size(max = 30)
	private String bairro;
	
	@Size(max = 10)
	private String cep;
	
	@Size(max = 30)
	private String cidade;
	
	@Size(max = 30)
	private String estado;
	
	@Email
	@Size(max = 50)
	private String email;
	
	@Size(max = 50)
	private String site;
	
	@Size(max = 20)
	private String creci;
	
	@Size(max = 20)
	private String cnpj;
	
	@Size(max = 250)
	private String imagem1;
	
	@Transient
	private String urlImagem1;
	
	@Size(max = 250)
	private String imagem2;
	
	@Transient
	private String urlImagem2;

}
