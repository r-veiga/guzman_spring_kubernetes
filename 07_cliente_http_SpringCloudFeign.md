# Escribir el cliente HTTP con Spring Cloud Feign

La referencia en el `pom.xml` está incluida desde el momento en que creé el proyecto de Cursos. 

```xml
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
```

## Configuración de Feign en la clase Main 

En la clase principal se habilita el contexto Feign para crear declarativamente clientes API.


```java
package org.rveiga.springcloud.msvc.cursos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsvcCursosApplication {
	public static void main(String[] args) {
		SpringApplication.run(MsvcCursosApplication.class, args);
	}
}
```
En un paquete nuevo, `clients`, creo un **cliente Http** con la anotación `@FeignClient`. <br/>
Mi cliente será `UsuarioClientRest` que llama a los endpoints del microservicio de usuarios.
```java
import org.rveiga.springcloud.msvc.cursos.models.Usuario;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="msvc-usuarios", url="localhost:8001")
public interface UsuarioClientRest {
    @GetMapping("/{id}") Usuario detalle(@PathVariable Long id);
    @PostMapping         Usuario crear(@RequestBody Usuario usuario);
    @GetMapping("/usuarios") List<Usuario> usuarios(@RequestParam Iterable<Long> ids);
}
```

## Usando el nuevo cliente (Feign) de la API de usuarios

Ahora, en el microservicio de cursos, tengo un cliente que hace peticiones al API de usuarios. ¿Cómo puedo usuarlo?

Lo primero es inyectar este cliente en el servicio que hará las llamadas al API. 
```java
public class CursoServiceImpl implements CursoService {

	private final CursoRepository repository;
	private final UsuarioClientRest httpClient;

	CursoServiceImpl(CursoRepository repository, UsuarioClientRest httpClient) {
		this.repository = repository;
		this.httpClient = httpClient;
	}
```
Añado los métodos en el interfaz del servicio que llamarán al API de usuarios.
```java
public interface CursoService {
	. . . 
	Optional<Usuario> asignarUsuario(Usuario usuario, Long cursoId);
	Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId);
	Optional<Usuario> eliminarUsuario(Usuario usuario, Long cursoId);

}
```
Implemento los métodos que llaman al API de usuarios con el cliente Feign. <br/>
Siguen todos un esquema similar: 
1. reciben como parámetros el ID del curso y un JSON descriptivo del usuario
2. con el ID de curso recibido, se chequea que existe en la BBDD
2. recibiendo un Json descriptivo del usuario se le 
   1. busca en el API de usuarios (acción de asignar o desasignar usuario existente al curso)
   2. crea con el API de usuarios (acción de asignar usuario que no existe a un curso)
3. crear una entidad de relación CursoUsuario
4. aplicar la entidad de relación CursoUsuario al Curso que existe en la BBDD
   1. añadir el item CursoUsuario a la lista que se guarda en `List<CursoUsuario> curso.cursoUsuario`
   2. eliminar el item CursoUsuario de la lista que se guarda en `List<CursoUsuario> curso.cursoUsuario`

```java
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
```
## Habilitar endpoints que invoquen estas llamadas

Implementación de endpoints en el Controller de Cursos con la finalidad de 
1. devolver los datos completos de los usuarios requeridos por ID
2. añadir usuario al curso
3. crear un nuevo usuario y añadirlo al curso
4. eliminar usuario del curso

```java
	@GetMapping("/usuarios")
	public ResponseEntity<?> obtenerAlumnos(@RequestParam List<Long> ids) {
		return ResponseEntity.ok(service.listarPorIds(ids));
	}

	@PutMapping("/asignar-usuario/{cursoId}")
	public ResponseEntity<?> asignarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId) {
		Optional<Usuario> asignado = null;
		try {
			asignado = service.asignarUsuario(usuario, cursoId);
		} catch (FeignException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap(
					"mensaje",
					"No existe el usuario por el ID o error en la comunicación: " + e.getMessage()
				));
		}

		if (asignado.isPresent()) {
			return ResponseEntity.status(HttpStatus.CREATED).body(asignado.get());
		}
		return ResponseEntity.notFound().build();
	}

	@PostMapping("/crear-usuario/{cursoId}")
	public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId) {
		Optional<Usuario> asignado = null;
		try {
			asignado = service.crearUsuario(usuario, cursoId);
		} catch (FeignException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap(
					"mensaje",
					"No se pudo crear el usuario o error en la comunicación: " + e.getMessage()
				));
		}

		if (asignado.isPresent()) {
			return ResponseEntity.status(HttpStatus.CREATED).body(asignado.get());
		}
		return ResponseEntity.notFound().build();
	}

	@DeleteMapping("/eliminar-usuario/{cursoId}")
	public ResponseEntity<?> eliminarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId) {
		Optional<Usuario> asignado = null;
		try {
			asignado = service.eliminarUsuario(usuario, cursoId);
		} catch (FeignException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap(
					"mensaje",
					"No existe el usuario por el ID o error en la comunicación: " + e.getMessage()
				));
		}

		if (asignado.isPresent()) {
			return ResponseEntity.status(HttpStatus.OK).body(asignado.get());
		}
		return ResponseEntity.notFound().build();
	}
```

[Siguiente](08_Dockerizando_un_microservicio.md)
