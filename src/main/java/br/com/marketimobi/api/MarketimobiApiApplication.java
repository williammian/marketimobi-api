package br.com.marketimobi.api;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

import br.com.marketimobi.api.config.property.MarketimobiApiProperty;
import br.com.marketimobi.api.utils.Gerenciador;

@SpringBootApplication
@EnableConfigurationProperties(MarketimobiApiProperty.class)
public class MarketimobiApiApplication {
	
	private static final Logger logger = LoggerFactory.getLogger(MarketimobiApiApplication.class);
	
	private static ApplicationContext APPLICATION_CONTEXT;

	public static void main(String[] args) {
		logger.info("Iniciando servidor Market Imobi ...");
		
		System.setProperty("spring.profiles.active", "prod, oauth-security, s3");
		//System.setProperty("spring.profiles.active", "dev, oauth-security, local");
		
		APPLICATION_CONTEXT = SpringApplication.run(MarketimobiApiApplication.class, args);
		
		registrarFontes();
		
		setarTimeZone();
		
		logger.info("Servidor Market Imobi inicializado com sucesso!");
	}
	
	public static <T> T getBean(Class<T> type) {
		return APPLICATION_CONTEXT.getBean(type);
	}
	
	private static void registrarFontes() {
		try {
			logger.info("Registrando fontes");
			
			String[] fontes = {"ARIAL.TTF", "BRUSHSC.TTF", "CALIBRI.TTF", "TAHOMA.TTF", "TIMES.TTF"};
			
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			
			for(String fonte : fontes) {
				InputStream is = new Gerenciador().getClass().getClassLoader()
				.getResourceAsStream("fontes" + File.separator + fonte);
				
				Font font = Font.createFont(Font.TRUETYPE_FONT, is);
				logger.info(font.getFontName());
				
				ge.registerFont(font);
			}
			
			logger.info("Fontes registradas");
		}catch (Exception err) {
			logger.error("Erro ao registrar fontes.", err);
			throw new RuntimeException("Erro ao registrar fontes.", err);
		}
	}
	
    private static void setarTimeZone(){
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
        logger.info("Spring boot application rodando em UTC timezone : " + TimeZone.getDefault());
        logger.info("Data/hora inicio servidor: " + LocalDateTime.now());
    }

}
