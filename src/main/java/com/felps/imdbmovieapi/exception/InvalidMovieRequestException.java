package com.felps.imdbmovieapi.exception;

/**
 * Lancada quando o parametro "title" recebido na requisicao e invalido
 * (por exemplo, vazio ou apenas espacos).
 * Mapeada para HTTP 400 pelo {@link GlobalExceptionHandler}.
 */
public class InvalidMovieRequestException extends RuntimeException {

    public InvalidMovieRequestException(String message) {
        super(message);
    }
}
