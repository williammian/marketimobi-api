package br.com.marketimobi.api.mail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import br.com.marketimobi.api.config.property.MarketimobiApiProperty;
import br.com.marketimobi.api.model.Contato;
import br.com.marketimobi.api.model.Usuario;

@Component
public class Mailer {
	
	@Autowired
	private MarketimobiApiProperty property;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private TemplateEngine thymeleaf;
	
	public void novoContato(Contato contato) {
		Map<String, Object> variaveis = new HashMap<>();
		variaveis.put("contato", contato);
		
		this.enviarEmail(property.getMail().getRemetente(), 
				Arrays.asList(property.getMail().getRemetente()),
				"Novo Contato - Market Imobi", 
				"mail/novo-contato", 
				variaveis);
	}
	
	public void esqueceuSenha(Usuario usuario, String novaSenha) {
		Map<String, Object> variaveis = new HashMap<>();
		variaveis.put("usuario", usuario);
		variaveis.put("novaSenha", novaSenha);
		
		this.enviarEmail(property.getMail().getRemetente(), 
				Arrays.asList(usuario.getEmail()),
				"Esqueceu a senha - Market Imobi", 
				"mail/esqueceu-senha", 
				variaveis);
	}
	
	public void enviarEmail(String remetente, 
			List<String> destinatarios, String assunto, String template, 
			Map<String, Object> variaveis) {
		Context context = new Context(new Locale("pt", "BR"));
		
		variaveis.entrySet().forEach(e -> context.setVariable(e.getKey(), e.getValue()));
		
		String mensagem = thymeleaf.process(template, context);
		
		this.enviarEmail(remetente, destinatarios, assunto, mensagem);
	}
	
	public void enviarEmail(String remetente, 
			List<String> destinatarios, String assunto, String mensagem) {
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
			helper.setFrom(remetente);
			helper.setTo(destinatarios.toArray(new String[destinatarios.size()]));
			helper.setSubject(assunto);
			helper.setText(mensagem, true);
			
			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			throw new RuntimeException("Problemas com o envio de e-mail!", e); 
		}
	}

}
