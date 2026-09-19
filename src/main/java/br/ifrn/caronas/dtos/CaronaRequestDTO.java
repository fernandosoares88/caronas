package br.ifrn.caronas.dtos;

import java.time.LocalDateTime;

import br.ifrn.caronas.models.Carona;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaronaRequestDTO {
	
	private String direcao;
	private LocalDateTime data;
	private Integer vagas;
	private Double valor;
	private String observacoes;
	
	public Carona extrair() {
		return Carona.builder()
				.direcao(this.direcao)
				.data(this.data)
				.vagas(this.vagas)
				.valor(this.valor)
				.observacoes(this.observacoes)
				.build();
	}

}
