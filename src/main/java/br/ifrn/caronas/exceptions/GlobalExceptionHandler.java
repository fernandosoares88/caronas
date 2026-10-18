package br.ifrn.caronas.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CaronaException.class)
    public String handleCaronaException(CaronaException ex,
                                        HttpServletRequest request,
                                        RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("danger", ex.getMessage());

        // Obtém a URL de origem da requisição do cabeçalho Referer
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            return "redirect:" + referer;
        }

        return "redirect:/caronas";
    }

    @ExceptionHandler(UsuarioTelefoneJaCadastradoException.class)
    public String handleUsuarioTelefoneJaCadastradoException(UsuarioTelefoneJaCadastradoException ex,
                                                             RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("danger", ex.getMessage());
        return "redirect:/cadastro";
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleException(RuntimeException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("danger", ex.getMessage());
        return "redirect:/caronas";
    }

}
