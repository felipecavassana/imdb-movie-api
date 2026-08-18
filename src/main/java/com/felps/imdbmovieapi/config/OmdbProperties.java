package com.felps.imdbmovieapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Mapeia as propriedades "omdb.*" definidas em application.properties
 * (prefixo "omdb") para um objeto Java tipado.
 *
 * <p>Exemplo em application.properties:</p>
 * <pre>
 * omdb.api-key=minha-chave
 * omdb.base-url=http://www.omdbapi.com/
 * </pre>
 */
@Component
@ConfigurationProperties(prefix = "omdb")
public class OmdbProperties {

    /** Chave de API gratuita obtida em https://www.omdbapi.com/apikey.aspx */
    private String apiKey;

    /** URL base da OMDb API. */
    private String baseUrl = "http://www.omdbapi.com/";

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
