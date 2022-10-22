# Creando el microservicio de USUARIOS

👉 [Volver a Readme.MD](Readme.MD)

## Valores de configuración en application.properties 

```properties
spring.application.name=msvc-usuarios
server.port=8001
```
`spring.application.name`

El nombre es muy importante, identifica mi microservicio

`server.port=8001`

Puerto de entrada a mi microservicio

## Puntos a destacar 

### (1) El endpoint devuelve status (200, 201, 404...)

Se devuelve una `@ResponseEntity<?>` 

```java
@GetMapping("/{id}")
public ResponseEntity<?> listar(@PathVariable Long id) {
    final Optional<Usuario> usuario = service.porId(id);
    if(usuario.isPresent()) {
        return ResponseEntity.ok(usuario.get());
    }
    return ResponseEntity.notFound().build(); // Genera la respuesta 404
}
```
También puedo indicar cuál será el **ResponseStatus** devuelto con una anotación al método. 

```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED) // Devuelve el status 201 "Creado"
public Usuario crear(@RequestBody Usuario usuario) {
    return service.guardar(usuario);
}
```
Si sobre el caso anterior en vez de la anotación prefiriese usar un **ResponseEntity**

```java
@PostMapping
// @ResponseStatus(HttpStatus.CREATED) 
public ResponseEntity<Usuario> crear(@RequestBody Usuario usuario) {
	return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(service.guardar(usuario));
}
```
Uso el PUT para modificar datos del usuario.
Ojo que este PUT recibe un @RequestBody y un @PathVariable. 

```java 
@PutMapping("/{id}")
public ResponseEntity<?> editar(@RequestBody Usuario modificacion, @PathVariable Long id) {
    final Optional<Usuario> userAlmacenado = service.porId(id);
    if(userAlmacenado.isPresent()) {
        final Usuario userModificado = userAlmacenado.get();
        userModificado.setNombre(modificacion.getNombre());
        userModificado.setEmail(modificacion.getEmail());
        userModificado.setPassword(modificacion.getPassword());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.guardar(userModificado));
    }
    return ResponseEntity.notFound().build(); // Genera la respuesta 404
}
```
DELETE devuelve un 204 "No Content" que es un OK. 

```java
@DeleteMapping("/{id}")
public ResponseEntity<?> eliminar(@PathVariable Long id) {
    Optional<Usuario> o = service.porId(id);
    if(o.isPresent()) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();  // status 204 - "Sin contenido"
    }
    return ResponseEntity.notFound().build();
}
```



