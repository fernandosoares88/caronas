package br.ifrn.caronas.exceptions;

public class UsuarioNaoEstaNaCaronaException extends CaronaException {
    public UsuarioNaoEstaNaCaronaException() {
        super("Usuário não está na carona.");
    }
    public UsuarioNaoEstaNaCaronaException(String message) {
        super(message);
    }
}
