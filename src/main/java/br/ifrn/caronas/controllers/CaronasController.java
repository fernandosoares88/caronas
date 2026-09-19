package br.ifrn.caronas.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
		return "caronas/form";
	}

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
	
	@PostMapping("/{id}/reservar")
	public ModelAndView reservar(@PathVariable Long id, RedirectAttributes attributes) {
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
		
		if(carona.getVagas() <= carona.getPassageiros().size()) {
			attributes.addFlashAttribute("msg", "Carona lotada, não é possível fazer reserva");
			return md;
		}
		
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
		md.addObject("carona", optional.get());
		return md;
	}

	@GetMapping
	public ModelAndView lista() {
		
		LocalDateTime umaHoraAtras = LocalDateTime.now().minusHours(1);

		List<Carona> all = cr.findByDataAfterOrderByDataAsc(umaHoraAtras);
		Usuario usuario = getPrincipal();

		List<CaronaItemListaDTO> caronas = gerarCaronaItemListaDTO(all, usuario);

		ModelAndView md = new ModelAndView("caronas/lista");
		md.addObject("caronas", caronas);
		return md;
	}
	
	@GetMapping("/minhas")
	public ModelAndView listaMinhas() {
		
		LocalDateTime umaHoraAtras = LocalDateTime.now().minusHours(1);

		Usuario usuario = getPrincipal();
		List<Carona> all = cr.findCaronasEnvolvidasUsuario(usuario);

		List<CaronaItemListaDTO> caronas = gerarCaronaItemListaDTO(all, usuario);

		ModelAndView md = new ModelAndView("caronas/lista-minhas");
		md.addObject("caronas", caronas);
		return md;
	}

	private List<CaronaItemListaDTO> gerarCaronaItemListaDTO(List<Carona> caronas, Usuario usuario) {
		List<CaronaItemListaDTO> dtos = new ArrayList<CaronaItemListaDTO>();

		for (Carona c : caronas) {
			dtos.add(gerarCaronaItemListaDTO(c, usuario));
		}

		return dtos;
	}

	private CaronaItemListaDTO gerarCaronaItemListaDTO(Carona carona, Usuario usuario) {
		CaronaItemListaDTO dto = CaronaItemListaDTO.builder()
									.id(carona.getId())
									.direcao(carona.getDirecao())
									.cancelada(carona.isCancelada())
									.data(carona.getData())
									.motorista(carona.getMotorista().getNome())
									.observacoes(carona.getObservacoes())
									.vagas(carona.getVagas())
									.valor(carona.getValor())
									.build();

		if (carona.getMotorista().getId() == usuario.getId()) {
			dto.setEstouNaCarona(true);
			dto.setSouMotorista(true);
		} else {
			for (Usuario p : carona.getPassageiros()) {
				if (p.getId() == usuario.getId()) {
					dto.setEstouNaCarona(true);
					break;
				}
			}
		}

		dto.setPassageiros(new ArrayList<String>());
		for (Usuario p : carona.getPassageiros()) {
			dto.getPassageiros().add(p.getNome());
		}

		dto.setEsgotada(carona.getVagas() == carona.getPassageiros().size());
		return dto;
	}

	private Usuario getPrincipal() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails user = (UserDetails) authentication.getPrincipal();
		return ur.findByTelefone(user.getUsername()).get();
	}

}
