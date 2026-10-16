package br.ifrn.caronas.dtos;

import br.ifrn.caronas.models.Usuario;
import lombok.Builder;
import lombok.Data;

import java.util.List;

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
        return usuarios.stream().map(UsuarioDTO::converter).toList();
    }

}
