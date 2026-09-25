package br.ifrn.caronas.models;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.annotation.Lazy;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor  
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Carona {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;
	private String direcao;
	private LocalDateTime data;
	private Integer vagas;
	private Double valor;
	private String observacoes;
	private boolean cancelada;
	
	@ManyToOne
	private Usuario motorista;
	
	@ManyToMany
	@Lazy(value = false)
	private List<Usuario> passageiros;

}
