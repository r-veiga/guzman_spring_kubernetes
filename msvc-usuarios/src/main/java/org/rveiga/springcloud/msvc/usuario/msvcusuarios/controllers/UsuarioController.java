package org.rveiga.springcloud.msvc.usuario.msvcusuarios.controllers;

import org.rveiga.springcloud.msvc.usuario.msvcusuarios.models.entity.Usuario;
import org.rveiga.springcloud.msvc.usuario.msvcusuarios.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class UsuarioController {

	private UsuarioService service;

	public UsuarioController(UsuarioService service) {
		this.service = service;
	}

	@GetMapping
	public List<Usuario> listar () {
		return service.listar();
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> detalle(@PathVariable Long id) {
		final Optional<Usuario> usuario = service.porId(id);
		if(usuario.isPresent()) {
			return ResponseEntity.ok(usuario.get());
		}
		return ResponseEntity.notFound().build(); // Genera la respuesta 404
	}

	@PostMapping
	// @ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Usuario> crear(@RequestBody Usuario usuario) {
		return ResponseEntity
							.status(HttpStatus.CREATED)
							.body(service.guardar(usuario));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> editar(@RequestBody Usuario modificacion, @PathVariable Long id) {
		final Optional<Usuario> userAlmacenado = service.porId(id);
		if(userAlmacenado.isPresent()) {
			final Usuario userModificado = userAlmacenado.get();
			userModificado.setNombre(modificacion.getNombre());
			userModificado.setEmail(modificacion.getEmail());
			userModificado.setPassword(modificacion.getPassword());
			return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(userModificado));
		}
		return ResponseEntity.notFound().build(); // Genera la respuesta 404
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
		Optional<Usuario> o = service.porId(id);
		if(o.isPresent()) {
			service.eliminar(id);
			return ResponseEntity.noContent().build();  // status 204 - "Sin contenido"
		}
		return ResponseEntity.notFound().build();
	}

}
