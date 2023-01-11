package org.rveiga.springcloud.msvc.usuario.msvcusuarios.repositories;

import org.rveiga.springcloud.msvc.usuario.msvcusuarios.models.entity.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}