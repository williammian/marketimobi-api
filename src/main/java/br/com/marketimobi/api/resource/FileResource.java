package br.com.marketimobi.api.resource;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.marketimobi.api.dto.Arquivo;
import br.com.marketimobi.api.exception.ValidacaoException;
import br.com.marketimobi.api.storage.FileStorage;

@RestController
@RequestMapping("/files")
public class FileResource {
	
	@Autowired
	private FileStorage fileStorage;
	
	@PostMapping("/upload")
	//@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write')")
	public Arquivo upload(@RequestParam MultipartFile arquivo) {
		String nome = fileStorage.salvarTemporariamente(arquivo);
		return new Arquivo(nome, fileStorage.configurarUrl(nome));
	}
	
	@GetMapping("/download/{fileName:.+}")
	//@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
    @ResponseBody
    public ResponseEntity<Resource> download(@PathVariable String fileName) {
		BufferedImage bi = null;
		ByteArrayOutputStream baos = null;
		ByteArrayResource bar = null;
		
		try {
			bi = fileStorage.loadFile(fileName);
	        
	        BodyBuilder bodyBuilder = ResponseEntity.ok();
	        
	        String extensaoDoArquivo = getFileExtension(fileName);
	        
	        if(extensaoDoArquivo.equalsIgnoreCase("JPG") || extensaoDoArquivo.equalsIgnoreCase("JPEG")) {
	        	bodyBuilder.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"");
	        	bodyBuilder.header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE);
	        }else if(extensaoDoArquivo.equalsIgnoreCase("GIF")) {
	        	bodyBuilder.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"");
	        	bodyBuilder.header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_GIF_VALUE);
	        }else if(extensaoDoArquivo.equalsIgnoreCase("PNG")) {
	        	bodyBuilder.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"");
	        	bodyBuilder.header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE);
	        }else {
	        	throw new ValidacaoException("Arquivo com extensão inválida.");
	        }
	        
	        baos = new ByteArrayOutputStream();
	    	ImageIO.write(bi, extensaoDoArquivo, baos);
	    	byte[] imageInByte = baos.toByteArray();
	        bar = new ByteArrayResource(imageInByte);
	        
	        return bodyBuilder.body(bar);
		}catch (Exception err) {
			throw new RuntimeException("Erro ao efetuar download de arquivo.", err);
		}finally {
			try {
				if(bi != null) {
					bi.flush();
					bi = null;
				}
				
				if(baos != null) {
					baos.flush();
					baos.close();
					baos = null;
				}
				
				bar = null;
			}catch (Exception err) {
				err.printStackTrace();
			}
		}
	}
	
	private String getFileExtension(String fileName) {
		if(fileName.contains("."))
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		else
			return "";
	}

}
