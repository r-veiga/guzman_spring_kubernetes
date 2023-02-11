# Dockerizar el microservicio "Cursos" y crear network

👉 [Volver a Readme.MD](Readme.MD)

<hr/>

En este apartado se desarrollará cómo 
- dockerizar el segundo microservicio, **Cursos** 
- configurar una network de contenedores

<hr/>

## Reemplazar `localhost` por `host.docker.internal` 

Todavía no existe una red de contenedores. 

El contenedor del microservicio buscará los puertos de conexión como si los expusiera `localhost`. <br/>
Pero está recluído en su propia red, con lo que se debe usar `host.docker.internal`.

Como ya se ha visto previamente, 
- en la cadena de conexión a la BBDD definida en el `application.properties` 
    ```properties
    spring.datasource.url=jdbc:postgresql://host.docker.internal:5532/msvc_cursos
    ```
- en las anotaciones de los clientes Feign, `@FeignClient(..., url="xxx")`
    ```java
    @FeignClient(name="msvc-usuarios", url="host.docker.internal:8001")
    public interface UsuarioClientRest { ... }
    ```
## Convertir el `Dockerfile` en multi-stage
Se modifica el `Dockerfile` para que quede de la siguiente manera, 

```shell
FROM openjdk:17-jdk-alpine as primer-paso
WORKDIR /app/msvc-cursos .

COPY ./pom.xml /app
COPY ./msvc-cursos/.mvn ./.mvn
COPY ./msvc-cursos/mvnw .
COPY ./msvc-cursos/pom.xml .

# Limpia el trailing de mvnw, que me ha dado problemas porque viene de Windows ¿?
RUN sed -i 's/\r$//' mvnw

# Con Maven crea un paquete vacío bajando todas las dependencias y después elimina la carpeta `target`
RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target

COPY ./msvc-cursos/src ./src
RUN ./mvnw clean package -DskipTests

FROM openjdk:17-jdk-alpine 
WORKDIR /app
COPY --from=primer-paso /app/msvc-cursos/target/msvc-cursos-0.0.1-SNAPSHOT.jar .
EXPOSE 8002
ENTRYPOINT ["java", "-jar", "msvc-cursos-0.0.1-SNAPSHOT.jar"]
```
Los comandos apropiados para este Dockerfile son
```powershell
cd <path/microservicio_usuarios>
.\mvnw clean
docker build -t micro-cursos:latest . -f .\msvc-cursos\Dockerfile
docker run --name container-cursos -p 15002:8002  micro-cursos:latest
```

## Creación de network
- creación de una red *ad-hoc* que por defecto es de tipo "bridge"
- incluir los contenedores dentro de esta nueva red
```powershell
docker network create mi-spring
docker network ls 
docker stop container-usuarios container-cursos 
docker rm   container-usuarios container-cursos 
docker run --rm --network mi-spring --name container-usuarios -p 15001:8001  micro-usuarios:latest
docker run --rm --network mi-spring --name container-cursos   -p 15002:8002  micro-cursos:latest
```
Guzmán no lo hace en esta fase del curso, pero yo imagino que se puede cambiar `host.docker.internal` 
por el nombre del contenedor del microservicio en las anotaciones `@FeignClient`.

<hr/>

👉 [Volver a Readme.MD](Readme.MD)
