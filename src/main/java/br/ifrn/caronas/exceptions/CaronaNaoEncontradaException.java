package br.ifrn.caronas.exceptions;

public class CaronaNaoEncontradaException extends CaronaException {

    public CaronaNaoEncontradaException() {
        super("Carona não encontrada.");
    }

    public CaronaNaoEncontradaException(String message) {
        super(message);
    }
}
