package com.felps.imdbmovieapi.dto.omdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representa o payload JSON retornado pela OMDb API.
 *
 * <p>A OMDb API usa nomes de campo em PascalCase (Title, Year, Actors...)
 * e sinaliza sucesso/erro atraves do campo "Response" ("True"/"False"),
 * em vez de usar codigos HTTP de erro. Por isso o campo {@link #response}
 * e o metodo {@link #isSuccess()} sao usados para decidir se o filme foi
 * encontrado.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OmdbMovieDto {

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Year")
    private String year;

    @JsonProperty("Actors")
    private String actors;

    @JsonProperty("Genre")
    private String genre;

    /**
     * Tipo de titulo retornado pela OMDb: "movie", "series" ou "episode".
     * Guardado aqui para permitir, no futuro, buscar series pelo mesmo
     * endpoint (usando somente o titulo).
     */
    @JsonProperty("Type")
    private String type;

    @JsonProperty("imdbRating")
    private String imdbRating;

    /** "True" quando o filme foi encontrado, "False" caso contrario. */
    @JsonProperty("Response")
    private String response;

    /** Mensagem de erro retornada pela OMDb quando Response = "False". */
    @JsonProperty("Error")
    private String error;

    public boolean isSuccess() {
        return "True".equalsIgnoreCase(response);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
