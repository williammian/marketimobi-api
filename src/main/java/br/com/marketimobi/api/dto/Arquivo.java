package br.com.marketimobi.api.dto;

public class Arquivo {
	
private String nome;
	
	private String url;

	public Arquivo(String nome, String url) {
		this.nome = nome;
		this.url = url;
	}
	
	public String getNome() {
		return nome;
	}
	
	public String getUrl() {
		return url;
	}

}
