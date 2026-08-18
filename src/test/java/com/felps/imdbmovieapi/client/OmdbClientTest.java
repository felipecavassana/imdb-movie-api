package com.felps.imdbmovieapi.client;

import com.felps.imdbmovieapi.config.OmdbProperties;
import com.felps.imdbmovieapi.dto.omdb.OmdbMovieDto;
import com.felps.imdbmovieapi.exception.OmdbServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.GET;

/**
 * Testa {@link OmdbClient} isoladamente, usando {@link MockRestServiceServer}
 * para simular respostas da OMDb API sem realizar nenhuma chamada de rede real.
 */
class OmdbClientTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private OmdbClient omdbClient;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();

        OmdbProperties properties = new OmdbProperties();
        properties.setApiKey("test-key");
        properties.setBaseUrl("http://www.omdbapi.com/");

        omdbClient = new OmdbClient(restTemplate, properties);
    }

    @Test
    void fetchMovieByTitle_shouldReturnDto_whenOmdbRespondsSuccessfully() {
        String json = "{\"Title\":\"Inception\",\"Year\":\"2010\","
                + "\"Actors\":\"Leonardo DiCaprio, Joseph Gordon-Levitt\","
                + "\"Genre\":\"Action, Sci-Fi, Thriller\",\"Type\":\"movie\","
                + "\"imdbRating\":\"8.8\",\"Response\":\"True\"}";

        mockServer.expect(requestTo("http://www.omdbapi.com/?t=Inception&apikey=test-key"))
                .andExpect(method(GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        OmdbMovieDto dto = omdbClient.fetchMovieByTitle("Inception");

        assertThat(dto.isSuccess()).isTrue();
        assertThat(dto.getTitle()).isEqualTo("Inception");
        assertThat(dto.getImdbRating()).isEqualTo("8.8");
        assertThat(dto.getGenre()).isEqualTo("Action, Sci-Fi, Thriller");
        assertThat(dto.getType()).isEqualTo("movie");
        mockServer.verify();
    }

    @Test
    void fetchMovieByTitle_shouldReturnDto_whenOmdbRespondsMovieNotFound() {
        String json = "{\"Response\":\"False\",\"Error\":\"Movie not found!\"}";

        mockServer.expect(requestTo("http://www.omdbapi.com/?t=xyz123&apikey=test-key"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        OmdbMovieDto dto = omdbClient.fetchMovieByTitle("xyz123");

        assertThat(dto.isSuccess()).isFalse();
        assertThat(dto.getError()).isEqualTo("Movie not found!");
    }

    @Test
    void fetchMovieByTitle_shouldThrowOmdbServiceException_whenOmdbIsUnavailable() {
        mockServer.expect(requestTo("http://www.omdbapi.com/?t=Inception&apikey=test-key"))
                .andRespond(withServerError());

        assertThrows(OmdbServiceException.class, () -> omdbClient.fetchMovieByTitle("Inception"));
    }

    @Test
    void fetchMovieByTitle_shouldEncodeTitleWithSpecialCharacters() {
        String json = "{\"Title\":\"Se7en\",\"Response\":\"True\"}";

        mockServer.expect(requestTo("http://www.omdbapi.com/?t=Se%267en&apikey=test-key"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        OmdbMovieDto dto = omdbClient.fetchMovieByTitle("Se&7en");

        assertThat(dto.getTitle()).isEqualTo("Se7en");
    }
}
