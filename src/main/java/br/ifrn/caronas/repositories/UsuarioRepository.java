package br.ifrn.caronas.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ifrn.caronas.models.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	
	Optional<Usuario> findByTelefone(String telefone);

}
