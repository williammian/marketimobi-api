package br.com.marketimobi.api.storage;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.SetObjectTaggingRequest;
import com.amazonaws.services.s3.model.Tag;

import br.com.marketimobi.api.config.property.MarketimobiApiProperty;

@Profile("s3")
@Component
public class S3Storage implements FileStorage {
	
	private static final Logger logger = LoggerFactory.getLogger(S3Storage.class);
	
	private MarketimobiApiProperty property;
	
	private AmazonS3 amazonS3;
	
	@Autowired
	public S3Storage(MarketimobiApiProperty property, AmazonS3 amazonS3) {
		this.property = property;
		this.amazonS3 = amazonS3;
		
		this.init();
	}

	@Override
	public String salvarTemporariamente(MultipartFile arquivo) {
		AccessControlList acl = null;
		ObjectMetadata objectMetadata = null;
		InputStream is = null;
		PutObjectRequest putObjectRequest = null;
		
		try {
			acl = new AccessControlList();
			acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
			
			objectMetadata = new ObjectMetadata();
			objectMetadata.setContentType(arquivo.getContentType());
			objectMetadata.setContentLength(arquivo.getSize());
			
			String nomeUnico = gerarNomeUnico(arquivo.getOriginalFilename());
			
			is = arquivo.getInputStream();
			
			putObjectRequest = new PutObjectRequest(
					property.getS3().getBucket(),
					nomeUnico,
					is, 
					objectMetadata)
					.withAccessControlList(acl);
			
			putObjectRequest.setTagging(new ObjectTagging(Arrays.asList(new Tag("expirar", "true"))));
			
			amazonS3.putObject(putObjectRequest);
			
			if (logger.isDebugEnabled()) {
				logger.debug("Arquivo {} enviado com sucesso para o S3.", arquivo.getOriginalFilename());
			}
			
			return nomeUnico;
		}catch (Exception err) {
			throw new RuntimeException("Problemas ao tentar enviar o arquivo para o S3.", err);
		}finally {
			try {
				acl = null;
				
				objectMetadata = null;
				
				if(is != null) {
					is.close();
					is = null;
				}
				
				putObjectRequest = null;
				
			} catch (Exception e) {
				logger.debug("Erro ao fechar requisições");
				e.printStackTrace();
			}
		}
	}

	@Override
	public String configurarUrl(String objeto) {
		return "https://" + property.getS3().getBucket() + ".s3.amazonaws.com/" + objeto;
	}

	@Override
	public void salvar(String objeto) {
		SetObjectTaggingRequest setObjectTaggingRequest = new SetObjectTaggingRequest(
				property.getS3().getBucket(), 
				objeto, 
				new ObjectTagging(Collections.emptyList()));
		
		amazonS3.setObjectTagging(setObjectTaggingRequest);
	}

	@Override
	public void remover(String objeto) {
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(
				property.getS3().getBucket(), objeto);
		
		amazonS3.deleteObject(deleteObjectRequest);
	}

	@Override
	public void substituir(String objetoAntigo, String objetoNovo) {
		if (StringUtils.hasText(objetoAntigo)) {
			this.remover(objetoAntigo);
		}
		
		salvar(objetoNovo);
	}

	@Override
	public BufferedImage loadFile(String fileName) {
		GetObjectRequest getObjectRequest = null;
		S3Object s3Object = null;
		S3ObjectInputStream s3ois = null;
		BufferedInputStream bis = null;
		InputStreamResource isr = null;
		InputStream is = null;
		BufferedImage bi = null;
		
		try {
			getObjectRequest = new GetObjectRequest(property.getS3().getBucket(), fileName);
			s3Object = amazonS3.getObject(getObjectRequest);	
			s3ois = s3Object.getObjectContent();
			bis = new BufferedInputStream(s3ois);
			isr = new InputStreamResource(bis);
			is = isr.getInputStream();
			bi = ImageIO.read(is);
			return bi;
		}catch (Exception err) {
			throw new RuntimeException("Erro ao carregar arquivo do S3.", err);
		}finally {
			try {
				getObjectRequest = null;
				
				if (s3Object != null) {
					s3Object.close();
					s3Object = null;
				}
				
				if (s3ois != null) {
					s3ois.close();
					s3ois = null;
				}
				
				if (bis != null) {
					bis.close();
					bis = null;
				}
				
				isr = null;
				
				if (is != null) {
					is.close();
					is = null;
				}
				
				if (bi != null) {
					bi.flush();
					bi = null;
				}
			} catch (Exception e) {
				logger.debug("Erro ao fechar requisições");
				e.printStackTrace();
			}
		}
	}

	@Override
	public void init() {
		logger.info("");
		logger.info("Storage S3 inicializado");
    	logger.info("Bucket: " + property.getS3().getBucket());
    	logger.info("");
	}

}
