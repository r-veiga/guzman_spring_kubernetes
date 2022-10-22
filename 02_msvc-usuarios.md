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

### (1) Creación del primer endpoint en el controller

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
