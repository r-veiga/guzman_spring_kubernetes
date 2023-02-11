# Dockerizando los microservicios

He estado hasta ahora trabajando con las bases de datos usadas por los dos microservicios: 
- MySQL 
- PostgreSQL

¡DOCKERIZADAS! **antes de las explicaciones de Guzmán en en el curso.** <br/>
Lo he hecho por mi comodidad, para evitar instalarme nada en mi laptop. 

Lo que he hecho de motu propio ha sido crearme un `compose.yml` para levantar
como grupo de contenedores de Docker Compose:
- un contenedor MySQL 
- un contenedor PostgreSQL 
- un contenedor con PgAdmin para ver el contenido del PostgreSQL

### 👉 Pero ahora toca empezar con el dockerizado según el curso. 
Guzmán empieza dockerizando uno de los microservicios, **Usuarios**. <br/>

Compila el proyecto del microservicio Usuarios en un `.jar` ejecutable con `Java` 
y se crea con él una imagen Docker de la que se generarán contenedores.

## Modificaciones necesarias al código para que funcione
Tal como está el código del microservicio **Usuarios** no me va a funcionar. 

El código referencia a `localhost`, y Guzmán en el curso va a levantar un contenedor Docker independiente. <br/>
El contenedor independiente vive en su propia network, así que no puede ver los puertos que ofrece `localhost`. <br/>

> Da igual que sean
> - los contenedores Docker Compose con las BBDD en su propia network
> - la aplicación de microservicio de Cursos que he levantado en local con el IntelliJ
>
> Son puertos que ofrece el host, y esta aplicación dockerizada no los ve desde su contenedor individual. 

Donde se usa `localhost` hay que emplear la palabra clave 👉 `host.docker.internal` en su lugar.

- **application.properties** del proyecto de Usuarios. 
    ```properties
    spring.datasource.url=jdbc:mysql://host.docker.internal:3307/msvc_usuarios?serverTimezone=Europe/Madrid&allowPublicKeyRetrieval=false
    ```
- **CursoClienteRest.java**, el fichero con las llamadas del cliente Feign desde Usuarios a Cursos. 
    ```properties
    @FeignClient(name="msvc-cursos", url="host.docker.internal:8002")
    public interface CursoClienteRest { ... }
    ```
  
## Preparando una consola con JDK 17
Me va a ser necesaria para compilar el proyecto.
```powershell
echo off
cls
$env:JAVA_HOME="C:\Users\roberto.veiga\vero\vero.jdk.win-x64\jdk-17.0.2"
$env:PATH+=";$env:JAVA_HOME\bin"
$project_home="C:\Users\roberto.veiga\vero\vero.git\guzman_spring_kubernetes"
cd $project_home
echo on
```

## Crear un `JAR` ejecutable del microservicio Usuarios
Me sitúo en la carpeta raíz del microservicio Usuarios y con Maven creo el `jar`. 
```powershell
cd .\msvc-usuarios
.\mvnw clean package
```

## Construir un Dockerfile para el microservicio Usuarios
El Dockerfile que creará la imagen Docker debe contener el fichero `jar` anterior. 

La imagen usará como base **openjdk-17**. <br/>
El punto de entrada será la ejecución del `jar` con el comando `java`. <br/>
El puerto 8001 expuesto en el Dockerfile es meramente informativo, 
en realidad se determina el puerto expuesto al crear el contenedor.

```shell
FROM openjdk:17.0.2
WORKDIR /app
COPY ./target/msvc-usuarios-0.0.1-SNAPSHOT.jar .
EXPOSE 8001
ENTRYPOINT ["java", "-jar", "msvc-usuarios-0.0.1-SNAPSHOT.jar"]
```
Voy a nombrar a la imagen **micro-usuarios**.
```powershell
docker build -t micro-usuarios:latest .
```

## Ejecutar el contenedor del microservicio Usuarios
El contenedor se ejecutará por el puerto 15001, lo mapeo con la opción `-p 15001:8001`. <br/>
De este modo voy a jugar a tener levantados al mismo tiempo 
- microservicio de Usuarios en local por el Intellij, con la palabra clave `localhost` por el puerto 8001
- contenedor con el microservicio de usuarios, con la palabra clave `host.docker.internal` por el puerto 15001

```powershell
docker run --name container-usuarios -p 15001:8001  micro-usuarios:latest
```

He probado a tener los dos levantados en paralelo y han funcionado perfectamente. 

Además, si cambio en el microservicio Cursos (levantado en local) las referencias del puerto 8001 al 15001, 
funciona bien con `localhost`.




