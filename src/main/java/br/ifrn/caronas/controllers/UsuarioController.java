package br.ifrn.caronas.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import br.ifrn.caronas.models.Usuario;
import br.ifrn.caronas.repositories.UsuarioRepository;

@Controller
public class UsuarioController {
	
	@Autowired
	private UsuarioRepository ur;
	

	@GetMapping("/cadastro")
	@PreAuthorize("!isAuthenticated()")
	public String cadastro() {
		return "usuarios/form";
	}
	
	@PostMapping("/cadastro")
	@PreAuthorize("!isAuthenticated()")
	public String salvar(Usuario usuario) {
		
		usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
		System.out.println(usuario);
		
		ur.save(usuario);
		
		return "redirect:/";
	}

}
