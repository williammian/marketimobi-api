package br.com.marketimobi.api.storage;

import java.awt.image.BufferedImage;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public interface FileStorage {
	
	public String salvarTemporariamente(MultipartFile arquivo);
		
	public String configurarUrl(String objeto);
	
	public void salvar(String objeto);

	public void remover(String objeto);
	
	public void substituir(String objetoAntigo, String objetoNovo);
	
	//public byte[] loadFile(String fileName);
	
	public BufferedImage loadFile(String fileName);
	
	public void init();
	
	default String gerarNomeUnico(String originalFilename) {
		return UUID.randomUUID().toString() + "_" + originalFilename;
	}

}
