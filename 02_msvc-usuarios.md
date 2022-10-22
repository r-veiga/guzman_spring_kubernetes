# Creando el microservicio de USUARIOS

👉 [Volver a Readme.MD](Readme.MD)

## Valores de configuración en application.properties 

```properties
spring.application.name=msvc-usuarios
server.port=8001

spring.datasource.url=jdbc:mysql://localhost:3307/msvc_usuarios
spring.datasource.username=root
spring.datasource.password=sasa
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.generate-ddl=true
logging.level.org.hibernate.SQL=debug
```
`spring.application.name`

El nombre es muy importante, identifica mi microservicio

`server.port=8001`

Puerto de entrada a mi microservicio

`spring.datasource.url=jdbc:mysql://localhost:3307/msvc_usuarios`

Cadena de conexión a la base de datos "msvc_usuarios", la tengo dockerizada por el puerto 3307 (en vez del habitual 3306).

`spring.datasource.username=root`

Mi usuario de conexión, "root". Es el admin por defecto de MySQL.

`spring.datasource.password=sasa`

Password de mi usuario de conexión. Será la que esté configurada en la BBDD MySQL. <br>
Con el comando de arranque del contenedor Docker del MySQL yo he asignado "sasa" como contraseña.

`spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver`

Uso la versión 8 de MySQL. Necesita el ".cj.". 

`spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect`

Uso la versión 8 de MySQL, que Spring soporta (hay una versión 5 que Spring no soporta: la 5.7 o la 5.8, no sé).

`spring.jpa.generate-ddl=true`

Si no existen, se generan las tablas en base de datos según la configuración de las @Entity Java.

`logging.level.org.hibernate.SQL=debug`

Quiero trazas con las instrucciones SQL que realmente estoy ejecutando. 


## Dockerizo  MySql

El curso en este momento pide la instalación local de MySql. <br>
Pero no me lo quiero descargar e instalar en local. <br>
Voy a hacer spoiler y saltar hasta el **vídeo 72 del curso** para ver cómo crear un contenedor Docker con MySql. 

Accedo al MySql de mi contenedor por el puerto 3307, es el puerto externo que ofrezco.
Asimismo determino dos variables de entorno.
1. genera una base de datos *msvc_usuarios* (vacía)
2. la contraseña del usuario *root* será "sasa"
```bash
docker pull mysql:8
docker run -d -p 3307:3306 --name mi-mysql8 
       -e MYSQL_ROOT_PASSWORD=sasa
       -e MYSQL_DATABASE=msvc_usuarios
       mysql:8
docker ps
docker logs mi-mysql8
```

Así tengo un MySql disponible por el puerto 3307. <br>
👉  Reseño que aquí no he usado la opción **--network** que en un momento posterior comunicará los distintos contenedores que levantaré.

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



