# Dockerizar las BBDD

👉 [Volver a Readme.MD](Readme.MD)

<hr/>

En este apartado se dockerizarán las BBDD, 
- dockerizar MysQL
- dockerizar PostgreSQL

<hr/>

## Contenedor Docker de MySQL
Se usará la versión 8 de MySQL. 

Guzmán propone estos comandos,
```powershell
docker pull mysql:8
docker run -d 
           --name mysql8 
           --network mi-spring 
           -p 3307:3306 
           -v data-mysql:/var/lib/mysql
           -e MYSQL_ROOT_PASSWORD=sasa
           -e MYSQL_DATABASE=msvc_usuarios
           --restart=always
           mysql:8
```

## Contenedor Docker de PostgreSQL
Se usará la versión 14 Alpine de PostgreSQL.

Guzmán propone estos comandos,
```powershell
docker pull postgres:14-alpine
docker run -d 
           --name postgres14 
           --network mi-spring 
           -p 5532:5432 
           -v data-postgres:/var/lib/postgresql/data
           -e POSTGRESS_PASSWORD=sasa
           -e POSTGRESS_DB=msvc_usuarios
           --restart=always
           postgres:14-alpine
```

## 👉 Todos los contenedores están en la network `mi-spring`
Guzmán levanta todos los contenedores con la opción `--network mi-spring`, por tanto están en la misma red. 

- contenedor microservicio usuarios `--name container-usuarios`
- contenedor microservicio cursos `--name container-cursos`
- contenedor bbdd mysql (usuarios) `--name mysql8`
- contenedor bbdd postgresql (cursos) `--name postgres14` 

Así que en lugar de `host.docker.internal` o de `localhost`, usaré el nombre del contenedor pertinente,
- en cadenas de conexión a BBDD en `application.properties`
- en anotaciones `@FeignClient`

## Mis personalizaciones
Desde el principio tengo dockerizadas las BBDD, las he estado levantando con "Docker Compose". <br/>
Tengo algunas diferencias menores, como p.ej. 
- nombre de los contenedores
- no empleo la network, así que contenedores de aplicación (no compose) y de BBDD (compose) no están en la misma red
- las variables de entorno del contenedor PostgreSQL
- mi Docker compose además de las dos BBDD también tiene un contenedor para PgAdmin
- ...

Modificaré mi `compose.yml` para dejarlo así: 
```yaml
version: '3'
services:

  guzman-mysql8:
    image: mysql:8
    container_name: guzman-mysql8
    ports:
      - 3307:3306
    environment:
      - MYSQL_ROOT_PASSWORD=sasa
      - MYSQL_DATABASE=msvc_usuarios
    volumes:
      - /data-bbdd/mysql/:/var/lib/mysql

  guzman-postgres14:
    image: postgres:14-alpine
    container_name: guzman-postgres14
    ports:
      - 5532:5432
    environment:
      - POSTGRES_USER=postgres
      - POSTGRES_DB=msvc_cursos
      - POSTGRES_PASSWORD=sasa
    volumes:
      - /data-bbdd/postgresql/data:/var/lib/postgresql/data

  guzman-pgadmin:
    image: dpage/pgadmin4
    container_name: guzman-pgadmin
    ports:
      - 5050:80
    environment:
      - PGADMIN_DEFAULT_EMAIL=zinedine@rmad.com
      - PGADMIN_DEFAULT_PASSWORD=zinedine

  guzman-msvc-cursos:
    image: micro-cursos:latest
    container_name: guzman-msvc-cursos
    ports:
      - 15002:8002
    depends_on:
      - guzman-postgres14

  guzman-msvc-usuarios:
    image: micro-usuarios:latest
    container_name: guzman-msvc-usuarios
    ports:
      - 15001:8001
    depends_on:
      - guzman-mysql8
```



<hr/>

👉 [Volver a Readme.MD](Readme.MD)
