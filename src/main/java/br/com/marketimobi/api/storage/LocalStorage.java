package br.com.marketimobi.api.storage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import br.com.marketimobi.api.config.property.MarketimobiApiProperty;

@Profile("local")
@Component
public class LocalStorage implements FileStorage {
	
	private static final Logger logger = LoggerFactory.getLogger(LocalStorage.class);
		
	private final MarketimobiApiProperty property;
	
	private final Path locationTemp;
	private final Path locationStorage;
	
	@Autowired
	public LocalStorage(MarketimobiApiProperty property) {
		this.property = property;
		
		//java.io.tmpdir = pasta de arquivos temporários do SO
		String pathTemp = System.getProperty("java.io.tmpdir");
		
		//user.home = pasta de usuário do SO
		//user.dir = pasta raiz da aplicação
		String pathStorage = System.getProperty("user.dir") + "\\marketimobi\\";
		
		this.locationTemp = Paths.get(pathTemp);
		this.locationStorage = Paths.get(pathStorage);
		
		this.init();
	}
	
	@Override
	public String salvarTemporariamente(MultipartFile arquivo) {
		String nomeUnico = gerarNomeUnico(arquivo.getOriginalFilename());
		
		try {
			Files.copy(arquivo.getInputStream(), this.locationTemp.resolve(nomeUnico));
			return nomeUnico;
		}catch (IOException e) {
			throw new RuntimeException("Problemas ao tentar salvar o arquivo temporariamente.", e);
		}
	}
	
	@Override
	public String configurarUrl(String objeto) {
		return property.getServer() + "/files/download/" + objeto;
	}

	@Override
	public void salvar(String objeto) {
		try {
			//CopyOption[] options = new CopyOption[]{ StandardCopyOption.ATOMIC_MOVE };
			//Files.move(this.locationTemp.resolve(objeto), this.locationStorage.resolve(objeto), options);
			Files.copy(this.locationTemp.resolve(objeto), this.locationStorage.resolve(objeto));
		}catch (IOException e) {
			throw new RuntimeException("Problemas ao tentar salvar o arquivo.", e);
		}
	}

	@Override
	public void remover(String objeto) {
		try {
			Files.deleteIfExists(this.locationTemp.resolve(objeto));
			Files.deleteIfExists(this.locationStorage.resolve(objeto));
		}catch (IOException e) {
			throw new RuntimeException("Problemas ao tentar remover o arquivo.", e);
		}
	}

	@Override
	public void substituir(String objetoAntigo, String objetoNovo) {
		if (StringUtils.hasText(objetoAntigo)) {
			this.remover(objetoAntigo);
		}
		salvar(objetoNovo);
	}

//	@Override
//	public byte[] loadFile(String fileName) {
//		try {
//            byte[] file = Files.readAllBytes(this.location.resolve(fileName));
//            return file;
//        } catch (IOException e) {
//            throw new RuntimeException("Falha ao carregar arquivo.");
//        }
//	}
	
	@Override
	public BufferedImage loadFile(String fileName) {
		try {
            Path file = locationStorage.resolve(fileName);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return ImageIO.read(resource.getInputStream());
            } else {
            	file = locationTemp.resolve(fileName);
            	resource = new UrlResource(file.toUri());
            	if(resource.exists() || resource.isReadable()) {
                    return ImageIO.read(resource.getInputStream());
            	} else {
            		throw new RuntimeException("Falha ao carregar arquivo.");
            	}
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Falha ao carregar arquivo.");
        } catch (IOException e) {
        	throw new RuntimeException("Falha ao carregar arquivo.");
        }
	}
	
	public void init() {
        try {
        	logger.info("");
        	logger.info("Inicializando Storage Local");
        	
        	if(!Files.exists(locationStorage)) {
        		Files.createDirectory(locationStorage);
        	}
        	
        	logger.info("Path Temporário: " + locationTemp.toAbsolutePath());
        	logger.info("Path Armazenamento: " + locationStorage.toAbsolutePath());
        	
        	logger.info("Storage Local inicializado");
        	logger.info("");
        } catch (Exception err) {
        	logger.error("Não foi possível inicializar o storage!", err);
            throw new RuntimeException("Não foi possível inicializar o storage!", err);
        }
    }

}
