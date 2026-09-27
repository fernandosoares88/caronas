package br.ifrn.caronas.dtos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.ifrn.caronas.models.Carona;
import br.ifrn.caronas.models.Usuario;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaronaItemListaDTO {

	private Long id;
	private String direcao;
	private LocalDateTime data;
	private Integer vagas;
	private Double valor;
	private String observacoes;
	private String motorista;
	private String motoristaTelefone;
	private boolean cancelada;
	private boolean estouNaCarona;
	private boolean souMotorista;
	private boolean esgotada;
	private boolean realizada;
	private List<UsuarioDTO> passageiros;
	
	public static List<CaronaItemListaDTO> converter(List<Carona> caronas, Usuario usuarioLogado) {
		List<CaronaItemListaDTO> dtos = new ArrayList<CaronaItemListaDTO>();

		for (Carona c : caronas) {
			dtos.add(converter(c, usuarioLogado));
		}

		return dtos;
	}

	public static CaronaItemListaDTO converter(Carona carona, Usuario usuarioLogado) {
		CaronaItemListaDTO dto = CaronaItemListaDTO.builder()
									.id(carona.getId())
									.direcao(carona.getDirecao())
									.cancelada(carona.isCancelada())
									.data(carona.getData())
									.motorista(carona.getMotorista().getNome())
									.motoristaTelefone(carona.getMotorista().getTelefone())
									.observacoes(carona.getObservacoes())
									.vagas(carona.getVagas())
									.valor(carona.getValor())
									.passageiros(UsuarioDTO.converter(carona.getPassageiros()))
									.build();

		if (carona.getMotorista().equals(usuarioLogado)) {
			dto.setEstouNaCarona(true);
			dto.setSouMotorista(true);
		} else {
			for (Usuario p : carona.getPassageiros()) {
				if (p.equals(usuarioLogado)) {
					dto.setEstouNaCarona(true);
					break;
				}
			}
		}

		dto.setEsgotada(carona.getVagas() == carona.getPassageiros().size());
		dto.setRealizada(carona.getData().isBefore(LocalDateTime.now()));
		return dto;
	}
	
}
