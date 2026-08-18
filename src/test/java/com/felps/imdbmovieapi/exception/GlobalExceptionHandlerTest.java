package com.felps.imdbmovieapi.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.ServletWebRequest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testa o {@link GlobalExceptionHandler} isoladamente, verificando se cada
 * tipo de excecao e traduzido para o status HTTP e corpo de resposta corretos.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final ServletWebRequest request =
            new ServletWebRequest(new MockHttpServletRequest("GET", "/api/movies"));

    @Test
    void handleMovieNotFound_shouldReturn404() {
        ResponseEntity<ApiError> response =
                handler.handleMovieNotFound(new MovieNotFoundException("xyz"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getMessage()).contains("xyz");
    }

    @Test
    void handleInvalidRequest_shouldReturn400() {
        ResponseEntity<ApiError> response =
                handler.handleInvalidRequest(new InvalidMovieRequestException("titulo invalido"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).isEqualTo("titulo invalido");
    }

    @Test
    void handleMissingParam_shouldReturn400() throws Exception {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("title", "String");

        ResponseEntity<ApiError> response = handler.handleMissingParam(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("title");
    }

    @Test
    void handleOmdbServiceException_shouldReturn502() {
        ResponseEntity<ApiError> response = handler.handleOmdbServiceException(
                new OmdbServiceException("timeout", null), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
    }

    @Test
    void handleGenericException_shouldReturn500_andHideInternalDetails() {
        ResponseEntity<ApiError> response =
                handler.handleGenericException(new RuntimeException("stack trace sensivel"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getMessage()).doesNotContain("stack trace sensivel");
    }
}
