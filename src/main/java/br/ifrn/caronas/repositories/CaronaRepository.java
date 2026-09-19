package br.ifrn.caronas.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.ifrn.caronas.models.Carona;
import br.ifrn.caronas.models.Usuario;

public interface CaronaRepository extends JpaRepository<Carona, Long> {
	
	Optional<Carona> findFirstByMotoristaOrderByIdDesc(Usuario motorista);
	
	List<Carona> findAllByMotorista(Usuario motorista);
	
	List<Carona> findAllByOrderByData();
	
	List<Carona> findByDataAfterAndCanceladaFalseOrderByDataAsc(LocalDateTime limite);
	
	// Busca caronas onde o usuário é o motorista OU está na lista de passageiros
    @Query("SELECT DISTINCT c FROM Carona c LEFT JOIN c.passageiros p WHERE c.motorista = :usuario OR :usuario MEMBER OF c.passageiros ORDER BY c.data ASC")
    List<Carona> findCaronasEnvolvidasUsuario(@Param("usuario") Usuario usuario);

}
