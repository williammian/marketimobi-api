package br.com.marketimobi.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import br.com.marketimobi.api.exception.ValidacaoException;
import br.com.marketimobi.api.model.Categoria;
import br.com.marketimobi.api.repository.CategoriaRepository;

@Service
public class CategoriaService {
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	public List<Categoria> listar(Integer primeiroRegNull) {
		List<Categoria> categorias = categoriaRepository.findAll(Sort.by(Sort.Direction.ASC, "codigo"));
		
		if(primeiroRegNull != null && primeiroRegNull.equals(1)) {
			if(categorias == null) categorias = new ArrayList<>();
			categorias.add(0, null);
		}
		return categorias;
	}
	
	public Categoria salvar(Categoria categoria) {
		return categoriaRepository.save(categoria);
	}
	
	public Categoria atualizar(Long id, Categoria categoria) {
		validarCategoria(categoria);
		
		Categoria categoriaSalva = buscarCategoriaPeloId(id);
		
		BeanUtils.copyProperties(categoria, categoriaSalva, "id");
		return categoriaRepository.save(categoriaSalva);
	}
	
	public void remover(Long id) {
		Categoria categoria = buscarCategoriaPeloId(id);
		
		categoriaRepository.delete(categoria);
	}
	
	public Categoria buscarCategoriaPeloId(Long id) {
		Optional<Categoria> categoria = categoriaRepository.findById(id);
		if (!categoria.isPresent()) {
			throw new EmptyResultDataAccessException(1);
		}
		return categoria.get();
	}
	
	private void validarCategoria(Categoria categoria) {
		if(categoria.getCategoriaPai() != null && 
				categoria.getId().equals(categoria.getCategoriaPai().getId())) {
			throw new ValidacaoException("Categoria pai não pode ser a mesma categoria do cadastro.");
		}
	}

}
