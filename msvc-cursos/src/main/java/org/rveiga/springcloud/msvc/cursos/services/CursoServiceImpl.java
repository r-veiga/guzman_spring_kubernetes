package org.rveiga.springcloud.msvc.cursos.services;

import org.rveiga.springcloud.msvc.cursos.clients.UsuarioClientRest;
import org.rveiga.springcloud.msvc.cursos.models.Usuario;
import org.rveiga.springcloud.msvc.cursos.models.entity.Curso;
import org.rveiga.springcloud.msvc.cursos.models.entity.CursoUsuario;
import org.rveiga.springcloud.msvc.cursos.repositories.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CursoServiceImpl implements CursoService {

	private final CursoRepository repository;
	private final UsuarioClientRest httpClient;

	CursoServiceImpl(CursoRepository repository, UsuarioClientRest httpClient) {
		this.repository = repository;
		this.httpClient = httpClient;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Curso> listar() {
		return (List<Curso>) repository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Curso> porId(Long id) {
		return repository.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Curso> porIdConUsuarios(Long id) {
		Optional<Curso> byId = repository.findById(id);
		if(byId.isPresent()) {
			Curso curso = byId.get();
			if (!curso.getCursoUsuarios().isEmpty()) {
				List<Long> ids = curso.getCursoUsuarios().stream()
						.map(CursoUsuario::getUsuarioId)
						.toList();
				List<Usuario> usuarios = httpClient.usuarios(ids);
				curso.setUsuarios(usuarios);
			}
			return Optional.of(curso);
		}
		return Optional.empty();
	}

	@Override
	@Transactional
	public Curso guardar(Curso curso) {
		return repository.save(curso);
	}

	@Override
	@Transactional
	public void eliminar(Long id) {
		repository.deleteById(id);
	}

	@Override
	@Transactional
	public void eliminarCursoUsuarioPorId(Long id) {
		repository.eliminarCursoUsuarioPorId(id);
	}

	@Override
	@Transactional
	public Optional<Usuario> asignarUsuario(Usuario usuario, Long cursoId) {
		Optional<Curso> cursoStored = repository.findById(cursoId);
		if (cursoStored.isPresent()) {
			Usuario usuarioMsvc = httpClient.detalle(usuario.getId());

			Curso curso = cursoStored.get();
			CursoUsuario cursoUsuario = new CursoUsuario();
			cursoUsuario.setUsuarioId(usuarioMsvc.getId());

			curso.addCursoUsuario(cursoUsuario);
			repository.save(curso);
			return Optional.of(usuarioMsvc);
		}
		return Optional.empty();
	}

	@Override
	@Transactional
	public Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId) {
		Optional<Curso> cursoStored = repository.findById(cursoId);
		if (cursoStored.isPresent()) {
			Usuario usuarioMsvc = httpClient.crear(usuario);

			Curso curso = cursoStored.get();
			CursoUsuario cursoUsuario = new CursoUsuario();
			cursoUsuario.setUsuarioId(usuarioMsvc.getId());

			curso.addCursoUsuario(cursoUsuario);
			repository.save(curso);
			return Optional.of(usuarioMsvc);
		}
		return Optional.empty();
	}

	@Override
	@Transactional
	public Optional<Usuario> eliminarUsuario(Usuario usuario, Long cursoId) {
		Optional<Curso> cursoStored = repository.findById(cursoId);
		if (cursoStored.isPresent()) {
			Usuario usuarioMsvc = httpClient.detalle(usuario.getId());

			Curso curso = cursoStored.get();
			CursoUsuario cursoUsuario = new CursoUsuario();
			cursoUsuario.setUsuarioId(usuarioMsvc.getId());

			curso.removeCursoUsuario(cursoUsuario);
			repository.save(curso);
			return Optional.of(usuarioMsvc);
		}
		return Optional.empty();
	}
}
