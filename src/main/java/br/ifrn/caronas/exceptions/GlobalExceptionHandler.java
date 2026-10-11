package br.ifrn.caronas.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CaronaException.class)
    public String handleCaronaException(CaronaException ex,
                                        RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("danger", ex.getMessage());
        return "redirect:/caronas";
    }

    @ExceptionHandler(UsuarioTelefoneJaCadastradoException.class)
    public String hadleUsuarioTelefoneJaCadastradoException(UsuarioTelefoneJaCadastradoException ex,
                                                            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("danger", ex.getMessage());
        return "redirect:/cadastro";
    }

}
