package br.ifrn.caronas.controllers;

import br.ifrn.caronas.dtos.PerfilFormDTO;
import br.ifrn.caronas.models.Usuario;
import br.ifrn.caronas.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

	@GetMapping("/perfil")
	@PreAuthorize("isAuthenticated()")
	public String perfil(@AuthenticationPrincipal Usuario usuarioLogado, Model model) {
		model.addAttribute("perfilFormDTO", PerfilFormDTO.converter(usuarioLogado));
		return "usuarios/perfil";
	}

	@PostMapping("/perfil")
	@PreAuthorize("isAuthenticated()")
	public String atualizarPerfil(@Valid PerfilFormDTO perfilFormDTO,
								  BindingResult result,
								  @AuthenticationPrincipal Usuario usuarioLogado,
								  RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return "usuarios/perfil";
		}

		try {
			usuarioService.atualizarPerfil(usuarioLogado, perfilFormDTO);
			// Atualiza o objeto do usuário na sessão/contexto do Spring Security se necessário
			usuarioLogado.setNome(perfilFormDTO.getNome());
			attributes.addFlashAttribute("success", "Perfil atualizado com sucesso!");
		} catch (IllegalArgumentException e) {
			attributes.addFlashAttribute("danger", e.getMessage());
		}

		return "redirect:/perfil";
	}

	@GetMapping("/admin/usuarios")
	@PreAuthorize("hasRole('ADMIN')")
	public String listarUsuarios(Model model) {
		model.addAttribute("usuarios", usuarioService.listarTodos());
		return "admin/usuarios";
	}

	@PostMapping("/admin/usuarios/{id}/resetar-senha")
	@PreAuthorize("hasRole('ADMIN')")
	public String resetarSenha(@PathVariable Long id, RedirectAttributes attributes) {
		try {
			usuarioService.resetarSenha(id);
			attributes.addFlashAttribute("success", "Senha do usuário resetada com sucesso para '123456'.");
		} catch (Exception e) {
			attributes.addFlashAttribute("danger", "Erro ao resetar senha: " + e.getMessage());
		}
		return "redirect:/admin/usuarios";
	}


}
