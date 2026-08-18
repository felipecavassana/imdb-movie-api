package com.felps.imdbmovieapi.exception;

/**
 * Lancada quando a OMDb API responde que nao encontrou nenhum filme
 * para o titulo pesquisado (Response = "False").
 * Mapeada para HTTP 404 pelo {@link GlobalExceptionHandler}.
 */
public class MovieNotFoundException extends RuntimeException {

    public MovieNotFoundException(String title) {
        super("Nenhum filme encontrado para o titulo: '" + title + "'");
    }
}
