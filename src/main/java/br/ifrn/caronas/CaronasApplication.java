package br.ifrn.caronas;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class CaronasApplication {
	
	@PostConstruct
    public void init() {
        // Define o fuso horário padrão para o horário de Brasília (ajuste se necessário)
        TimeZone.setDefault(TimeZone.getTimeZone("America/Fortaleza"));
    }

	public static void main(String[] args) {
		SpringApplication.run(CaronasApplication.class, args);
		System.out.println("iniciou a aplicação");
	}

}
