package org.rveiga.springcloud.msvc.cursos.clients;

import org.rveiga.springcloud.msvc.cursos.models.Usuario;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

@FeignClient(name="msvc-usuarios", url="guzman-msvc-usuarios:8001")
public interface UsuarioClientRest {

    @GetMapping("/{id}") Usuario detalle(@PathVariable Long id);
    @PostMapping Usuario crear(@RequestBody Usuario usuario);
    @GetMapping("/usuarios") List<Usuario> usuarios(@RequestParam Iterable<Long> ids);
}
