package com.felps.imdbmovieapi.exception;

/**
 * Lancada quando ocorre um problema tecnico ao chamar a OMDb API:
 * timeout, indisponibilidade, resposta invalida/inesperada, etc.
 * Mapeada para HTTP 502 (Bad Gateway) pelo {@link GlobalExceptionHandler},
 * pois o erro se origina em um servico externo, nao na nossa API.
 */
public class OmdbServiceException extends RuntimeException {

    public OmdbServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
