package br.ifrn.caronas.dtos;

import br.ifrn.caronas.models.Carona;
import br.ifrn.caronas.models.Usuario;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class CaronaItemListaDTO {

    private Long id;
    private String direcao;
    private LocalDateTime data;
    private Integer vagas;
    private Double valor;
    private String pix;
    private String observacoes;
    private String motorista;
    private String motoristaTelefone;
    private boolean cancelada;
    private boolean estouNaCarona;
    private boolean souMotorista;
    private boolean esgotada;
    private boolean realizada;
    private List<UsuarioDTO> passageiros = new ArrayList<>();

    public static List<CaronaItemListaDTO> converter(List<Carona> caronas, Usuario usuarioLogado) {
        return caronas.stream().map(c -> CaronaItemListaDTO.converter(c, usuarioLogado)).collect(Collectors.toList());
    }

    public static CaronaItemListaDTO converter(Carona carona, Usuario usuarioLogado) {
        return CaronaItemListaDTO.builder()
                .id(carona.getId())
                .direcao(carona.getDirecao())
                .data(carona.getData())
                .vagas(carona.getVagas())
                .valor(carona.getValor())
                .pix(carona.getPix())
                .observacoes(carona.getObservacoes())
                .motorista(carona.getMotorista().getNome())
                .motoristaTelefone(carona.getMotorista().getTelefone())
                .cancelada(carona.isCancelada())
                .estouNaCarona(carona.getMotorista().equals(usuarioLogado) || carona.getPassageiros().contains(usuarioLogado))
                .souMotorista(carona.getMotorista().equals(usuarioLogado))
                .esgotada(carona.getVagas() == carona.getPassageiros().size())
                .realizada(carona.getData().isBefore(LocalDateTime.now()))
                .passageiros(UsuarioDTO.converter(carona.getPassageiros()))
                .build();
    }

}
