package org.rveiga.springcloud.msvc.cursos.controllers;

import org.rveiga.springcloud.msvc.cursos.entity.Curso;
import org.rveiga.springcloud.msvc.cursos.services.CursoService;
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
public class CursoController {

	private final CursoService service;

	CursoController(CursoService service) {
		this.service = service;
	}

	@GetMapping
	public ResponseEntity<List<Curso>> listar() {
		return ResponseEntity.ok(service.listar());
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> detalle(@PathVariable Long id) {
		final Optional<Curso> cursoAlmacenado = service.porId(id);
		if(cursoAlmacenado.isPresent()) {
			return ResponseEntity.ok(cursoAlmacenado.get());
		}
		return ResponseEntity.notFound().build();
	}

	@PostMapping("/")
	public ResponseEntity<?> crear(@Valid @RequestBody Curso curso, BindingResult result) {
		if (result.hasErrors()) {
			return montarErroresDeValidacion(result);
		}
		Curso cursoDb = service.guardar(curso);
		return ResponseEntity
						.status(HttpStatus.CREATED)
						.body(cursoDb);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> editar(@Valid @RequestBody Curso modificacion, BindingResult result, @PathVariable Long id) {
		if (result.hasErrors()) {
			return montarErroresDeValidacion(result);
		}
		final Optional<Curso> cursoAlmacenado = service.porId(id);
		if(cursoAlmacenado.isPresent()) {
			Curso cursoModificado = cursoAlmacenado.get();
			cursoModificado.setNombre(modificacion.getNombre());
			return ResponseEntity
							.status(HttpStatus.CREATED)
							.body(service.guardar(cursoModificado));
		}
		return ResponseEntity.notFound().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
		Optional<Curso> cursoAlmacenado = service.porId(id);
		if(cursoAlmacenado.isPresent()) {
			service.eliminar(id);
			return ResponseEntity.noContent().build();
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
