package br.ifrn.caronas.dtos;

import java.time.LocalDateTime;
import java.util.List;

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
	private boolean cancelada;
	private boolean estouNaCarona;
	private boolean souMotorista;
	private boolean esgotada;
	private List<String> passageiros;
	
}
