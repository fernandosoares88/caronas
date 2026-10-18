package br.ifrn.caronas.services;

import br.ifrn.caronas.dtos.PerfilFormDTO;
import br.ifrn.caronas.exceptions.UsuarioTelefoneJaCadastradoException;
import br.ifrn.caronas.models.Usuario;
import br.ifrn.caronas.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario adicionar(Usuario usuario) {
        if (usuario.getId() != null) {
            throw new IllegalArgumentException("Não é possível adicionar um usuário que já possui ID.");
        }
        if (usuarioRepository.findByTelefone(usuario.getTelefone()).isPresent()) {
            throw new UsuarioTelefoneJaCadastradoException();
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void atualizarPerfil(Usuario usuarioLogado, PerfilFormDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioLogado.getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // Atualiza o nome
        usuario.setNome(dto.getNome());

        // Se o usuário preencheu o campo de nova senha, processa a alteração
        if (dto.getNovaSenha() != null && !dto.getNovaSenha().isBlank()) {
            if (dto.getSenhaAtual() == null || !passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenha())) {
                throw new IllegalArgumentException("A senha atual informada está incorreta.");
            }
            if (!dto.getNovaSenha().equals(dto.getConfirmarNovaSenha())) {
                throw new IllegalArgumentException("A nova senha e a confirmação não conferem.");
            }
            usuario.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
        }

        usuarioRepository.save(usuario);
    }

    @Transactional
    public void resetarSenha(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // Redefine a senha para o valor padrão 123456
        usuario.setSenha(passwordEncoder.encode("123456"));
        usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAllByOrderByNomeAsc();
    }
    public List<Usuario> findAllByNome(String nome) {
        return usuarioRepository.findAllByNomeContainsIgnoreCase(nome);
    }
}
