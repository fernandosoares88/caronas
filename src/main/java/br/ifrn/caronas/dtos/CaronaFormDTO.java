package br.ifrn.caronas.dtos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import br.ifrn.caronas.models.Carona;
import br.ifrn.caronas.models.Usuario;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaronaFormDTO {

	private Long id;
	private String direcao;
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime data;
	private Integer vagas;
	private Double valor;
	private String pix;
	private String observacoes;
	private List<UsuarioDTO> passageiros = new ArrayList<>();
	private List<Long> passageirosIds = new ArrayList<>();

	public Carona extrair() {
		List<Usuario> listaPassageiros = null;

		// Se vierem IDs selecionados via formulário
		if (this.passageirosIds != null && !this.passageirosIds.isEmpty()) {
			listaPassageiros = this.passageirosIds.stream()
					.map(id -> Usuario.builder().id(id).build())
					.collect(Collectors.toList());
		} else if (this.passageiros != null) {
			listaPassageiros = UsuarioDTO.extrair(this.passageiros);
		}

		return Carona.builder()
				.id(this.id)
				.direcao(this.direcao)
				.data(this.data)
				.vagas(this.vagas)
				.valor(this.valor)
				.pix(this.getPix())
				.observacoes(this.observacoes)
				.passageiros(listaPassageiros)
				.build();
	}

	public static CaronaFormDTO converter(Carona carona) {
		List<Long> ids = (carona.getPassageiros() != null) ?
				carona.getPassageiros().stream().map(Usuario::getId).collect(Collectors.toList()) : null;

		return CaronaFormDTO.builder()
				.id(carona.getId())
				.direcao(carona.getDirecao())
				.data(carona.getData())
				.vagas(carona.getVagas())
				.valor(carona.getValor())
				.pix(carona.getPix())
				.observacoes(carona.getObservacoes())
				.passageiros(UsuarioDTO.converter(carona.getPassageiros()))
				.passageirosIds(ids)
				.build();
	}
}
