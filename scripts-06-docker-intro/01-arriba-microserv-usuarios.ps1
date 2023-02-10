## 1 ## -- Configura JDK 17 para esta sesión
echo off
cls
$env:JAVA_HOME="C:\Users\roberto.veiga\vero\vero.jdk.win-x64\jdk-17.0.2"
$env:PATH+=";$env:JAVA_HOME\bin"
$project_home="C:\Users\roberto.veiga\vero\vero.git\guzman_spring_kubernetes"
cd $project_home
echo on

## 2 ## -- Muevo el directorio de trabajo a la carpeta del microservicio Usuarios
cd .\msvc-usuarios

## --- Recuerda antes de construir, que en el application.properties de la aplicación
## --- debo modificar `localhost`en la cadena de conexión a la BBDD, desde el contenedor
## --- no puedo ver la BBDD que existe en el host.
## --- Debe ser `host.docker.internal` en su lugar:
## ---
## --- jdbc:mysql://host.docker.internal:3307/msvc_usuarios?serverTimezone=Europe/Madrid&allowPublicKeyRetrieval=false
## ---
## --- También debe cambiarse `localhost`en las anotaciones de @FeignClient
## ---
## --- @FeignClient(name="msvc-cursos", url="host.docker.internal:8002")
## ---
## --- TL;DR Este contenedor vive en su propia network, así que para ver el host hay que
## --- usar la palabra clave `host.docker.internal`. Yo tengo la BBDD MySql en el host, o si no
## --- en un Docker Compose. De cualquier modo, el puerto de la BBDD es servido por el host
## --- para este contenedor, así que su cadena de conexión no funciona con `localhost`.


## 3 ## -- crea el .jar a partir del código del microservicio de Usuarios
.\mvnw clean package

## 4 ## -- Construye la imagen
docker build -t micro-usuarios:latest .

## 5 ## -- Levanta el contenedor escuchando por el puerto 15001
docker run --name container-usuarios -p 15001:8001  micro-usuarios:latest

