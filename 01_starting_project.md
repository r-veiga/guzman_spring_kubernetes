# Empezando a preparar el proyecto

👉 [Volver a Readme.MD](Readme.MD)

## Crear el proyecto de microservicio "usuarios" 

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

## Crear el proyecto padre "curso-kubernetes"

Creo un proyecto Java 17 mediante Maven en IntelliJ, sin necesidad de indicar el arquetipo.

Como es el proyecto padre, es importante que el *package* sea *pom*.
```xml 
    <groupId>org.rveiga.springcloud.msvc</groupId>
    <artifactId>curso-kubernetes</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>pom</packaging>
```

## Relacionando "usuarios" con su padre "curso-kubernetes"

### (1) "usuarios" dentro del proyecto padre
Desempaqueta el **.zip** de "**msvc-usuarios**" obtenido de Spring Initializr dentro de la carpeta raíz del proyecto padre, "curso-kubernetes". 

Creará una carpeta "msvc-usuarios" dentro de la carpeta raíz del proyecto padre.

### (2) arreglar *pom.xml* para relación padre/hijo
Los **pom.xml** de los dos proyectos deberán modificarse para establecer la relación padre/hijo. 

#### (2.1) "curso-kubernetes" :: pom.xml :: Introducir bloque &lt;parent&gt;

Introduzco un bloque &lt;parent&gt; antes de las coordenadas Maven de "curso-kubernetes". 

Será el mismo bloque bloque &lt;parent&gt; que tiene el proyecto hijo "msvc-usuarios", el proyecto **spring-boot-starter-parent**. <br>
¿Por qué? Porque necesito el pom de Spring Boot que me creó Spring Initializr.

El proyecto **spring-boot-starter-parent** es un starter project especial que proporciona las configuraciones por defecto para nuestra aplicación 
y un árbol de dependencias completo para construir rápidamente un proyecto Spring Boot. <br>
Proporciona también la configuración por defecto para plugins Maven como maven-failsafe-plugin, maven-jar-plugin, maven-surefire-plugin y maven-war-plugin. <br> 
Además hereda la gestión de dependencias de *spring-boot-dependencies* que es el padre de *spring-boot-starter-parent*.

```xml 
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.6.13</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
```
#### (2.2) "msvc-usuarios" :: pom.xml :: Reemplazar bloque &lt;parent&gt;

Reemplazo el contenido del bloque &lt;parent&gt; por las coordenadas de "curso-kubernetes" como su padre. <br> 
De este modo creo la relación padre-hijo. Y como el padre tiene el **spring-boot-starter-parent**, entonces también se aplica en el hijo. 

```xml 
    <parent>
        <groupId>org.rveiga.springcloud.msvc</groupId>
        <artifactId>curso-kubernetes</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
```
#### (2.3) "curso-kubernetes" :: pom.xml :: Listar sus módulos

Debo indicar la lista de módulos que compone el proyecto padre. 

De momento sólo tengo un módulo, "msvc-usuarios". 

```xml 
    <modules>
        <module>msvc-usuarios</module>
    </modules>
```
## ¿Esto arranca ya? 

Puedo intentar arrancar el proyecto "msvc-usuarios" desde su clase `@SpringBootApplication` 
pero como no hemos configurado el MySql (recuerda que hemos metido su dependencia), el arranque fallará. 


[Siguiente](02_msvc-usuarios.md)
