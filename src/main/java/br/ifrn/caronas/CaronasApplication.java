package br.ifrn.caronas;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class CaronasApplication {

    @PostConstruct
    public void init() {
        // Define o fuso horário padrão para o horário de Brasília (ajuste se necessário)
        TimeZone.setDefault(TimeZone.getTimeZone("America/Fortaleza"));
    }

    static void main(String[] args) {
        SpringApplication.run(CaronasApplication.class, args);
    }

}
