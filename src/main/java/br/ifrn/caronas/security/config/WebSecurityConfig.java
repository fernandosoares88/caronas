package br.ifrn.caronas.security.config;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
	
	@Value("${REMEMBER_ME_KEY:chavePadraoSeNaoEncontrar}")
	private String rememberMeKey;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    
		System.out.println("ALERT:TESTE:::: => RememberKey: " + rememberMeKey);
		
		http.authorizeHttpRequests((requests) -> requests
	            .requestMatchers("/", "/cadastro").permitAll()
	            .anyRequest().authenticated())
	        .formLogin((form) -> form
	            .loginPage("/login")
	            .defaultSuccessUrl("/caronas", true)
	            .permitAll()
	        )
	        .logout((logout) -> logout.permitAll())
	        .rememberMe(remember -> remember
	                .key(rememberMeKey) 
	                .tokenValiditySeconds(86400 * 14) 
	            );;
	        
	    return http.build();
	}
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
