package br.com.marketimobi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class AlterarSenhaDto {
	String senhaAtual;
	String senhaNova;
}
