package br.ifrn.caronas.dtos;

import br.ifrn.caronas.models.Usuario;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class UsuarioDTO {

    private Long id;
    private String nome;
    private String telefone;

    public static UsuarioDTO converter(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .telefone(usuario.getTelefone())
                .build();
    }

    public static List<UsuarioDTO> converter(List<Usuario> usuarios) {
        return usuarios != null ? usuarios.stream().map(UsuarioDTO::converter).collect(Collectors.toList()): null;
    }

    public static Usuario extrair(UsuarioDTO usuarioDTO) {
        return Usuario.builder()
                .id(usuarioDTO.id)
                .nome(usuarioDTO.nome)
                .telefone(usuarioDTO.telefone)
                .build();
    }

    public static List<Usuario> extrair(List<UsuarioDTO> usuarioDTOs) {
        return usuarioDTOs != null ? usuarioDTOs.stream().map(UsuarioDTO::extrair).collect(Collectors.toList()) : null;
    }
}


