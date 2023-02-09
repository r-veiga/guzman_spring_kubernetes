package org.rveiga.springcloud.msvc.usuario.msvcusuarios.services;

import org.rveiga.springcloud.msvc.usuario.msvcusuarios.models.entity.Usuario;
import org.rveiga.springcloud.msvc.usuario.msvcusuarios.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	@Autowired
	private UsuarioRepository repository;

	@Override
	@Transactional(readOnly = true)
	public List<Usuario> listar() {
		return (List<Usuario>) repository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Usuario> porId(Long id) {
		return repository.findById(id);
	}

	@Override
	@Transactional
	public Usuario guardar(Usuario usuario) {
		return repository.save(usuario);
	}

	@Override
	@Transactional
	public void eliminar(Long id) {
		repository.deleteById(id);
	}

	@Override
	@Transactional
	public List<Usuario> listarPorIds(Iterable<Long> ids) {
		return (List<Usuario>) repository.findAllById(ids);
	}

	@Override
	@Transactional
	public Optional<Usuario> porEmail(String email) {
		return repository.findByEmail(email);
	}
}
