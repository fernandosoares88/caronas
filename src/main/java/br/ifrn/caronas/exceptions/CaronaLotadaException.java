package br.ifrn.caronas.exceptions;

public class CaronaLotadaException extends CaronaException {

    public CaronaLotadaException() {
        super("Esta carona já está lotada.");
    }

    public CaronaLotadaException(String message) {
        super(message);
    }
}
