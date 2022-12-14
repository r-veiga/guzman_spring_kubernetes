# Creando el microservicio de USUARIOS

馃憠 [Volver a Readme.MD](Readme.MD)

## Valores de configuraci贸n en application.properties

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

Cadena de conexi贸n a la base de datos "msvc_usuarios", la tengo dockerizada por el puerto 3307 (en vez del habitual 3306).

`spring.datasource.username=root`

Mi usuario de conexi贸n, "root". Es el admin por defecto de MySQL.

`spring.datasource.password=sasa`

Password de mi usuario de conexi贸n. Ser谩 la que est茅 configurada en la BBDD MySQL. <br>
Con el comando de arranque del contenedor Docker del MySQL yo he asignado "sasa" como contrase帽a.

`spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver`

Uso la versi贸n 8 de MySQL. Necesita el ".cj.". 

`spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect`

Uso la versi贸n 8 de MySQL, que Spring soporta (hay una versi贸n 5 que Spring no soporta: la 5.7 o la 5.8, no s茅).

`spring.jpa.generate-ddl=true`

Si no existen, se generan las tablas en base de datos seg煤n la configuraci贸n de las @Entity Java.

`logging.level.org.hibernate.SQL=debug`

Quiero trazas con las instrucciones SQL que realmente estoy ejecutando. 


## Dockerizo  MySql

El curso en este momento pide la instalaci贸n local de MySql. <br>
Pero no me lo quiero descargar e instalar en local. <br>
Voy a hacer spoiler y saltar hasta el **v铆deo 72 del curso** para ver c贸mo crear un contenedor Docker con MySql. 

Accedo al MySql de mi contenedor por el puerto 3307, es el puerto externo que ofrezco.
Asimismo determino dos variables de entorno.
1. genera una base de datos *msvc_usuarios* (vac铆a)
2. la contrase帽a del usuario *root* ser谩 "sasa"
```bash
docker pull mysql:8
docker run -d -p 3307:3306 --name guzman-mysql8 
       -e MYSQL_ROOT_PASSWORD=sasa
       -e MYSQL_DATABASE=msvc_usuarios
       mysql:8
docker ps
docker logs guzman-mysql8
```

As铆 tengo un MySql disponible por el puerto 3307. <br>
馃憠  Rese帽o que aqu铆 no he usado la opci贸n **--network** que en un momento posterior comunicar谩 los distintos contenedores que levantar茅.

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
Tambi茅n puedo indicar cu谩l ser谩 el **ResponseStatus** devuelto con una anotaci贸n al m茅todo. 

```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED) // Devuelve el status 201 "Creado"
public Usuario crear(@RequestBody Usuario usuario) {
    return service.guardar(usuario);
}
```
Si sobre el caso anterior en vez de la anotaci贸n prefiriese usar un **ResponseEntity**

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
(2) Troubleshoot en la URL de conexi贸n a MySQL

Andr茅s explica que suele haber problemas al no indicar dos par谩metros en la cadena de conexi贸n a MySQL en el fichero `application.properties`. <br>
Esto ocurr铆a en versiones previas del driver de MySQL, y mejor tenerlo en cuenta por si acaso surge.

1. `serverTimeZone`, se necesita indicar una zona horaria
2. `allowPublicKeyRetrieval`, es una opci贸n de cliente para que el connector de MySql permita al cliente solicitar autom谩ticamente la clave p煤blica al servidor. <br> 
Su valor por defecto es `false`, por eso hay que activarlo. <br> 
Idealmente habr铆a que a帽adir `useSSL=false` con prop贸sitos de desarrollo/testing.

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/msvc_usuarios?serverTimezone=Europe/Madrid&allowPublicKeyRetrieval=false
```
