package br.com.marketimobi.api.model;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lancamento")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Lancamento {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "id_imobiliaria")
	private Imobiliaria imobiliaria;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "id_produto")
	private Produto produto;
	
	@NotNull
	private LocalDate data;

	@NotNull
	private LocalTime hora;
}
