package org.rveiga.springcloud.msvc.usuario.msvcusuarios.controllers;

import org.rveiga.springcloud.msvc.usuario.msvcusuarios.models.entity.Usuario;
import org.rveiga.springcloud.msvc.usuario.msvcusuarios.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	public ResponseEntity<?> crear(@Valid @RequestBody Usuario usuario, BindingResult result) {
		if (result.hasErrors()) {
			return montarErroresDeValidacion(result);
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuario));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> editar(@Valid @RequestBody Usuario modificacion, BindingResult result, @PathVariable Long id) {
		if (result.hasErrors()) {
			return montarErroresDeValidacion(result);
		}

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

	private ResponseEntity<Map<String, String>> montarErroresDeValidacion(BindingResult result) {
		Map<String, String> errores = new HashMap<>();
		result.getFieldErrors().forEach(err -> {
			errores.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
		});
		return ResponseEntity.badRequest().body(errores);
	}
}
