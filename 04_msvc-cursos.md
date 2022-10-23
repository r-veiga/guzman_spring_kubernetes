# Creando el microservicio de CURSOS

👉 [Volver a Readme.MD](Readme.MD)

## Crear el proyecto de microservicio "cursos"

Creo un proyecto desde **Spring Initializr**.

- Project: Maven Project
- Spring Boot 2.6.13
- Group: org.rveiga.springcloud.msvc.cursos
- Artifact: msvc-cursos
- Packaging: JAR
- Java 17
- Dependency: Spring Boot Dev Tools
- Dependency: Spring Web
- Dependency: Spring Data JPA
- Dependency: PostgreSQL Driver
- Dependency: OpenFeign
- Dependency: Validator

## Relacionando "msvc-cursos" con su padre "curso-kubernetes"

####(1) "msvc-cursos" :: pom.xml :: Reemplazar bloque &lt;parent&gt;

Reemplazo el contenido del bloque &lt;parent&gt; por las coordenadas de "curso-kubernetes" como su padre. <br>
De este modo creo la relación padre-hijo. Y como el padre tiene el **spring-boot-starter-parent**, entonces también se aplica en el hijo.

```xml 
    <parent>
        <groupId>org.rveiga.springcloud.msvc</groupId>
        <artifactId>curso-kubernetes</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
```
####(2) "curso-kubernetes" :: pom.xml :: Añadir módulo "msvc-cursos"

Debo indicar la lista de módulos que compone el proyecto padre.

Son dos módulos, uno por cada proyecto de microservicios: "msvc-usuarios" y "msvc-cursos".

```xml 
    <modules>
        <module>msvc-usuarios</module>
        <module>msvc-cursos</module>
    </modules>
```

## Escribiendo el código de la aplicación 

No voy a extenderme, es similar a lo visto en el microservicio previo para usuarios. 
Mejor mira directamente el código. 

## Configurar el datasource y la conexión a PostgreSQL 

El fichero **application.properties** del microservicio de cursos será: 

```properties
spring.application.name=msvc-cursos
server.port=8002

spring.datasource.url=jdbc:postgresql://localhost:5532/msvc_cursos
spring.datasource.username=postgres
spring.datasource.password=sasa
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jap.database-platform=org.hibernate.dialect.PostgreSQL10Dialect
spring.jpa.generate-ddl=true
loggin.level.org.hibernate.SQL=debug
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
```

`spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true`

Es una propiedad opcional, pero el instructor la pone porque podría tener problemas con algunos tipos de campos. 

## Dockerizo  PostgreSQL

El curso en este momento pide la instalación local de PostgreSQL. <br>
Pero no me lo quiero descargar e instalar en local. <br>
Voy a hacer spoiler y saltar hasta el **vídeo 73 del curso** para ver cómo crear un contenedor Docker con PostgreSQL.

Accedo al PostgreSQL de mi contenedor por el puerto 5532, es el puerto externo que ofrezco.
Asimismo determino dos variables de entorno.
1. genera una base de datos *msvc_cursos* (vacía)
2. la contraseña del usuario *postgres* será "sasa"
```bash
docker pull postgres:14-alpine
docker run -d -p 5532:5432 --name mi-postgres14 
       -e POSTGRES_PASSWORD=sasa
       -e POSTGRES_DB=msvc_cursos
       postgres:14-alpine
docker ps
docker logs mi-postgres14
```

Así tengo un PostgreSQL disponible por el puerto 5532. <br>
👉  Reseño que aquí no he usado la opción **--network** que en un momento posterior comunicará los distintos contenedores que levantaré.
