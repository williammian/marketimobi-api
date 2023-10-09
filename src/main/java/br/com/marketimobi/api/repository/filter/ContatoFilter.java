package br.com.marketimobi.api.repository.filter;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContatoFilter {
	
	String nome;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	LocalDate dataCadastro;
	
	Integer conferido;

}
