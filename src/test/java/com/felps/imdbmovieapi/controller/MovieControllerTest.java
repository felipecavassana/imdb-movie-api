package com.felps.imdbmovieapi.controller;

import com.felps.imdbmovieapi.dto.MovieResponse;
import com.felps.imdbmovieapi.exception.InvalidMovieRequestException;
import com.felps.imdbmovieapi.exception.MovieNotFoundException;
import com.felps.imdbmovieapi.exception.OmdbServiceException;
import com.felps.imdbmovieapi.service.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testa apenas a camada web (Controller + GlobalExceptionHandler), com a
 * camada de servico mockada via {@code @MockBean}. Nao sobe o contexto
 * completo do Spring nem faz chamadas HTTP reais.
 */
@WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @Test
    void getMovie_shouldReturn200WithMovieData_whenMovieIsFound() throws Exception {
        MovieResponse movie = new MovieResponse(
                "Inception", "2010", "movie",
                Arrays.asList("Action", "Sci-Fi", "Thriller"),
                Arrays.asList("Leonardo DiCaprio", "Joseph Gordon-Levitt"), "8.8");
        when(movieService.getMovieByTitle("Inception")).thenReturn(movie);

        mockMvc.perform(get("/api/movies").param("title", "Inception"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.title").value("Inception"))
                .andExpect(jsonPath("$.year").value("2010"))
                .andExpect(jsonPath("$.type").value("movie"))
                .andExpect(jsonPath("$.rating").value("8.8"))
                .andExpect(jsonPath("$.genre", org.hamcrest.Matchers.hasSize(3)))
                .andExpect(jsonPath("$.genre[0]").value("Action"))
                .andExpect(jsonPath("$.cast", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$.cast[0]").value("Leonardo DiCaprio"));
    }

    @Test
    void getMovie_shouldReturn404_whenMovieIsNotFound() throws Exception {
        when(movieService.getMovieByTitle(eq("filme-inexistente-xyz")))
                .thenThrow(new MovieNotFoundException("filme-inexistente-xyz"));

        mockMvc.perform(get("/api/movies").param("title", "filme-inexistente-xyz"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getMovie_shouldReturn400_whenTitleParamIsMissing() throws Exception {
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void getMovie_shouldReturn400_whenTitleIsBlank() throws Exception {
        when(movieService.getMovieByTitle(" "))
                .thenThrow(new InvalidMovieRequestException("O parametro 'title' e obrigatorio."));

        mockMvc.perform(get("/api/movies").param("title", " "))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMovie_shouldReturn502_whenOmdbApiFails() throws Exception {
        when(movieService.getMovieByTitle("Inception"))
                .thenThrow(new OmdbServiceException("Falha ao consultar a OMDb API", null));

        mockMvc.perform(get("/api/movies").param("title", "Inception"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502));
    }
}
