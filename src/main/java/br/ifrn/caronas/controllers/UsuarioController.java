package br.ifrn.caronas.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	public String salvar(Usuario usuario, RedirectAttributes attributes) {
		Optional<Usuario> optional = ur.findByTelefone(usuario.getTelefone());
		
		if(optional.isPresent()) {
			attributes.addFlashAttribute("msg", "Número de telefone já cadastrado");
			return "redirect:/cadastro";
		}
		
		usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
		System.out.println(usuario);
		
		ur.save(usuario);
		attributes.addFlashAttribute("msg", "Usuário cadastrado com sucesso. Faça login para acessar a aplicação.");
		return "redirect:/login";
	}

}
