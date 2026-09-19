package br.ifrn.caronas.security.config;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.ifrn.caronas.models.Papel;
import br.ifrn.caronas.models.Usuario;
import br.ifrn.caronas.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UsuarioDetailServiceImpl implements UserDetailsService {

	@Autowired
	final UsuarioRepository ur;

	public UsuarioDetailServiceImpl(UsuarioRepository usuarioRepository) {
		this.ur = usuarioRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario user = ur.findByTelefone(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
		System.out.println(user);
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), true,
				true, true, true, user.getAuthorities() == null? new ArrayList<Papel>(): user.getAuthorities());
	}

}
