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
docker run -d -p 3307:3306 --name guzman-mysql8 --network spring 
       -e MYSQL_ROOT_PASSWORD=sasa
       -e MYSQL_DATABASE=msvc_usuarios
       mysql:8
docker ps
docker logs guzman-mysql8
```
## Dockerizo  PostgreSQL

Salto al **vídeo 73 del curso** para ver cómo crear un contenedor de PostgreSQL para que no me lo tenga que descargar.

Accedo al PostgreSQL de mi contenedor por el puerto 5532, es el puerto externo que ofrezco.
Asimismo determino dos variables de entorno.
1. genera una base de datos *msvc_cursos* (vacía)
2. la contraseña del usuario *postgres* será "sasa"
```bash
docker pull postgres:14-alpine
docker run -d -p 5532:5432 --name guzman-postgres14 --network spring 
       -e POSTGRES_PASSWORD=sasa
       -e POSTGRES_DB=msvc_cursos
       postgres:14-alpine
docker ps
docker logs guzman-postgres14
```

## Uso de pgAdmin con Docker 

La herramienta de gestión de PostgreSQL es pgAdmin. <br/>
Me será muy útil para ver qué tablas se crean y qué datos se almacenan. 

En vez de tener una instalación local en mi host, voy a arrancar un contenedor Docker. <br/>
Aquí daré las instrucciones para configurarlo y usarlo. 
Para más detalle, consulta en [DockerHub](https://hub.docker.com/r/dpage/pgadmin4/).

Ejecutaré un contenedor simple de **pgAdmin**, accesible por el puerto 5050. 
Le indico las dos variables de entorno obligatorias, que necesitaré para hacer login en la web.

```powershell
docker run -d
    --name guzman-pgadmin
    -p 5050:80 
    -e "PGADMIN_DEFAULT_EMAIL=zinedine@rmad.com" 
    -e "PGADMIN_DEFAULT_PASSWORD=zinedine" 
    dpage/pgadmin4
```
```
http://localhost:5050
```
En pgAdmin se configurará una  nueva conexión a PostgreSQL con los valores 
usados en el `application.properties` para que el microservicio de Cursos se conecte a PostgreSQL.

| Name                 | guzman-postgres14 |
|----------------------|-------------------|
| Host name/address    | 192.168.1.150     | 
| Port                 | 5532              |
| Maintenance database | msvc_cursos       | 
| Username             | postgres          | 

El valor de IP de **Host name/address** se obtiene inspeccionando el contenedor de la BBDD PostgreSQL.
```powershell
docker inspect guzman-postgres14
```