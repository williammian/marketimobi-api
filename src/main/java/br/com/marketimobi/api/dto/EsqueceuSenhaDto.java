package br.com.marketimobi.api.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class EsqueceuSenhaDto {
	String email;
	LocalDate dataNascimento;
}
