package br.com.marketimobi.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import br.com.marketimobi.api.model.Imobiliaria;
import br.com.marketimobi.api.model.enums.StatusImobiliaria;
import br.com.marketimobi.api.repository.ImobiliariaRepository;
import br.com.marketimobi.api.repository.projection.ImobiliariaDto;
import br.com.marketimobi.api.storage.FileStorage;

@Service
public class ImobiliariaService {

	@Autowired
	private ImobiliariaRepository imobiliariaRepository;
	
	@Autowired
	private LancamentoService lancamentoService;
	
	@Autowired
	private FileStorage fileStorage;
	
	public List<ImobiliariaDto> listar(Integer primeiroRegNull) {
		List<ImobiliariaDto> imobiliarias = imobiliariaRepository.findAllImobiliariaDto();
		
		if(primeiroRegNull != null && primeiroRegNull.equals(1)) {
			if(imobiliarias == null) imobiliarias = new ArrayList<>();
			imobiliarias.add(0, null);
		}
		return imobiliarias;
	}
	
	public Imobiliaria salvar(Imobiliaria imobiliaria) {
		if (StringUtils.hasText(imobiliaria.getImagem1())) {
			fileStorage.salvar(imobiliaria.getImagem1());
		}
		
		if (StringUtils.hasText(imobiliaria.getImagem2())) {
			fileStorage.salvar(imobiliaria.getImagem2());
		}
		
		return imobiliariaRepository.save(imobiliaria);
	}
	
	public Imobiliaria atualizar(Long id, Imobiliaria imobiliaria) {
		Imobiliaria imobiliariaSalva = buscarImobiliariaPeloId(id);
		
		if (StringUtils.isEmpty(imobiliaria.getImagem1())
				&& StringUtils.hasText(imobiliariaSalva.getImagem1())) {
			fileStorage.remover(imobiliariaSalva.getImagem1());
		} else if (StringUtils.hasText(imobiliaria.getImagem1())
				&& !imobiliaria.getImagem1().equals(imobiliariaSalva.getImagem1())) {
			fileStorage.substituir(imobiliariaSalva.getImagem1(), imobiliaria.getImagem1());
		}
		
		if (StringUtils.isEmpty(imobiliaria.getImagem2())
				&& StringUtils.hasText(imobiliariaSalva.getImagem2())) {
			fileStorage.remover(imobiliariaSalva.getImagem2());
		} else if (StringUtils.hasText(imobiliaria.getImagem2())
				&& !imobiliaria.getImagem2().equals(imobiliariaSalva.getImagem2())) {
			fileStorage.substituir(imobiliariaSalva.getImagem2(), imobiliaria.getImagem2());
		}

		BeanUtils.copyProperties(imobiliaria, imobiliariaSalva, "id");
		return imobiliariaRepository.save(imobiliariaSalva);
	}
	
	public void remover(Long id) {
		Imobiliaria imobiliaria = buscarImobiliariaPeloId(id);
		
		lancamentoService.validarExistenciaLctoImobiliaria(imobiliaria);
		
		if(imobiliaria.getImagem1() != null) fileStorage.remover(imobiliaria.getImagem1());
		
		if(imobiliaria.getImagem2() != null) fileStorage.remover(imobiliaria.getImagem2());
		
		imobiliariaRepository.delete(imobiliaria);
	}
	
	public void atualizarPropriedadeStatus(Long id, String status) {
		Imobiliaria imobiliariaSalva = buscarImobiliariaPeloId(id);
		imobiliariaSalva.setStatus(StatusImobiliaria.valueOf(status));
		imobiliariaRepository.save(imobiliariaSalva);
	}
	
	public Imobiliaria buscarImobiliariaPeloId(Long id) {
		Optional<Imobiliaria> imobiliaria = imobiliariaRepository.findById(id);
		if (!imobiliaria.isPresent()) {
			throw new EmptyResultDataAccessException(1);
		}
		return imobiliaria.get();
	}
	
}
