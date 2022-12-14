# Creando el microservicio de CURSOS

馃憠 [Volver a Readme.MD](Readme.MD)

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
De este modo creo la relaci贸n padre-hijo. Y como el padre tiene el **spring-boot-starter-parent**, entonces tambi茅n se aplica en el hijo.

```xml 
    <parent>
        <groupId>org.rveiga.springcloud.msvc</groupId>
        <artifactId>curso-kubernetes</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
```
####(2) "curso-kubernetes" :: pom.xml :: A帽adir m贸dulo "msvc-cursos"

Debo indicar la lista de m贸dulos que compone el proyecto padre.

Son dos m贸dulos, uno por cada proyecto de microservicios: "msvc-usuarios" y "msvc-cursos".

```xml 
    <modules>
        <module>msvc-usuarios</module>
        <module>msvc-cursos</module>
    </modules>
```

## Escribiendo el c贸digo de la aplicaci贸n 

No voy a extenderme, es similar a lo visto en el microservicio previo para usuarios. 
Mejor mira directamente el c贸digo. 

## Configurar el datasource y la conexi贸n a PostgreSQL 

El fichero **application.properties** del microservicio de cursos ser谩: 

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

Es una propiedad opcional, pero el instructor la pone porque podr铆a tener problemas con algunos tipos de campos. 

## Dockerizo  PostgreSQL

El curso en este momento pide la instalaci贸n local de PostgreSQL. <br>
Pero no me lo quiero descargar e instalar en local. <br>
Voy a hacer spoiler y saltar hasta el **v铆deo 73 del curso** para ver c贸mo crear un contenedor Docker con PostgreSQL.

Accedo al PostgreSQL de mi contenedor por el puerto 5532, es el puerto externo que ofrezco.
Asimismo determino dos variables de entorno.
1. genera una base de datos *msvc_cursos* (vac铆a)
2. la contrase帽a del usuario *postgres* ser谩 "sasa"
```bash
docker pull postgres:14-alpine
docker run -d -p 5532:5432 --name guzman-postgres14 
       -e POSTGRES_PASSWORD=sasa
       -e POSTGRES_DB=msvc_cursos
       postgres:14-alpine
docker ps
docker logs guzman-postgres14
```

As铆 tengo un PostgreSQL disponible por el puerto 5532. <br>
馃憠  Rese帽o que aqu铆 no he usado la opci贸n **--network** que en un momento posterior comunicar谩 los distintos contenedores que levantar茅.
