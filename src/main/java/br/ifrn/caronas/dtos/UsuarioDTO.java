package br.ifrn.caronas.dtos;

import java.util.ArrayList;
import java.util.List;

import br.ifrn.caronas.models.Usuario;
import lombok.Builder;
import lombok.Data;

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
		ArrayList<UsuarioDTO> list = new ArrayList<UsuarioDTO>();
		for (Usuario u : usuarios){
			list.add(converter(u));
		}
		return list;
	}

}
