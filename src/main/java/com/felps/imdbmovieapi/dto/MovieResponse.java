package com.felps.imdbmovieapi.dto;

import java.util.List;
import java.util.Objects;

/**
 * DTO de saida da nossa API: contem apenas os campos que o cliente
 * desta API pediu (nome, ano, tipo, genero, elenco e avaliacao), ja
 * tratados e normalizados a partir da resposta bruta da OMDb API.
 *
 * <p>O campo {@code type} ("movie", "series" ou "episode") e retornado
 * desde ja para preparar o terreno para quando este mesmo endpoint
 * passar a poder ser usado tambem para buscar series pelo titulo.</p>
 */
public class MovieResponse {

    private String title;
    private String year;
    private String type;
    private List<String> genre;
    private List<String> cast;
    private String rating;

    public MovieResponse() {
    }

    public MovieResponse(String title, String year, String type, List<String> genre,
                          List<String> cast, String rating) {
        this.title = title;
        this.year = year;
        this.type = type;
        this.genre = genre;
        this.cast = cast;
        this.rating = rating;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getGenre() {
        return genre;
    }

    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    public List<String> getCast() {
        return cast;
    }

    public void setCast(List<String> cast) {
        this.cast = cast;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MovieResponse)) return false;
        MovieResponse that = (MovieResponse) o;
        return Objects.equals(title, that.title)
                && Objects.equals(year, that.year)
                && Objects.equals(type, that.type)
                && Objects.equals(genre, that.genre)
                && Objects.equals(cast, that.cast)
                && Objects.equals(rating, that.rating);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, year, type, genre, cast, rating);
    }

    @Override
    public String toString() {
        return "MovieResponse{" +
                "title='" + title + '\'' +
                ", year='" + year + '\'' +
                ", type='" + type + '\'' +
                ", genre=" + genre +
                ", cast=" + cast +
                ", rating='" + rating + '\'' +
                '}';
    }
}
