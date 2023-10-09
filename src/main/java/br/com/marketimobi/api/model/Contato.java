package br.com.marketimobi.api.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contato")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Contato {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(min = 3, max = 50)
	private String nome;
	
	@NotNull
	@Email
	@Size(min = 3, max = 50)
	private String email;
	
	@Size(max = 20)
	private String telefone;
	
	@Size(max = 50)
	private String assunto;
	
	@Size(max = 500)
	private String mensagem;
	
	@NotNull
	private Boolean conferido;
	
	@NotNull
	@Column(name = "data_cadastro")
	private LocalDate dataCadastro;
	
}
