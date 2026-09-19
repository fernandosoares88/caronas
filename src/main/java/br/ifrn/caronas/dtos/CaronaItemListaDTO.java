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
	private List<String> passageiros;
	
	public static List<CaronaItemListaDTO> gerarCaronaItemListaDTO(List<Carona> caronas, Usuario usuario) {
		List<CaronaItemListaDTO> dtos = new ArrayList<CaronaItemListaDTO>();

		for (Carona c : caronas) {
			dtos.add(gerarCaronaItemListaDTO(c, usuario));
		}

		return dtos;
	}

	public static CaronaItemListaDTO gerarCaronaItemListaDTO(Carona carona, Usuario usuario) {
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
									.build();

		if (carona.getMotorista().getId() == usuario.getId()) {
			dto.setEstouNaCarona(true);
			dto.setSouMotorista(true);
		} else {
			for (Usuario p : carona.getPassageiros()) {
				if (p.getId() == usuario.getId()) {
					dto.setEstouNaCarona(true);
					break;
				}
			}
		}

		dto.setPassageiros(new ArrayList<String>());
		for (Usuario p : carona.getPassageiros()) {
			dto.getPassageiros().add(p.getNome());
		}

		dto.setEsgotada(carona.getVagas() == carona.getPassageiros().size());
		return dto;
	}
	
}
