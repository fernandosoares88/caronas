package br.ifrn.caronas.repositories;

import br.ifrn.caronas.models.Carona;
import br.ifrn.caronas.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CaronaRepository extends JpaRepository<Carona, Long> {

    Optional<Carona> findFirstByMotoristaOrderByIdDesc(Usuario motorista);

    @Query("SELECT c FROM Carona c WHERE c.data BETWEEN :inicio AND :fim " +
            "AND (:usuario IS NULL OR c.motorista = :usuario OR :usuario MEMBER OF c.passageiros) " +
            "AND (:motorista IS NULL OR c.motorista = :motorista) " +
            "AND (:cancelada IS NULL OR c.cancelada = :cancelada)" +
            "ORDER BY c.data ASC" )
    List<Carona> filtrarCaronas(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("usuario") Usuario usuario,
            @Param("motorista") Usuario motorista,
            @Param("cancelada") Boolean cancelada
    );

}
