package br.ifrn.caronas.services;

import br.ifrn.caronas.exceptions.*;
import br.ifrn.caronas.models.Carona;
import br.ifrn.caronas.models.Usuario;
import br.ifrn.caronas.repositories.CaronaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CaronaService {

    private final CaronaRepository caronaRepository;

    public CaronaService(CaronaRepository caronaRepository) {
        this.caronaRepository = caronaRepository;
    }

    public Carona adicionar(Carona carona) {
        if (carona.getId() != null) {
            throw new IllegalArgumentException("Não é possível adicionar uma carona que já possui ID.");
        }
        if(carona.getData().isBefore(LocalDateTime.now()))
            throw new CaronaException("Não é possível adicionar uma carona com data anterior.");

        if(carona.getPassageiros() != null)
            carona.getPassageiros().remove(carona.getMotorista());
        if (carona.getPassageiros() != null && carona.getPassageiros().size() > carona.getVagas())
            throw new CaronaNaoEditavelException("Não foi possível adicionar a carona pois a quantidade de vagas é menor que a quantidade de passageiros.");

        return caronaRepository.save(carona);
    }

    public Carona editar(Carona carona, Usuario usuarioLogado) {

        Carona dbCarona = caronaRepository.findById(carona.getId()).orElseThrow(CaronaNaoEncontradaException::new);

        if (!dbCarona.getMotorista().equals(usuarioLogado) || dbCarona.isCancelada()
                || !dbCarona.getData().isAfter(LocalDateTime.now())) {
            throw new CaronaNaoEditavelException();
        }

        if(carona.getPassageiros() != null)
            carona.getPassageiros().remove(usuarioLogado);
        if (carona.getPassageiros() != null && carona.getPassageiros().size() > carona.getVagas())
            throw new CaronaNaoEditavelException("Não foi possível editar a carona pois a quantidade de vagas é menor que a quantidade de passageiros.");

        dbCarona.setDirecao(carona.getDirecao());
        dbCarona.setData(carona.getData());
        dbCarona.setVagas(carona.getVagas());
        dbCarona.setValor(carona.getValor());
        dbCarona.setPassageiros(carona.getPassageiros());
        dbCarona.setPix(carona.getPix());
        dbCarona.setObservacoes(carona.getObservacoes());

        return caronaRepository.save(dbCarona);
    }

    public Carona findById(Long id) {
        return caronaRepository.findById(id).orElseThrow(CaronaNaoEncontradaException::new);
    }

    public List<Carona> search(LocalDateTime inicio, LocalDateTime fim,
                               Usuario usuario, Usuario motorista,
                               Boolean cancelada) {
        return caronaRepository.filtrarCaronas(inicio, fim, usuario, motorista, cancelada);
    }

    public Carona buscarUltimaCaronaPorUsuario(Usuario usuario) {
        return caronaRepository.findFirstByMotoristaOrderByIdDesc(usuario).orElse(null);
    }

    public Carona reservarCarona(Carona carona, Usuario usuario) {
        Carona dbCarona = findById(carona.getId());

        /* Validação: Só permite reservar se a data da carona for futura */
        /* Validação: Só permite reservar se não estiver cancelada */
        if (dbCarona.getData().isBefore(LocalDateTime.now()) || dbCarona.isCancelada())
            throw new CaronaNaoEditavelException("Não é possível reservar esta carona.");

        /* Validação: Só permite reservar se o usuário NÃO for motorista ou passageiro */
        if (usuario.equals(dbCarona.getMotorista()) || dbCarona.getPassageiros().contains(usuario))
            throw new UsuarioEstaNaCaronaException();

        /* Validação: verifica se a carona está lotada */
        if (dbCarona.getVagas() <= dbCarona.getPassageiros().size())
            throw new CaronaLotadaException();

        /* Adiciona o usuário na carona */
        dbCarona.getPassageiros().add(usuario);
        return caronaRepository.save(dbCarona);
    }

    public Carona sairDaCarona(Carona carona, Usuario usuario) {
        Carona dbCarona = findById(carona.getId());

        /* Validação: Só permite sair da carona se a data da carona for futura */
        /* Validação: Só permite sair da carona se não estiver cancelada */
        /* Validação: Só permite sair da carona se o usuário NÃO for o motorista */
        if (dbCarona.getData().isBefore(LocalDateTime.now()) || dbCarona.isCancelada() || dbCarona.getMotorista().equals(usuario))
            throw new CaronaNaoEditavelException("Não é possível remover reserva desta carona.");

        /* Validação: Só permite sair da carona se o usuário for um passageiro */
        if (!dbCarona.getPassageiros().contains(usuario))
            throw new UsuarioNaoEstaNaCaronaException();

        /* Remove o usuário da carona */
        dbCarona.getPassageiros().remove(usuario);
        return caronaRepository.save(dbCarona);
    }

    public Carona cancelarCarona(Carona carona, Usuario usuario) {
        Carona dbCarona = findById(carona.getId());

        /* Validação: Só permite cancelar carona se a data da carona for futura */
        /* Validação: Só permite cancelar carona se não estiver cancelada */
        /* Validação: Só permite cancelar carona se o usuário for o motorista */
        if (dbCarona.getData().isBefore(LocalDateTime.now()) || dbCarona.isCancelada() || !dbCarona.getMotorista().equals(usuario))
            throw new CaronaNaoEditavelException();

        /* Cancela a carona */
        dbCarona.setCancelada(true);
        return caronaRepository.save(dbCarona);
    }
}
