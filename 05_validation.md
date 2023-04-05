# Validaciones

👉 [Volver a Readme.MD](Readme.MD)

## Validar los datos del JSON

Si en este momento tratase de aplicar la anotación `@NotEmpty` de **javax.validation** en un campo de las entidades *Curso* o *Usuario* de los dos microservicios, el resultado sería diferente. 

Cuando originalmente creé los proyectos de microservicios con Spring Initializr: 
- `msvc-usuarios` -> **no** incluí la dependencia al artefacto "**Validation**" 
- `msvc-cursos` -> **sí** incluí la dependencia al artefacto "**Validation**" 

Por tanto, en `msvc-usuarios` me da error. <br>
Así que lo primero es añadir la dependencia en el **pom.xml** de `msvc-usuarios`.
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

## Asignar validaciones a campos de entidad 
Ya tengo añadida la dependencia a `spring-boot-starter-validation` en el fichero `pom.xml`.
Entonces ahora puedo **asignar anotaciones de validación** a campos de la entidad. 
```java
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Email;

    @NotEmpty 
    private String nombre;
    
    @NotEmpty 
    @Email 
    @Column(unique=true)
    private String email;
```

## Ejecución de las validaciones
¿Y en qué punto se ejecutan estas validaciones? <br/>

👉 **en los endpoints** donde emplee la anotación `@Valid` y el objeto `BindingResult` ❗❗

```java
import org.springframework.validation.BindingResult;
import javax.validation.Valid;

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Usuario usuario,BindingResult result) {
        
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            result.getFieldErrors().forEach( err -> {
                errores.put(
                        err.getField(), 
                        "El campo " + err.getField() + " " + err.getDefaultMessage()
                );
            });
            return ResponseEntity.badRequest().body(errores);
        }
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.guardar(usuario));
    }
```
Presta atención a cómo se están almacenando los errores de validación en el mapa que se devolverá como cuerpo de la respuesta.


## Personalización de los mensajes de error
Pero de este modo, indicando sólo `@NotEmpty`, se devuelve el mensaje de error por defecto, no está personalizado. <br>
Además, se mostrará en el idioma correspondiente a la zona horaria que tengo definida (según el browser). <br>
Podría personalizarlo implementando multilenguaje con `message.properties`. <br> 
Puedo probar los lenguajes enviando en Postman la cabecera "*Accept-Language*" con valores como en-US, en-UK, fr-CH, ru...

En este ejemplo voy simplemente a forzar el mensaje de error con el parámetro `message`. 
```java
  @NotEmpty(message = "Le nom ne peut pas être laissé en blanc.")
  private String nombre;
```

[Siguiente](06_relacionar_los_microservicios.md)
