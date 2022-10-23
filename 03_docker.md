# Notas sobre Docker

👉 [Volver a Readme.MD](Readme.MD)

Lo importante aquí es que introduzco el concepto **--network** a la hora de crear los contenedores.
De este modo serán visibles entre ellos. 

## Dockerizo  MySql

Salto al **vídeo 72 del curso** para ver cómo crear un contenedor de MySql para que no me lo tenga que descargar.

Accedo al MySql de mi contenedor por el puerto 3307, es el puerto externo que ofrezco.
Asimismo determino dos variables de entorno.
1. genera una base de datos *msvc_usuarios* (vacía)
2. la contraseña del usuario *root* será "sasa"
```bash
docker pull mysql:8
docker run -d -p 3307:3306 --name mi-mysql8 --network spring 
       -e MYSQL_ROOT_PASSWORD=sasa
       -e MYSQL_DATABASE=msvc_usuarios
       mysql:8
docker ps
docker logs mi-mysql8
```
## Dockerizo  PostgreSQL

Salto al **vídeo 73 del curso** para ver cómo crear un contenedor de PostgreSQL para que no me lo tenga que descargar.

Accedo al PostgreSQL de mi contenedor por el puerto 5532, es el puerto externo que ofrezco.
Asimismo determino dos variables de entorno.
1. genera una base de datos *msvc_cursos* (vacía)
2. la contraseña del usuario *postgres* será "sasa"
```bash
docker pull postgres:14-alpine
docker run -d -p 5532:5432 --name mi-postgres14 --network spring 
       -e POSTGRES_PASSWORD=sasa
       -e POSTGRES_DB=msvc_cursos
       postgres:14-alpine
docker ps
docker logs mi-postgres14
```