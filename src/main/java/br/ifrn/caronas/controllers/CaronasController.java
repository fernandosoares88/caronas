package br.ifrn.caronas.controllers;

import br.ifrn.caronas.dtos.CaronaFormDTO;
import br.ifrn.caronas.dtos.CaronaItemListaDTO;
import br.ifrn.caronas.models.Carona;
import br.ifrn.caronas.models.Usuario;
import br.ifrn.caronas.services.CaronaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/caronas")
public class CaronasController {

    private final CaronaService caronaService;

    public CaronasController(CaronaService caronaService) {
        this.caronaService = caronaService;
    }

    @GetMapping("/novo")
    public String form(CaronaFormDTO caronaFormDTO, @AuthenticationPrincipal Usuario usuarioLogado) {

        if (caronaFormDTO.getId() == null || caronaFormDTO.getId() == 0) {
            Carona carona = caronaService.buscarUltimaCaronaPorUsuario(usuarioLogado);
            if (carona != null) {
                caronaFormDTO.setObservacoes(carona.getObservacoes());
                caronaFormDTO.setVagas(carona.getVagas());
                caronaFormDTO.setValor(carona.getValor());
                caronaFormDTO.setPix(carona.getPix());
            }
        }

        return "caronas/form";
    }

    @PostMapping
    public String salvar(@Valid CaronaFormDTO caronaFormDTO, BindingResult result, RedirectAttributes attributes,
                         @AuthenticationPrincipal Usuario usuarioLogado) {
        if (result.hasErrors()) {
            return form(caronaFormDTO, usuarioLogado);
        }

        // Nova carona
        if (caronaFormDTO.getId() == null || caronaFormDTO.getId() == 0) {
            Carona carona = caronaFormDTO.extrair();
            carona.setMotorista(usuarioLogado);

            caronaService.adicionar(carona);

            attributes.addFlashAttribute("success", "Nova carona cadastrada");
            return "redirect:/caronas/novo";

        } else { // Edição de carona
            Carona carona = caronaFormDTO.extrair();
            Carona caronaEditada = caronaService.editar(carona, usuarioLogado);

            if (!Objects.equals(caronaEditada.getVagas(), caronaFormDTO.getVagas()))
                attributes.addFlashAttribute("warning", "Não foi possível alterar as vagas para " + caronaFormDTO.getVagas()
                        + " passageiro(s) pois sua carona já possui " + caronaEditada.getPassageiros().size() + " passageiro(s) confirmado(s)");

            attributes.addFlashAttribute("success", "A carona foi editada");
            return "redirect:/caronas/" + caronaFormDTO.getId();
        }

    }

    @GetMapping("/{id}/editar")
    public String editarSelecionarCarona(@PathVariable Long id, RedirectAttributes attributes,
                                         @AuthenticationPrincipal Usuario usuarioLogado,
                                         Model model) {
        Carona carona = caronaService.findById(id);

        if (!carona.getMotorista().equals(usuarioLogado)) {
            attributes.addFlashAttribute("warning", "Você não tem permissão para editar essa carona");
            return "redirect:/caronas/{id}";
        }
        model.addAttribute("caronaFormDTO", CaronaFormDTO.converter(carona));
        return "caronas/form";
    }


    @PostMapping("/{id}/reservar")
    public String reservar(@PathVariable Long id, RedirectAttributes attributes,
                           @AuthenticationPrincipal Usuario usuarioLogado) {
        caronaService.reservarCarona(Carona.builder().id(id).build(), usuarioLogado);
        attributes.addFlashAttribute("success", "Reserva realizada com sucesso");
        return "redirect:/caronas";
    }

    @PostMapping("/{id}/sair")
    public String sair(@PathVariable Long id, RedirectAttributes attributes,
                       @AuthenticationPrincipal Usuario usuarioLogado) {
        caronaService.sairDaCarona(Carona.builder().id(id).build(), usuarioLogado);
        attributes.addFlashAttribute("success", "Usuário removido da carona.");
        return "redirect:/caronas";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelarCarona(@PathVariable Long id, RedirectAttributes attributes,
                                 @AuthenticationPrincipal Usuario usuarioLogado) {
        caronaService.cancelarCarona(Carona.builder().id(id).build(), usuarioLogado);
        attributes.addFlashAttribute("success", "A carona foi cancelada.");
        return "redirect:/caronas/{id}";

    }

    @GetMapping("/{id}")
    public String detalhes(@PathVariable Long id,
                           @AuthenticationPrincipal Usuario usuarioLogado,
                           Model model) {
        model.addAttribute("carona", CaronaItemListaDTO.converter(caronaService.findById(id), usuarioLogado));
        return "caronas/detalhes";
    }

    @GetMapping
    public String lista(@AuthenticationPrincipal Usuario usuarioLogado,
                        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime filtroDataInicio,
                        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime filtroDataFim,
                        Boolean filtroMinhasCaronas,
                        Boolean filtroMostrarCanceladas,
                        HttpServletRequest request,
                        HttpSession session,
                        Model model) {


        //Tenta recuperar valores da sessão
        if (filtroDataInicio == null)
            filtroDataInicio = (LocalDateTime) session.getAttribute("filtroDataInicio");
        if (filtroDataFim == null)
            filtroDataFim = (LocalDateTime) session.getAttribute("filtroDataFim");

        // Verifica se a requisição veio via HTMX
        boolean isHtmx = "true".equals(request.getHeader("HX-Request"));
        if (filtroMinhasCaronas == null && isHtmx)
            filtroMinhasCaronas = Boolean.FALSE;
        else if (filtroMinhasCaronas == null)
            filtroMinhasCaronas = (Boolean) session.getAttribute("filtroMinhasCaronas");

        // Cria valores padrões caso os filtros venham nulos
        if (filtroDataInicio == null)
            filtroDataInicio = LocalDateTime.now().toLocalDate().atTime(0, 0, 0);

        if (filtroDataFim == null) {
            LocalDateTime agora = LocalDateTime.now();
            DayOfWeek diaSemana = agora.getDayOfWeek();
            if (diaSemana.getValue() < DayOfWeek.FRIDAY.getValue()) {
                filtroDataFim = filtroDataInicio.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
            } else {
                filtroDataFim = filtroDataInicio.plusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            }
            filtroDataFim = filtroDataFim.toLocalDate().atTime(23, 59, 59);
        }

        List<Carona> all = caronaService.search(filtroDataInicio, filtroDataFim,
                Boolean.TRUE.equals(filtroMinhasCaronas) ? usuarioLogado : null,
                null,
                Boolean.TRUE.equals(filtroMostrarCanceladas) ? null : false);

        List<CaronaItemListaDTO> caronas = CaronaItemListaDTO.converter(all, usuarioLogado);

        model.addAttribute("caronas", caronas);
        model.addAttribute("filtroDataInicio", filtroDataInicio);
        model.addAttribute("filtroDataFim", filtroDataFim);
        model.addAttribute("filtroMinhasCaronas", filtroMinhasCaronas);

        session.setAttribute("filtroDataInicio", filtroDataInicio);
        session.setAttribute("filtroDataFim", filtroDataFim);
        session.setAttribute("filtroMinhasCaronas", filtroMinhasCaronas);

        return isHtmx ? "caronas/lista :: #lista-caronas-container" : "caronas/lista";
    }

    @GetMapping("/limpar")
    public String limparFiltros(HttpSession session) {
        session.removeAttribute("filtroDataInicio");
        session.removeAttribute("filtroDataFim");
        session.removeAttribute("filtroMinhasCaronas");

        return "redirect:/caronas";
    }

}
