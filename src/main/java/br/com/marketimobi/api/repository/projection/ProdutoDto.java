package br.com.marketimobi.api.repository.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ProdutoDto {
	Long id;
	String codigo;
	String nome;
}
