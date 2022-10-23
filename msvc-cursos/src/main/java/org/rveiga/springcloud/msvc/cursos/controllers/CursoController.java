package org.rveiga.springcloud.msvc.cursos.controllers;

import org.rveiga.springcloud.msvc.cursos.entity.Curso;
import org.rveiga.springcloud.msvc.cursos.services.CursoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
	public ResponseEntity<?> crear(@RequestBody Curso curso) {
		Curso cursoDb = service.guardar(curso);
		return ResponseEntity
						.status(HttpStatus.CREATED)
						.body(cursoDb);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> editar(@RequestBody Curso modificacion, @PathVariable Long id) {
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
}
