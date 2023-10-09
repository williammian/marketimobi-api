package br.com.marketimobi.api.service;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import br.com.marketimobi.api.mail.Mailer;
import br.com.marketimobi.api.model.Contato;
import br.com.marketimobi.api.repository.ContatoRepository;

@Service
public class ContatoService {
	
	@Autowired
	private ContatoRepository contatoRepository;
	
	@Autowired
	private Mailer mailer;
	
	public Contato salvar(Contato contato, boolean enviarEmail) {
		Contato contatoSalvo = contatoRepository.save(contato);
		if(enviarEmail) mailer.novoContato(contatoSalvo);
		return contatoSalvo;
	}
	
	public Contato atualizar(Long id, Contato contato) {
		Contato contatoSalvo = buscarContatoPeloId(id);
		
		BeanUtils.copyProperties(contato, contatoSalvo, "id");
		return contatoRepository.save(contatoSalvo);
	}
	
	public void remover(Long id) {
		Contato contato = buscarContatoPeloId(id);
		
		contatoRepository.delete(contato);
	}
	
	public void atualizarPropriedadeConferido(Long id, Boolean conferido) {
		Contato contatoSalvo = buscarContatoPeloId(id);
		contatoSalvo.setConferido(conferido);
		contatoRepository.save(contatoSalvo);
	}
	
	public Contato buscarContatoPeloId(Long id) {
		Optional<Contato> contato = contatoRepository.findById(id);
		if (!contato.isPresent()) {
			throw new EmptyResultDataAccessException(1);
		}
		return contato.get();
	}

}
