package br.ifrn.caronas.controllers;

import br.ifrn.caronas.models.Usuario;
import br.ifrn.caronas.services.UsuarioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rest/usuarios")
public class UsuarioRestController {

    UsuarioService usuarioService;

    public UsuarioRestController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<Usuario> buscarPorNome(@RequestParam(required = false, defaultValue = "") String q) {
        return usuarioService.findAllByNome(q);
    }

}
