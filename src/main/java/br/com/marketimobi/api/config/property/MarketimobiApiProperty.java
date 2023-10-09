package br.com.marketimobi.api.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("marketimobi")
public class MarketimobiApiProperty {
	
	private String originPermitida = "http://localhost:4200";
	
	private String server = "http://localhost:8080";
	
	private final Seguranca seguranca = new Seguranca();
	
	private final Mail mail = new Mail();
	
	private final S3 s3 = new S3();
	
	public String getOriginPermitida() {
		return originPermitida;
	}

	public void setOriginPermitida(String originPermitida) {
		this.originPermitida = originPermitida;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}
	
	public Seguranca getSeguranca() {
		return seguranca;
	}
	
	public Mail getMail() {
		return mail;
	}
	
	public S3 getS3() {
		return s3;
	}
	
	public static class Seguranca {
		
		private boolean enableHttps;

		public boolean isEnableHttps() {
			return enableHttps;
		}

		public void setEnableHttps(boolean enableHttps) {
			this.enableHttps = enableHttps;
		}
	
	}
	
	public static class Mail {
		
		private String remetente;
		
		private String host;
		
		private Integer port;
		
		private String username;
		
		private String password;

		public String getRemetente() {
			return remetente;
		}

		public void setRemetente(String remetente) {
			this.remetente = remetente;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}
	
	public static class S3 {
		
		private String accessKeyId;
		
		private String secretAccessKey;
		
		private String bucket;
		
		public String getBucket() {
			return bucket;
		}
		
		public void setBucket(String bucket) {
			this.bucket = bucket;
		}

		public String getAccessKeyId() {
			return accessKeyId;
		}

		public void setAccessKeyId(String accessKeyId) {
			this.accessKeyId = accessKeyId;
		}

		public String getSecretAccessKey() {
			return secretAccessKey;
		}

		public void setSecretAccessKey(String secretAccessKey) {
			this.secretAccessKey = secretAccessKey;
		}
	}

}
