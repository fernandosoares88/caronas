package br.ifrn.caronas.services;

import br.ifrn.caronas.exceptions.UsuarioTelefoneJaCadastradoException;
import br.ifrn.caronas.models.Usuario;
import br.ifrn.caronas.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario adicionar(Usuario usuario) {
        if (usuario.getId() != null) {
            throw new IllegalArgumentException("Não é possível adicionar um usuário que já possui ID.");
        }
        if (usuarioRepository.findByTelefone(usuario.getTelefone()).isPresent()) {
            throw new UsuarioTelefoneJaCadastradoException();
        }
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> findAllByNome(String nome) {
        return usuarioRepository.findAllByNomeContainsIgnoreCase(nome);
    }
}
