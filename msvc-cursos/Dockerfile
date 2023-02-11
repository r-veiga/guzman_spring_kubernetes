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