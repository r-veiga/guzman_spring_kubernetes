# Empezando a preparar el proyecto

👉 [Volver a Readme.MD](Readme.MD)

## Crea un proyecto padre "curso-kubernetes"

Se crea un proyecto Java 17 con Maven en IntelliJ, sin seleccionar ningún arquetipo.

Como es el proyecto padre, es importante que el *package* sea *pom*.
```xml 
    <groupId>org.rveiga.springcloud.msvc</groupId>
    <artifactId>curso-kubernetes</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>pom</packaging>
```

## Crea el primer proyecto hijo: Microservicio "usuarios" 

Creo un proyecto desde **Spring Initializr**. 

- Project: Maven Project
- Spring Boot 2.6.4
- Group: org.rveiga.springcloud.msvc.usuario
- Artifact: msvc-usuarios
- Packaging: JAR
- Java 17
- Dependency: Spring Boot Dev Tools
- Dependency: Spring Web
- Dependency: Spring Data JPA
- Dependency: My SQL Driver
- Dependency: OpenFeign

## Relaciona "usuarios" con su padre "curso-kubernetes"

### (1) "usuarios" dentro del proyecto padre
En la carpeta raíz del proyecto padre, "curso-kubernetes" se desempaqueta el **.zip** `msvc-usuarios` (Spring Initializr). <br/>
Creará una subcarpeta "**msvc-usuarios**" en el padre.

### (2) ajustar los *pom.xml* para relación padre/hijo
Los **pom.xml** de los dos proyectos deben modificarse para establecer la relación padre/hijo. 

#### (2.1) pom.xml padre :: "curso-kubernetes" :: Introducir bloque &lt;parent&gt;

Introduzco un bloque `&lt;parent&gt;` antes de las coordenadas Maven de "curso-kubernetes". <br/> 
Es copiado del bloque `&lt;parent&gt;` creado por Spring Initializr para "msvc-usuarios". 
Lo que contiene es el proyecto **spring-boot-starter-parent**. <br>

**spring-boot-starter-parent** es un starter project especial para construir rápidamente un proyecto Spring Boot. <br/> 
Proporciona:  
- configuraciones por defecto para nuestra aplicación 
- árbol de dependencias completo para un proyecto Spring Boot
- configuración por defecto para plugins Maven como maven-failsafe-plugin, maven-jar-plugin, maven-surefire-plugin y maven-war-plugin
- hereda la gestión de dependencias de *spring-boot-dependencies* que es el padre de *spring-boot-starter-parent*

```xml 
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.6.13</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
```
#### (2.2) pom.xml hijo ::"msvc-usuarios" :: Reemplazar bloque &lt;parent&gt;

Reemplaza el contenido del bloque `&lt;parent&gt;` por las coordenadas de "curso-kubernetes"` para que sea su padre. <br/> 
Así se crea la relación padre-hijo. <br/> 
Y como el padre tiene el **spring-boot-starter-parent**, entonces su contenido también se aplica en el hijo. 

```xml 
    <parent>
        <groupId>org.rveiga.springcloud.msvc</groupId>
        <artifactId>curso-kubernetes</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
```
#### (2.3) "curso-kubernetes" :: pom.xml :: Lista de sus módulos hijo

Hay que crear la lista de módulos que compone el proyecto padre. 

De momento sólo existe un módulo, "msvc-usuarios". 

```xml 
    <modules>
        <module>msvc-usuarios</module>
    </modules>
```
## ¿Esto arranca ya? 

Puedo intentar arrancar el proyecto "msvc-usuarios" desde su clase `@SpringBootApplication` 
pero como no hemos configurado el MySql (se introdujo su dependencia en Spring Initializr), el arranque fallará. 


[Siguiente](02_msvc-usuarios.md)
