package br.ifrn.caronas.exceptions;

public class UsuarioEstaNaCaronaException extends CaronaException {
    public UsuarioEstaNaCaronaException() {
        super("Usuário já está na carona.");
    }
    public UsuarioEstaNaCaronaException(String message) {
        super(message);
    }
}
