package com.felps.imdbmovieapi.service.impl;

import com.felps.imdbmovieapi.client.OmdbClient;
import com.felps.imdbmovieapi.dto.MovieResponse;
import com.felps.imdbmovieapi.dto.omdb.OmdbMovieDto;
import com.felps.imdbmovieapi.exception.InvalidMovieRequestException;
import com.felps.imdbmovieapi.exception.MovieNotFoundException;
import com.felps.imdbmovieapi.service.MovieService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementacao da regra de negocio de busca de filmes: valida a entrada,
 * delega a chamada externa ao {@link OmdbClient} e traduz a resposta bruta
 * da OMDb API para o formato de saida da nossa API ({@link MovieResponse}).
 */
@Service
public class MovieServiceImpl implements MovieService {

    private static final String NOT_AVAILABLE = "N/A";

    private final OmdbClient omdbClient;

    public MovieServiceImpl(OmdbClient omdbClient) {
        this.omdbClient = omdbClient;
    }

    @Override
    public MovieResponse getMovieByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidMovieRequestException(
                    "O parametro 'title' e obrigatorio e nao pode estar vazio.");
        }

        String trimmedTitle = title.trim();
        OmdbMovieDto dto = omdbClient.fetchMovieByTitle(trimmedTitle);

        if (!dto.isSuccess()) {
            throw new MovieNotFoundException(trimmedTitle);
        }

        return new MovieResponse(
                dto.getTitle(),
                dto.getYear(),
                dto.getType(),
                parseCommaSeparatedList(dto.getGenre()),
                parseCommaSeparatedList(dto.getActors()),
                dto.getImdbRating()
        );
    }

    /**
     * A OMDb API retorna tanto o elenco quanto o genero como uma unica string
     * separada por virgulas (ex: "Leonardo DiCaprio, Joseph Gordon-Levitt" ou
     * "Action, Sci-Fi, Thriller"), ou "N/A" quando a informacao nao esta
     * disponivel. Este metodo converte isso em uma lista limpa para o
     * consumidor da nossa API.
     */
    private List<String> parseCommaSeparatedList(String rawValue) {
        if (rawValue == null || rawValue.trim().isEmpty() || NOT_AVAILABLE.equalsIgnoreCase(rawValue.trim())) {
            return Collections.emptyList();
        }
        return Arrays.stream(rawValue.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .collect(Collectors.toList());
    }
}
