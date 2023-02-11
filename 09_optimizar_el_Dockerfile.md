# Optimizar el Dockerfile 

👉 [Volver a Readme.MD](Readme.MD)
<hr/>

## Generar el compilado `.jar` dentro de la imagen

Puedo simplificar el `Dockerfile` que creé previamente donde se copiaba el `jar`ya generado a la imagen. 

Apoyándome en **Maven** puedo ejecutar directamente la creación dentro de la imagen. <br/>
El problema entonces es que el microservicio es un proyecto hijo, 
y necesito disponer del `pom.xml` del proyecto padre debido a las dependencias.

En vez de este `pom.xml` original, 
```shell
FROM openjdk:17.0.2
WORKDIR /app
COPY ./target/msvc-usuarios-0.0.1-SNAPSHOT.jar .
EXPOSE 8001
ENTRYPOINT ["java", "-jar", "msvc-usuarios-0.0.1-SNAPSHOT.jar"]
```
La carpeta `target` con el compilado, ahora se generará dentro de la imagen. <br/>
Así que como paso previo, se eliminará del host si es que todavía existe.
```powershell
cd <path/microservicio_usuarios>
.\mvnw clean
cd ..
```

**Atención** ❗❗ <br/>
👉 Estoy en la carpeta del proyecto padre, no en la del microservicio. <br/>
El comando de creación de la imagen será,
```powershell
docker build -t micro-usuarios:latest . -f .\msvc-usuarios\Dockerfile
```
Aplicado sobre este otro `pom.xml`, 
```shell
FROM openjdk:17.0.2
WORKDIR /app/msvc-usuarios .

# ¡Atención! :: estoy en la carpeta de contexto del Dockerfile (raíz del proyecto padre)
# Se copia, 
# (1) POM del padre                     --> a la carpeta de trabajo /app
# (2) código del proyecto microservicio --> a la carpeta de trabajo /app/msvc-usuarios
COPY ./pom.xml /app
COPY ./msvc-usuarios .

# genera el JAR, como siempre en una carpeta `target`
RUN sed -i 's/\r$//' mvnw
RUN ./mvnw clean package -DskipTests

EXPOSE 8001
ENTRYPOINT ["java", "-jar", "./target/msvc-usuarios-0.0.1-SNAPSHOT.jar"]
```

Inconveniente: 
- se demora bastante porque se tiene que bajar todas las dependencias
- un cambio de código cambiará la capa `COPY ./msvc-usuarios .` así que todo lo subsiguiente se recreará

## Optimizar velocidad. Guarda dependencias descargadas en caché
Busco que con la modificación de código en el proyecto no haya que realizar una descarga de nuevo. 

Se descargarán primero las dependencias, en una capa previa. <br/>
Esta capa se reconstruirá sólo si cambian los `pom.xml`.

El nuevo `Dockerfile` quedará de esta manera,
```shell
FROM openjdk:17.0.2
WORKDIR /app/msvc-usuarios .

COPY ./pom.xml /app
COPY ./msvc-usuarios/.mvn ./.mvn
COPY ./msvc-usuarios/mvnw .
COPY ./msvc-usuarios/pom.xml .

# Limpia el trailing de mvnw, que me ha dado problemas porque viene de Windows ¿?
RUN sed -i 's/\r$//' mvnw

# Con Maven crea un paquete vacío bajando todas las dependencias y después elimina la carpeta `target`
RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target

# genera el JAR a partir del código fuente
COPY ./msvc-usuarios/src ./src
RUN ./mvnw clean package -DskipTests

EXPOSE 8001
ENTRYPOINT ["java", "-jar", "./target/msvc-usuarios-0.0.1-SNAPSHOT.jar"]
```

Se han apllicado unas opciones al comando `mvnw`:
- `-Dmaven.test.skip` no compila ni ejecuta el código de los tests
- `-Dmaven.main.skip` no compila el código principal 
- `-Dspring-boot.repackage.skip` empaqueta el proyecto, pero sin el código fuente (o sea que baja las dependencias y ya)

Una alternativa a `RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip` 
es el comando `RUN ./mvnw dependency:go-offline`. <br/>
Es muy similar, la diferencia es mínima, pero Guzmán recomienda mejor la primera.

Como curiosidad, la diferencia entre `-Dmaven.test.skip` y  `-DskipTests` es que el primero ni compila ni ejecuta los tests. 
El segundo se limita a no ejecutarlos. 

## Optimizar tamaño: `Multi-Stage builds`
El primer cambio para optimizar tamaño será usar una imagen de JDK-Alpine en vez de OpenJDK. <br/>
JDK Alpine es más ligera. 

Un `Dockerfile` multi-stage crea una imagen por cada cláusula `FROM` en el `Dockerfile`.

En este caso, el primer paso es casi idéntico al Dockerfile anterior. <br/>
Pero las cosas cambian cuando ya tengo construido el `.jar`. <br/>
Todas las dependencias y el código y demás, ¡ya no me sirven! <br/>
Me es suficiente simple y llanamente con el `.jar` y construir un punto de entrada que lo ejecute.

Así que una vez construido el `.jar` se genera otra imagen con base en `openjdk:17-jdk-alpine` <br/>
y lo interesante viene en el `COPY --from=primer-paso` donde indico qué quiero copiar de alguno de los 
pasos previos de este multi-stage.

```shell
FROM openjdk:17-jdk-alpine as primer-paso
WORKDIR /app/msvc-usuarios .

COPY ./pom.xml /app
COPY ./msvc-usuarios/.mvn ./.mvn
COPY ./msvc-usuarios/mvnw .
COPY ./msvc-usuarios/pom.xml .

# Limpia el trailing de mvnw, que me ha dado problemas porque viene de Windows ¿?
RUN sed -i 's/\r$//' mvnw

# Con Maven crea un paquete vacío bajando todas las dependencias y después elimina la carpeta `target`
RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target

COPY ./msvc-usuarios/src ./src
RUN ./mvnw clean package -DskipTests

FROM openjdk:17-jdk-alpine 
WORKDIR /app
COPY --from=primer-paso /app/msvc-usuarios/target/msvc-usuarios-0.0.1-SNAPSHOT.jar .
EXPOSE 8001
ENTRYPOINT ["java", "-jar", "msvc-usuarios-0.0.1-SNAPSHOT.jar"]
```

<hr/>

👉 [Volver a Readme.MD](Readme.MD)
