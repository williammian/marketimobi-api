package br.com.marketimobi.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categoria")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Categoria {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@Column(unique = true)
	@Size(min = 1, max = 10)
	private String codigo;

	@NotNull
	@Column(unique = true)
	@Size(min = 3, max = 30)
	private String nome;
	
	@ManyToOne
	@JoinColumn(name = "categoria_pai_id")
	@JsonIgnoreProperties({"categoriaPai", "subcategorias"})
	private Categoria categoriaPai;
	
	@OneToMany(mappedBy = "categoriaPai", cascade = CascadeType.ALL)
	@JsonIgnoreProperties({"categoriaPai"})
	@OrderBy("codigo ASC")
	private List<Categoria> subcategorias = new ArrayList<>();
	
}
