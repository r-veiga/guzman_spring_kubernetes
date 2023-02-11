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