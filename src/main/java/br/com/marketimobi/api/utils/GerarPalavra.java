package br.com.marketimobi.api.utils;

public class GerarPalavra {
	
	public static void main(String[] args) {
		String letras = "0123456789abcdefghijklmnopqrstuvxwyzABCDEFGHIJKLMNOPQRSTUVXWYZ$&(@)(!";
		
		String palavra = "";
		for(int i = 0; i < 10; i++){
			int aleatorio = (int) (Math.random() * (letras.length() -1) );
			String letra = letras.substring(aleatorio, aleatorio+1);
			palavra = palavra + letra;
		}
		
		System.out.println(palavra);
		
	}

}
