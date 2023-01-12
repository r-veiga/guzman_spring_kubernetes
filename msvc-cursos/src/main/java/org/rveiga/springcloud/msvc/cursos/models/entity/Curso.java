package org.rveiga.springcloud.msvc.cursos.models.entity;

import org.rveiga.springcloud.msvc.cursos.models.Usuario;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="cursos")
public class Curso {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotEmpty
	private String nombre;

	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.LAZY)
	private List<CursoUsuario> cursoUsuarios;

	@Transient
	private List<Usuario> usuarios;

	public Curso() {
		cursoUsuarios = new ArrayList<>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<CursoUsuario> getCursoUsuarios() {
		return cursoUsuarios;
	}

	public void setCursoUsuarios(List<CursoUsuario> cursoUsuarios) {
		this.cursoUsuarios = cursoUsuarios;
	}

	public void addCursoUsuario(CursoUsuario cursoUsuario) {
		this.cursoUsuarios.add(cursoUsuario);
	}

	public void removeCursoUsuario(CursoUsuario cursoUsuario) {
		this.cursoUsuarios.remove(cursoUsuario);
	}
}
