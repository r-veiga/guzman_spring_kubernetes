# Relacionando los microservicios CURSOS y USUARIOS

Hasta este momento sólo he realizado operaciones CRUD que afectaban o a la entidad `Curso` o a `Usuario`. <br/>
Por tanto sólo interactuaba con uno de los microservicios (y su BBDD asociada). 

Ahora realizaré acciones que requieran los dos microservicios.

## Acciones a implementar
Algunas acciones en la aplicación que requieren una conexión entre los microservicios. 

| CURSOS | USUARIOS |
|---|---|
| Asignar usuario | Eliminar usuario & desasignar |
| Crear usuario |  |
| Desasignar usuario |  |
| Obtener usuarios de un curso |  |

## Crear entidad intermedia `Curso-Usuario` 
Quiero relacionar los IDs de Curso y Usuario. 

En el proyecto `msvc-cursos` creo la entidad intermedia `CursoUsuario`. <br/> 
Es decir, los usuarios apuntados a un curso. 

```java
import javax.persistence.*;

@Entity
@Table(name = "cursos_usuarios")
public class CursoUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", unique = true)
    private Long usuarioId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
```

Pero si en la nueva entidad tengo sólo el ID de usuario... ¿dónde está el ID de curso?

Creo un campo `@OneToMany List<CursoUsuario>` en la entidad `Curso`. <br/>
Servirá de **foreign key** al marcarlo con `@JoinColumn(name = "curso_id")`.

```java
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.LAZY)
    @JoinColumn(name = "curso_id")
    private List<CursoUsuario> cursoUsuarios;
	
	@Transient
	private List<Usuario> usuarios;

    public Curso() {
        // Inicializo "cursoUsuarios" en el constructor
        cursoUsuarios = new ArrayList<>();
    }
    
    // crear getter + setter --- de "cursoUsuarios" y "usuarios"

    public void addCursoUsuario(CursoUsuario cursoUsuario) {
        this.cursoUsuarios.add(cursoUsuario);
    }
    
    public void removeCursoUsuario(CursoUsuario cursoUsuario) {
        this.cursoUsuarios.remove(cursoUsuario);
    }
```

Emplear `fetch=FetchType.LAZY` resultaría innecesario porque es el comportamiento por defecto. 

Tengo un campo `@Transient private List<Usuario> usuarios;` fuera del contexto de Hibernate 
(de la persistencia) que usaré para poblar los datos completos de los usuarios. <br/>
A continuación explico la creación del tipo `Usuario` usado para esta lista.


## Crear bean `Usuario` en el proyecto de Cursos
Crearé un bean de `Usuario` con las mismas propiedades que tiene la entidad `Usuario` en el proyecto de Usuarios. <br/> 
Pero sin anotaciones, ni a nivel de clase ni de propiedad. Un Java Bean mondo y lirondo. 

Lo usaré para hacer unmarshal al JSON que reciba. 

```java
package org.rveiga.springcloud.msvc.cursos.models;

public class Usuario {
    private Long id;
    private String nombre;
    private String email;
    private String password;
	
	// --------- getters + setters
}
```








