package com.felps.imdbmovieapi.controller;

import com.felps.imdbmovieapi.dto.MovieResponse;
import com.felps.imdbmovieapi.service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Camada de apresentacao (Controller da arquitetura MVC): expoe o endpoint
 * HTTP e delega toda a regra de negocio para {@link MovieService}.
 *
 * <p>Exemplo de uso:</p>
 * <pre>
 * GET /api/movies?title=Inception
 * </pre>
 */
@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * Busca informacoes de um filme (nome, ano, elenco e avaliacao) a
     * partir do titulo informado via query param.
     *
     * @param title titulo do filme, ex: "Inception"
     */
    @GetMapping
    public ResponseEntity<MovieResponse> getMovie(@RequestParam("title") String title) {
        MovieResponse movie = movieService.getMovieByTitle(title);
        return ResponseEntity.ok(movie);
    }
}
