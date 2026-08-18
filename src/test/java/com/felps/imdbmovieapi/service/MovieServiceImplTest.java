package com.felps.imdbmovieapi.service;

import com.felps.imdbmovieapi.client.OmdbClient;
import com.felps.imdbmovieapi.dto.MovieResponse;
import com.felps.imdbmovieapi.dto.omdb.OmdbMovieDto;
import com.felps.imdbmovieapi.exception.InvalidMovieRequestException;
import com.felps.imdbmovieapi.exception.MovieNotFoundException;
import com.felps.imdbmovieapi.service.impl.MovieServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitarios da camada de servico. O {@link OmdbClient} e mockado
 * para que estes testes validem apenas a regra de negocio (parsing do
 * elenco, tratamento de "nao encontrado", validacao de entrada), sem
 * depender de rede ou do contexto do Spring.
 */
@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

    @Mock
    private OmdbClient omdbClient;

    @InjectMocks
    private MovieServiceImpl movieService;

    @Test
    void getMovieByTitle_shouldReturnMovie_whenOmdbFindsIt() {
        OmdbMovieDto dto = new OmdbMovieDto();
        dto.setResponse("True");
        dto.setTitle("Inception");
        dto.setYear("2010");
        dto.setActors("Leonardo DiCaprio, Joseph Gordon-Levitt, Elliot Page");
        dto.setGenre("Action, Sci-Fi, Thriller");
        dto.setType("movie");
        dto.setImdbRating("8.8");

        when(omdbClient.fetchMovieByTitle("Inception")).thenReturn(dto);

        MovieResponse result = movieService.getMovieByTitle("Inception");

        assertThat(result.getTitle()).isEqualTo("Inception");
        assertThat(result.getYear()).isEqualTo("2010");
        assertThat(result.getType()).isEqualTo("movie");
        assertThat(result.getRating()).isEqualTo("8.8");
        assertThat(result.getCast())
                .containsExactly("Leonardo DiCaprio", "Joseph Gordon-Levitt", "Elliot Page");
        assertThat(result.getGenre())
                .containsExactly("Action", "Sci-Fi", "Thriller");
    }

    @Test
    void getMovieByTitle_shouldReturnTypeSeries_whenOmdbTitleIsASeries() {
        OmdbMovieDto dto = new OmdbMovieDto();
        dto.setResponse("True");
        dto.setTitle("Breaking Bad");
        dto.setYear("2008-2013");
        dto.setType("series");
        dto.setGenre("Crime, Drama, Thriller");
        dto.setActors("Bryan Cranston, Aaron Paul, Anna Gunn");
        dto.setImdbRating("9.5");

        when(omdbClient.fetchMovieByTitle("Breaking Bad")).thenReturn(dto);

        MovieResponse result = movieService.getMovieByTitle("Breaking Bad");

        assertThat(result.getType()).isEqualTo("series");
        assertThat(result.getGenre()).containsExactly("Crime", "Drama", "Thriller");
    }

    @Test
    void getMovieByTitle_shouldTrimTitleBeforeCallingClient() {
        OmdbMovieDto dto = new OmdbMovieDto();
        dto.setResponse("True");
        dto.setTitle("Inception");
        dto.setActors("N/A");
        when(omdbClient.fetchMovieByTitle("Inception")).thenReturn(dto);

        movieService.getMovieByTitle("  Inception  ");

        verify(omdbClient).fetchMovieByTitle("Inception");
    }

    @Test
    void getMovieByTitle_shouldReturnEmptyCast_whenActorsIsNotAvailable() {
        OmdbMovieDto dto = new OmdbMovieDto();
        dto.setResponse("True");
        dto.setTitle("Some Obscure Movie");
        dto.setActors("N/A");
        dto.setGenre("N/A");
        when(omdbClient.fetchMovieByTitle(anyString())).thenReturn(dto);

        MovieResponse result = movieService.getMovieByTitle("Some Obscure Movie");

        assertThat(result.getCast()).isEqualTo(Collections.<String>emptyList());
        assertThat(result.getGenre()).isEqualTo(Collections.<String>emptyList());
    }

    @Test
    void getMovieByTitle_shouldThrowMovieNotFound_whenOmdbResponseIsFalse() {
        OmdbMovieDto dto = new OmdbMovieDto();
        dto.setResponse("False");
        dto.setError("Movie not found!");
        when(omdbClient.fetchMovieByTitle("xyz-inexistente")).thenReturn(dto);

        assertThrows(MovieNotFoundException.class,
                () -> movieService.getMovieByTitle("xyz-inexistente"));
    }

    @Test
    void getMovieByTitle_shouldThrowInvalidRequest_whenTitleIsNull() {
        assertThrows(InvalidMovieRequestException.class,
                () -> movieService.getMovieByTitle(null));
        verify(omdbClient, never()).fetchMovieByTitle(anyString());
    }

    @Test
    void getMovieByTitle_shouldThrowInvalidRequest_whenTitleIsBlank() {
        assertThrows(InvalidMovieRequestException.class,
                () -> movieService.getMovieByTitle("   "));
        verify(omdbClient, never()).fetchMovieByTitle(anyString());
    }
}
