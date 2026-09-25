package br.ifrn.caronas.dtos;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import br.ifrn.caronas.models.Carona;
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
	private String observacoes;
	
	public Carona extrair() {
		return Carona.builder()
				.id(this.id)
				.direcao(this.direcao)
				.data(this.data)
				.vagas(this.vagas)
				.valor(this.valor)
				.observacoes(this.observacoes)
				.build();
	}
	
	public static CaronaFormDTO gerarCaronaRequestDTO(Carona carona) {
		return CaronaFormDTO.builder()
				.id(carona.getId())
				.direcao(carona.getDirecao())
				.data(carona.getData())
				.vagas(carona.getVagas())
				.valor(carona.getValor())
				.observacoes(carona.getObservacoes())
				.build();
	}

}
