package com.felps.imdbmovieapi.client;

import com.felps.imdbmovieapi.config.OmdbProperties;
import com.felps.imdbmovieapi.dto.omdb.OmdbMovieDto;
import com.felps.imdbmovieapi.exception.OmdbServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Camada responsavel exclusivamente por se comunicar com a OMDb API
 * (a API externa que fornece os dados do IMDB). Isola o resto da
 * aplicacao dos detalhes de protocolo HTTP e da URL do provedor externo.
 */
@Component
public class OmdbClient {

    private final RestTemplate restTemplate;
    private final OmdbProperties properties;

    public OmdbClient(RestTemplate restTemplate, OmdbProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    /**
     * Busca um filme pelo titulo na OMDb API.
     *
     * @param title titulo do filme (ja validado/nao vazio)
     * @return o DTO com a resposta bruta da OMDb (pode representar "nao encontrado";
     *         quem decide isso e a camada de servico, olhando {@link OmdbMovieDto#isSuccess()})
     * @throws OmdbServiceException se houver falha de rede, timeout, ou resposta invalida
     */
    public OmdbMovieDto fetchMovieByTitle(String title) {
        URI uri = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                .queryParam("t", title)
                .queryParam("apikey", properties.getApiKey())
                .build()
                .encode()
                .toUri();

        try {
            OmdbMovieDto dto = restTemplate.getForObject(uri, OmdbMovieDto.class);
            if (dto == null) {
                throw new OmdbServiceException("A OMDb API retornou uma resposta vazia.", null);
            }
            return dto;
        } catch (RestClientException ex) {
            throw new OmdbServiceException("Falha ao consultar a OMDb API: " + ex.getMessage(), ex);
        }
    }
}
