package org.rveiga.springcloud.msvc.usuario.msvcusuarios.controllers;

import org.rveiga.springcloud.msvc.usuario.msvcusuarios.models.entity.Usuario;
import org.rveiga.springcloud.msvc.usuario.msvcusuarios.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

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

		if (service.porEmail(usuario.getEmail()).isPresent()) {
			String msg = "☢❗ No puedo dar de alta un usuario con un email que ya existe en el sistema.";
			return ResponseEntity.badRequest().body(Collections.singletonMap("error-1", msg));
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuario));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> editar(@Valid @RequestBody Usuario cambios, BindingResult result, @PathVariable Long id) {
		if (result.hasErrors()) {
			return montarErroresDeValidacion(result);
		}

		String emailPorVerificar = cambios.getEmail();
		Optional<Usuario> userConEmail = service.porEmail(emailPorVerificar);
		if (userConEmail.isPresent() && userConEmail.get().getId() != id) {
			String msg = "☢❗ El email ya existe en el sistema asignado a otro usuario. No puedo gestionar esta modificación.";
			return ResponseEntity.badRequest().body(Collections.singletonMap("error-1", msg));
		}

		final Optional<Usuario> usuario = service.porId(id);
		if(usuario.isPresent()) {
			final Usuario modificacion = usuario.get();
			modificacion.setId(id);
			modificacion.setNombre(cambios.getNombre());
			modificacion.setEmail(cambios.getEmail());
			modificacion.setPassword(cambios.getPassword());
			return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(modificacion));
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

	@GetMapping("/usuarios")
	public ResponseEntity<?> obtenerAlumnos(@RequestParam List<Long> ids) {
		return ResponseEntity.ok(service.listarPorIds(ids));
	}

	private ResponseEntity<Map<String, String>> montarErroresDeValidacion(BindingResult result) {
		Map<String, String> errores = new HashMap<>();
		result.getFieldErrors().forEach(err -> {
			errores.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
		});
		return ResponseEntity.badRequest().body(errores);
	}
}
