FROM openjdk:17-jdk-alpine as primer
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

# ---
# --- ME ESTÁ FALLANDO EL MULTI-STAGE
# --- PARA CREAR EL CONTENEDOR TENGO QUE USAR EL CÓDIGO PREVIO AL USO DE MULTI-STAGE
# --- ESTO HAY QUE RESOLVERLO
# ---
# FROM openjdk:17-jdk-alpine
# WORKDIR /app
# COPY --from=primer /app/msvc-cursos/target/msvc-cursos-0.0.1-SNAPSHOT.jar .
# EXPOSE 8002
# ENTRYPOINT ["java", "-jar", "msvc-cursos-0.0.1-SNAPSHOT.jar"]

EXPOSE 8002
ENTRYPOINT ["java", "-jar", "./target/msvc-cursos-0.0.1-SNAPSHOT.jar"]