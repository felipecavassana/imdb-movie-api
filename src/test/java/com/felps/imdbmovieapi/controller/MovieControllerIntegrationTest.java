package com.felps.imdbmovieapi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste de integracao ponta a ponta: sobe o contexto completo do Spring
 * (Controller -> Service -> Client) e usa MockMvc para simular uma
 * requisicao HTTP real. A unica coisa mockada e a chamada de rede para a
 * OMDb API (via {@link MockRestServiceServer}), garantindo que nenhum
 * teste dependa de acesso a internet.
 *
 * <p>{@code @DirtiesContext} garante que o contexto (e o RestTemplate/
 * MockRestServiceServer associado) seja recriado a cada teste, evitando
 * que expectativas de um teste vazem para o proximo.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MovieControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    void getMovie_shouldReturnMovieData_endToEnd() throws Exception {
        String omdbJson = "{\"Title\":\"The Matrix\",\"Year\":\"1999\","
                + "\"Actors\":\"Keanu Reeves, Laurence Fishburne, Carrie-Anne Moss\","
                + "\"Genre\":\"Action, Sci-Fi\",\"Type\":\"movie\","
                + "\"imdbRating\":\"8.7\",\"Response\":\"True\"}";

        mockServer.expect(requestTo("http://www.omdbapi.com/?t=The%20Matrix&apikey=test-api-key"))
                .andRespond(withSuccess(omdbJson, MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/movies").param("title", "The Matrix"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The Matrix"))
                .andExpect(jsonPath("$.year").value("1999"))
                .andExpect(jsonPath("$.type").value("movie"))
                .andExpect(jsonPath("$.genre[0]").value("Action"))
                .andExpect(jsonPath("$.rating").value("8.7"))
                .andExpect(jsonPath("$.cast[2]").value("Carrie-Anne Moss"));

        mockServer.verify();
    }

    @Test
    void getMovie_shouldReturnSeriesData_endToEnd() throws Exception {
        // A OMDb API ja retorna series pelo mesmo parametro "t" usado para
        // filmes: o campo "Type" e o que diferencia o resultado. Este teste
        // documenta que o endpoint atual ja suporta series, bastando ao
        // cliente da API observar o campo "type" na resposta.
        String omdbJson = "{\"Title\":\"Breaking Bad\",\"Year\":\"2008-2013\","
                + "\"Actors\":\"Bryan Cranston, Aaron Paul, Anna Gunn\","
                + "\"Genre\":\"Crime, Drama, Thriller\",\"Type\":\"series\","
                + "\"imdbRating\":\"9.5\",\"Response\":\"True\"}";

        mockServer.expect(requestTo("http://www.omdbapi.com/?t=Breaking%20Bad&apikey=test-api-key"))
                .andRespond(withSuccess(omdbJson, MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/movies").param("title", "Breaking Bad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Breaking Bad"))
                .andExpect(jsonPath("$.type").value("series"))
                .andExpect(jsonPath("$.genre[1]").value("Drama"));
    }

    @Test
    void getMovie_shouldReturn404_endToEnd_whenOmdbDoesNotFindMovie() throws Exception {
        String omdbJson = "{\"Response\":\"False\",\"Error\":\"Movie not found!\"}";

        mockServer.expect(requestTo("http://www.omdbapi.com/?t=asdkfjhaslkdjf&apikey=test-api-key"))
                .andRespond(withSuccess(omdbJson, MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/movies").param("title", "asdkfjhaslkdjf"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
