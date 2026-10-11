package br.ifrn.caronas.controllers;

import br.ifrn.caronas.models.Usuario;
import br.ifrn.caronas.services.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsuarioController {

	private final UsuarioService usuarioService;

	public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	@GetMapping("/cadastro")
	@PreAuthorize("!isAuthenticated()")
	public String cadastro() {
		return "usuarios/form";
	}
	
	@PostMapping("/cadastro")
	@PreAuthorize("!isAuthenticated()")
	public String salvar(Usuario usuario, RedirectAttributes attributes) {
		usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
		usuarioService.adicionar(usuario);
		attributes.addFlashAttribute("success", "Usuário cadastrado com sucesso. Faça login para acessar a aplicação.");
		return "redirect:/login";
	}

}
