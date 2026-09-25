package br.ifrn.caronas.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.ifrn.caronas.dtos.CaronaFormDTO;
import br.ifrn.caronas.dtos.CaronaItemListaDTO;
import br.ifrn.caronas.models.Carona;
import br.ifrn.caronas.models.Usuario;
import br.ifrn.caronas.repositories.CaronaRepository;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/caronas")
public class CaronasController {

	@Autowired
	private CaronaRepository cr;

	@GetMapping("/novo")
	public String form(CaronaFormDTO caronaFormDTO, @AuthenticationPrincipal Usuario usuarioLogado) {

		if (caronaFormDTO.getId() == null || caronaFormDTO.getId() == 0) {
			Optional<Carona> optional = cr.findFirstByMotoristaOrderByIdDesc(usuarioLogado);
			if (optional.isPresent()) {
				Carona carona = optional.get();
				caronaFormDTO.setObservacoes(carona.getObservacoes());
				caronaFormDTO.setVagas(carona.getVagas());
				caronaFormDTO.setValor(carona.getValor());
			}
		}

		return "caronas/form";
	}

	/**
	 * Salva uma nova carona
	 * 
	 * @param caronaFormDTO
	 * @param result
	 * @param attributes
	 * @return
	 */
	@PostMapping
	public String salvar(@Valid CaronaFormDTO caronaFormDTO, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal Usuario usuarioLogado) {

		if (result.hasErrors()) {
			return form(caronaFormDTO, usuarioLogado);
		}
		System.out.println(caronaFormDTO);

		// Nova carona
		if (caronaFormDTO.getId() == null || caronaFormDTO.getId() == 0) {
			Carona carona = caronaFormDTO.extrair();
			carona.setMotorista(usuarioLogado);

			cr.save(carona);

			attributes.addFlashAttribute("msg", "Nova carona cadastrada");
			return "redirect:/caronas/novo";

		} else { // Edição de carona

			Optional<Carona> optional = cr.findById(caronaFormDTO.getId());
			if (optional.isEmpty()) {
				attributes.addFlashAttribute("msg", "Carona não encontrada");
				return "redirect:/caronas";
			}

			Carona carona = optional.get();

			if (!carona.getMotorista().equals(usuarioLogado) || carona.isCancelada()
					|| !carona.getData().isAfter(LocalDateTime.now())) {

				attributes.addFlashAttribute("msg",
						"Você não tem permissão para editar esta carona ou ela não pode mais ser alterada.");
				return "redirect:/caronas/" + caronaFormDTO.getId();
			}

			if (caronaFormDTO.getVagas() < carona.getPassageiros().size()) {
				attributes.addFlashAttribute("msg2", "Não é possível alterar as vagas para " + caronaFormDTO.getVagas()
						+ " pois sua carona já possui " + carona.getPassageiros().size() + " passageiros");
			} else {
				carona.setVagas(caronaFormDTO.getVagas());
			}

			carona.setDirecao(caronaFormDTO.getDirecao());
			carona.setData(caronaFormDTO.getData());
			carona.setValor(caronaFormDTO.getValor());
			carona.setObservacoes(caronaFormDTO.getObservacoes());

			cr.save(carona);

			attributes.addFlashAttribute("msg", "A carona foi editada");

			return "redirect:/caronas/" + caronaFormDTO.getId();
		}

	}

	@GetMapping("/{id}/editar")
	public ModelAndView editarSelecionarCarona(@PathVariable Long id, RedirectAttributes attributes, @AuthenticationPrincipal Usuario usuarioLogado) {
		Optional<Carona> optional = cr.findById(id);
		ModelAndView md = new ModelAndView();

		if (optional.isEmpty()) {
			md.setViewName("redirect:/caronas");
			attributes.addFlashAttribute("msg", "Carona não encontrada");
			return md;
		}
		Carona carona = optional.get();

		md.setViewName("redirect:/caronas/{id}");
		if (!carona.getMotorista().equals(usuarioLogado)) {
			md.setViewName("redirect:/caronas/{id}");
			attributes.addFlashAttribute("msg", "Você não tem permissão para editar essa carona");
			return md;
		}

		md.setViewName("caronas/form");
		md.addObject("caronaFormDTO", CaronaFormDTO.gerarCaronaRequestDTO(carona));
		System.out.println(carona);
		System.out.println(CaronaFormDTO.gerarCaronaRequestDTO(carona));

		return md;
	}

	/**
	 * Reserva a carona para o usuário logado
	 * 
	 * @param id
	 * @param attributes
	 * @return
	 */
	@PostMapping("/{id}/reservar")
	public ModelAndView reservar(@PathVariable Long id, RedirectAttributes attributes, @AuthenticationPrincipal Usuario usuarioLogado) {
		Optional<Carona> optional = cr.findById(id);
		ModelAndView md = new ModelAndView();
		md.setViewName("redirect:/caronas");

		/** Verifica se a carona existe **/
		if (optional.isEmpty()) {
			attributes.addFlashAttribute("msg", "Carona não encontrada");
			return md;
		}

		Carona carona = optional.get();
		
		/** Validação: Só permite reservar se a data da carona for futura **/
	    if (!carona.getData().isAfter(LocalDateTime.now())) {
	        attributes.addFlashAttribute("msg", "Esta carona já ocorreu ou está ocorrendo, não é possível fazer reserva.");
	        return md;
	    }

		/** Verifica se o usuário já está na carona como motorista ou passageiro **/
		if (carona.getMotorista().equals(usuarioLogado)) {
			attributes.addFlashAttribute("msg", "Usuario já está na carona");
			return md;
		} else {
			for (Usuario p : carona.getPassageiros()) {
				if (p.equals(usuarioLogado)) {
					attributes.addFlashAttribute("msg", "Usuario já está na carona");
					return md;
				}
			}
		}

		/** Verifica se a carona está lotada **/
		if (carona.getVagas() <= carona.getPassageiros().size()) {
			attributes.addFlashAttribute("msg", "Carona lotada, não é possível fazer reserva");
			return md;
		}

		/** Adiciona o usuário na carona **/
		carona.getPassageiros().add(usuarioLogado);
		cr.save(carona);

		attributes.addFlashAttribute("msg", "Reserva realizada com sucesso");
		return md;
	}

	@PostMapping("/{id}/sair")
	public ModelAndView sair(@PathVariable Long id, RedirectAttributes attributes, @AuthenticationPrincipal Usuario usuarioLogado) {
		Optional<Carona> optional = cr.findById(id);
		ModelAndView md = new ModelAndView();
		md.setViewName("redirect:/caronas");
		if (optional.isEmpty()) {
			attributes.addFlashAttribute("msg", "Carona não encontrada");
			return md;
		}

		Carona carona = optional.get();
		
		/** Validação: Só permite sair se a data da carona for futura **/
	    if (!carona.getData().isAfter(LocalDateTime.now())) {
	        attributes.addFlashAttribute("msg", "Esta carona já ocorreu, não é possível sair dela.");
	        return md;
	    }

		if (carona.getMotorista().equals(usuarioLogado)) {
			attributes.addFlashAttribute("msg", "O motorista não pode sair da carona");
			return md;
		} else {
			for (Usuario p : carona.getPassageiros()) {
				if (p.equals(usuarioLogado)) {
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
	public ModelAndView cancelarCarona(@PathVariable Long id, RedirectAttributes attributes, @AuthenticationPrincipal Usuario usuarioLogado) {
		Optional<Carona> optional = cr.findById(id);
		ModelAndView md = new ModelAndView();

		if (optional.isEmpty()) {
			md.setViewName("redirect:/caronas");
			attributes.addFlashAttribute("msg", "Carona não encontrada");
			return md;
		}
		Carona carona = optional.get();

		md.setViewName("redirect:/caronas/{id}");
		if (!carona.getMotorista().equals(usuarioLogado)) {
			attributes.addFlashAttribute("msg", "Você não tem permissão para cancelar essa carona");
			return md;
		}
		
		/** Validação: Só permite cancelar se a data da carona for futura **/
	    if (!carona.getData().isAfter(LocalDateTime.now())) {
	        attributes.addFlashAttribute("msg", "Esta carona já ocorreu, não é possível cancelá-la.");
	        return md;
	    }

		carona.setCancelada(true);
		cr.save(carona);
		attributes.addFlashAttribute("msg", "Carona cancelada!");

		return md;
	}

	@GetMapping("/{id}")
	public ModelAndView detalhes(@PathVariable Long id, RedirectAttributes attributes, @AuthenticationPrincipal Usuario usuarioLogado) {
		Optional<Carona> optional = cr.findById(id);
		ModelAndView md = new ModelAndView();
		if (optional.isEmpty()) {
			attributes.addFlashAttribute("msg", "Carona não encontrada");
			md.setViewName("redirect:/caronas");
			return md;
		}
		md.setViewName("caronas/detalhes");
		md.addObject("carona", CaronaItemListaDTO.gerarCaronaItemListaDTO(optional.get(), usuarioLogado));
		return md;
	}

	@GetMapping
	public ModelAndView lista(@AuthenticationPrincipal Usuario usuarioLogado) {
		
		System.out.println("Usuario logado" + usuarioLogado);

		LocalDateTime umaHoraAtras = LocalDateTime.now().minusHours(1);

		System.out.println("Hora Servidor - 1 hora: " + umaHoraAtras);

		List<Carona> all = cr.findByDataAfterAndCanceladaFalseOrderByDataAsc(umaHoraAtras);

		List<CaronaItemListaDTO> caronas = CaronaItemListaDTO.gerarCaronaItemListaDTO(all, usuarioLogado);

		ModelAndView md = new ModelAndView("caronas/lista");
		md.addObject("caronas", caronas);
		return md;
	}

	@GetMapping("/minhas")
	public ModelAndView listaMinhas(@AuthenticationPrincipal Usuario usuarioLogado) {

		List<Carona> all = cr.findCaronasEnvolvidasUsuario(usuarioLogado);

		List<CaronaItemListaDTO> caronas = CaronaItemListaDTO.gerarCaronaItemListaDTO(all, usuarioLogado);

		ModelAndView md = new ModelAndView("caronas/lista-minhas");
		md.addObject("caronas", caronas);
		return md;
	}

}
