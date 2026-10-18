package br.ifrn.caronas.dtos;

import br.ifrn.caronas.models.Usuario;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilFormDTO {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    private String telefone; // Exibido apenas como leitura

    private String senhaAtual;
    private String novaSenha;
    private String confirmarNovaSenha;

    public static PerfilFormDTO converter(Usuario usuario) {
        return PerfilFormDTO.builder()
                .nome(usuario.getNome())
                .telefone(usuario.getTelefone())
                .build();
    }
}