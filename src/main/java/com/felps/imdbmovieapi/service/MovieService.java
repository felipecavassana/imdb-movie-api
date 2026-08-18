package com.felps.imdbmovieapi.service;

import com.felps.imdbmovieapi.dto.MovieResponse;

/**
 * Contrato da camada de servico responsavel pela regra de negocio
 * de busca de filmes.
 */
public interface MovieService {

    /**
     * Busca um filme pelo titulo.
     *
     * @param title titulo (ou parte do titulo) do filme a ser pesquisado
     * @return dados do filme (nome, ano, elenco e avaliacao)
     * @throws com.felps.imdbmovieapi.exception.InvalidMovieRequestException se o titulo for vazio/nulo
     * @throws com.felps.imdbmovieapi.exception.MovieNotFoundException se nenhum filme for encontrado
     * @throws com.felps.imdbmovieapi.exception.OmdbServiceException se a OMDb API falhar
     */
    MovieResponse getMovieByTitle(String title);
}
