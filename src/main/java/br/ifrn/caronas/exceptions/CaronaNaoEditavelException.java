package br.ifrn.caronas.exceptions;

public class CaronaNaoEditavelException extends CaronaException {

    public CaronaNaoEditavelException() {
        super("Você não tem permissão para editar esta carona ou ela não pode mais ser alterada");
    }

    public CaronaNaoEditavelException(String message) {
        super(message);
    }
}
