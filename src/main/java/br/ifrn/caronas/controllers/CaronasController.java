package br.ifrn.caronas.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.ifrn.caronas.dtos.CaronaItemListaDTO;
import br.ifrn.caronas.dtos.CaronaRequestDTO;
import br.ifrn.caronas.models.Carona;
import br.ifrn.caronas.models.Usuario;
import br.ifrn.caronas.repositories.CaronaRepository;
import br.ifrn.caronas.repositories.UsuarioRepository;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/caronas")
public class CaronasController {

	@Autowired
	private UsuarioRepository ur;
	@Autowired
	private CaronaRepository cr;

	@GetMapping("/novo")
	public String form(CaronaRequestDTO caronaRequestDTO) {

		if(caronaRequestDTO.getId() == null || caronaRequestDTO.getId() == 0) {
			Optional<Carona> optional = cr.findFirstByMotoristaOrderByIdDesc(getPrincipal());
			if(optional.isPresent()) {
				Carona carona = optional.get();
				caronaRequestDTO.setObservacoes(carona.getObservacoes());
				caronaRequestDTO.setVagas(carona.getVagas());
				caronaRequestDTO.setValor(carona.getValor());
			}
		}
		
		return "caronas/form";
	}

	/**
	 * Salva uma nova carona
	 * @param caronaRequestDTO
	 * @param result
	 * @param attributes
	 * @return
	 */
	@PostMapping
	public String salvar(@Valid CaronaRequestDTO caronaRequestDTO, BindingResult result,
			RedirectAttributes attributes) {

		if (result.hasErrors()) {
			return form(caronaRequestDTO);
		}
		System.out.println(caronaRequestDTO);

		Carona carona = caronaRequestDTO.extrair();
		carona.setMotorista(getPrincipal());

		System.out.println(carona);

		cr.save(carona);

		attributes.addFlashAttribute("msg", "Carona cadastrada com sucesso");

		return "redirect:/caronas/novo";
	}
	
	/**
	 * Reserva a carona para o usuário logado
	 * @param id
	 * @param attributes
	 * @return
	 */
	@PostMapping("/{id}/reservar")
	public ModelAndView reservar(@PathVariable Long id, RedirectAttributes attributes) {
		Optional<Carona> optional = cr.findById(id);
		ModelAndView md = new ModelAndView();
		md.setViewName("redirect:/caronas");
		
		/** Verifica se a carona existe **/
		if (optional.isEmpty()) {
			attributes.addFlashAttribute("msg", "Carona não encontrada");
			return md;
		}
		
		Carona carona = optional.get();
		Usuario usuario = getPrincipal();
		
		/** Verifica se o usuário já está na carona como motorista ou passageiro **/
		if (carona.getMotorista().getId() == usuario.getId()) {
			attributes.addFlashAttribute("msg", "Usuario já está na carona");
			return md;
		} else {
			for (Usuario p : carona.getPassageiros()) {
				if (p.getId() == usuario.getId()) {
					attributes.addFlashAttribute("msg", "Usuario já está na carona");
					return md;
				}
			}
		}
		
		/** Verifica se a carona está lotada **/
		if(carona.getVagas() <= carona.getPassageiros().size()) {
			attributes.addFlashAttribute("msg", "Carona lotada, não é possível fazer reserva");
			return md;
		}
		
		/** Adiciona o usuário na carona **/
		carona.getPassageiros().add(usuario);
		cr.save(carona);
		
		attributes.addFlashAttribute("msg", "Reserva realizada com sucesso");
		return md;
	}
	
	@PostMapping("/{id}/sair")
	public ModelAndView sair(@PathVariable Long id, RedirectAttributes attributes) {
		Optional<Carona> optional = cr.findById(id);
		ModelAndView md = new ModelAndView();
		md.setViewName("redirect:/caronas");
		if (optional.isEmpty()) {
			attributes.addFlashAttribute("msg", "Carona não encontrada");
			return md;
		}
		
		Carona carona = optional.get();
		Usuario usuario = getPrincipal();
		
		if (carona.getMotorista().getId() == usuario.getId()) {
			attributes.addFlashAttribute("msg", "O motorista não pode sair da carona");
			return md;
		} else {
			for (Usuario p : carona.getPassageiros()) {
				if (p.getId() == usuario.getId()) {
					carona.getPassageiros().remove(p);
					cr.save(carona);			
					attributes.addFlashAttribute("msg", "Usuario removido da carona");
					return md;
				}
			}
		}
				
		attributes.addFlashAttribute("msg", "O usuário não está na carona para ser removido");
		return md;
	}
	
	@PostMapping("/{id}/cancelar")
	public ModelAndView cancelarCarona(@PathVariable Long id, RedirectAttributes attributes) {
		Optional<Carona> optional = cr.findById(id);
		ModelAndView md = new ModelAndView();
		
		if (optional.isEmpty()) {
			md.setViewName("redirect:/caronas");
			attributes.addFlashAttribute("msg", "Carona não encontrada");
			return md;
		}
		Carona carona = optional.get();
		Usuario usuario = getPrincipal();
		
		md.setViewName("redirect:/caronas/{id}");
		if(carona.getMotorista().getId() != usuario.getId()) {
			attributes.addFlashAttribute("msg", "Você não tem permissão para cancelar essa carona");
			return md;
		}
		
		carona.setCancelada(true);
		cr.save(carona);
		attributes.addFlashAttribute("msg", "Carona cancelada!");
		
		return md;
	}

	@GetMapping("/{id}")
	public ModelAndView detalhes(@PathVariable Long id, RedirectAttributes attributes) {
		Optional<Carona> optional = cr.findById(id);
		ModelAndView md = new ModelAndView();
		if (optional.isEmpty()) {
			attributes.addFlashAttribute("msg", "Carona não encontrada");
			md.setViewName("redirect:/caronas");
			return md;
		}
		md.setViewName("caronas/detalhes");
		md.addObject("carona", CaronaItemListaDTO.gerarCaronaItemListaDTO(optional.get(), getPrincipal()));
		return md;
	}

	@GetMapping
	public ModelAndView lista() {
		
		LocalDateTime umaHoraAtras = LocalDateTime.now().minusHours(1);

		List<Carona> all = cr.findByDataAfterAndCanceladaFalseOrderByDataAsc(umaHoraAtras);
		Usuario usuario = getPrincipal();

		List<CaronaItemListaDTO> caronas = CaronaItemListaDTO.gerarCaronaItemListaDTO(all, usuario);

		ModelAndView md = new ModelAndView("caronas/lista");
		md.addObject("caronas", caronas);
		return md;
	}
	
	@GetMapping("/minhas")
	public ModelAndView listaMinhas() {
		
		Usuario usuario = getPrincipal();
		List<Carona> all = cr.findCaronasEnvolvidasUsuario(usuario);

		List<CaronaItemListaDTO> caronas = CaronaItemListaDTO.gerarCaronaItemListaDTO(all, usuario);

		ModelAndView md = new ModelAndView("caronas/lista-minhas");
		md.addObject("caronas", caronas);
		return md;
	}

	// Busca o usuário que está logado
	private Usuario getPrincipal() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails user = (UserDetails) authentication.getPrincipal();
		return ur.findByTelefone(user.getUsername()).get();
	}

}
