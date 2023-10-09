package br.com.marketimobi.api.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeradorSenha {
	
	public static void main(String[] args) {
		String senha = "market1914!";
		System.out.println("Senha:\n" + senha);
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String senhaEncoded = encoder.encode(senha);
		System.out.println("\nSenha encoded:\n" + senhaEncoded);
		
		boolean isSenhaCorreta = encoder.matches(senha, senhaEncoded);
		System.out.println("\nSenha correta: " + isSenhaCorreta);
	}

}
