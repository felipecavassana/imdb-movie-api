package com.felps.imdbmovieapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicacao Spring Boot.
 *
 * <p>Sobe um servidor embutido (Tomcat) e expoe a API REST de consulta
 * de filmes definida em {@link com.felps.imdbmovieapi.controller.MovieController}.</p>
 */
@SpringBootApplication
public class ImdbMovieApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImdbMovieApiApplication.class, args);
    }
}
