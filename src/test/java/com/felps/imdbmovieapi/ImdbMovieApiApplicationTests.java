package com.felps.imdbmovieapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Teste de "smoke test": garante que todo o contexto do Spring sobe
 * sem erros (todos os beans sao criados e as dependencias sao resolvidas).
 */
@SpringBootTest
class ImdbMovieApiApplicationTests {

    @Test
    void contextLoads() {
        // Se o contexto do Spring nao subir, este teste falha automaticamente.
    }
}
