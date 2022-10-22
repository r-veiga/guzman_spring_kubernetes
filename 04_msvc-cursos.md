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