package br.ifrn.caronas.exceptions;

public class UsuarioTelefoneJaCadastradoException extends RuntimeException {
    public UsuarioTelefoneJaCadastradoException() {
        super("Já existe um usuário cadastrado com esse telefone.");
    }

    public UsuarioTelefoneJaCadastradoException(String message) {
        super(message);
    }
}
