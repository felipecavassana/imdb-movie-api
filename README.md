# imdb-movie-api

API REST em **Java 8 + Spring Boot + Maven** que consulta informações de
filmes (nome, ano, elenco e avaliação) a partir de um título passado por
`query param`, usando a **[OMDb API](https://www.omdbapi.com/)** — um serviço
gratuito que expõe os dados do IMDB via HTTP/JSON.

> **Por que OMDb API e não a "API do IMDB"?** O IMDB (site oficial) não
> disponibiliza uma API pública e gratuita. A OMDb API é o serviço mais usado
> pela comunidade para consultar dados do IMDB de forma simples, legal e
> gratuita (com limite diário na versão free).

Este projeto foi criado como material de estudo: arquitetura em camadas
(MVC), tratamento de exceções centralizado, testes automatizados em todas as
camadas e execução via Docker.

---

## Índice

1. [Arquitetura do projeto](#arquitetura-do-projeto)
2. [Pré-requisitos](#pré-requisitos)
3. [Obtendo a API key da OMDb](#obtendo-a-api-key-da-omdb)
4. [Como abrir e rodar no IntelliJ IDEA](#como-abrir-e-rodar-no-intellij-idea)
5. [Endpoint da API](#endpoint-da-api)
6. [Tratamento de erros](#tratamento-de-erros)
7. [Rodando os testes](#rodando-os-testes)
8. [Executando com Docker](#executando-com-docker)
9. [Estrutura de pastas](#estrutura-de-pastas)
10. [Próximos passos sugeridos](#próximos-passos-sugeridos)

---

## Arquitetura do projeto

O projeto segue o padrão **MVC (Model-View-Controller)**, adaptado para uma
API REST (sem "View" visual, o "Model" é o JSON de resposta):

```
Requisição HTTP
      │
      ▼
┌─────────────────┐     ┌──────────────┐     ┌───────────────┐     ┌─────────────┐
│  MovieController │ --> │ MovieService │ --> │  OmdbClient    │ --> │  OMDb API    │
│  (Controller)     │     │ (regra de    │     │  (integração   │     │ (externa)    │
│                   │     │  negócio)    │     │  HTTP externa) │     │              │
└─────────────────┘     └──────────────┘     └───────────────┘     └─────────────┘
      │
      ▼
GlobalExceptionHandler (trata qualquer exceção lançada nas camadas acima
e converte em uma resposta JSON de erro padronizada)
```

- **`controller`** — recebe a requisição HTTP, delega para o service e
  devolve a resposta. Não tem lógica de negócio.
- **`service`** — valida a entrada, chama o client externo e transforma a
  resposta bruta da OMDb no formato de saída da nossa API.
- **`client`** — a única camada que conhece a URL e o formato da OMDb API.
  Se um dia trocarmos de provedor de dados, só essa camada muda.
- **`dto`** — objetos de transferência de dados: um para o que a OMDb
  devolve (`OmdbMovieDto`) e outro para o que a nossa API devolve
  (`MovieResponse`). Eles são propositalmente diferentes, para não expor ao
  cliente da nossa API detalhes internos do provedor externo.
- **`exception` / `GlobalExceptionHandler`** — centraliza o tratamento de
  erros com `@RestControllerAdvice`, garantindo respostas de erro
  consistentes em toda a aplicação.
- **`config`** — configuração de infraestrutura (o bean `RestTemplate` e o
  binding das propriedades `omdb.*`).

---

## Pré-requisitos

- **JDK 8** instalado ([Adoptium Temurin 8](https://adoptium.net/temurin/releases/?version=8) é uma boa opção)
- **Maven 3.6+** (o IntelliJ já vem com um Maven embutido, não precisa instalar)
- **IntelliJ IDEA** (Community ou Ultimate)
- Uma **API key gratuita da OMDb API** (veja abaixo)
- **Docker** e **Docker Compose** (opcional, só se for rodar em container)

---

## Obtendo a API key da OMDb

1. Acesse **https://www.omdbapi.com/apikey.aspx**
2. Escolha o plano **FREE** (1.000 requisições/dia) e informe seu e-mail.
3. Você receberá um e-mail com um link de ativação — clique nele.
4. A chave (uma sequência de caracteres, ex: `a1b2c3d4`) é exibida após a
   ativação. Guarde-a — ela será usada na variável de ambiente `OMDB_API_KEY`.

**Nunca coloque a chave diretamente no código ou no `application.properties`
commitado.** Este projeto já está preparado para ler a chave de uma variável
de ambiente (`OMDB_API_KEY`), então o valor real fica só na sua máquina.

---

## Como abrir e rodar no IntelliJ IDEA

1. **Abrir o projeto**
   `File > Open...` e selecione a pasta `imdb-movie-api` (a que contém o
   `pom.xml`). O IntelliJ vai reconhecer automaticamente como projeto Maven
   e baixar as dependências (acompanhe a barra de progresso no canto
   inferior direito).

2. **Configurar o JDK do projeto**
   `File > Project Structure > Project` → em "SDK", selecione o JDK 8
   (adicione via "Add SDK" se ainda não estiver instalado).

3. **Configurar a variável de ambiente com a API key**
   Antes de rodar, configure `OMDB_API_KEY`:
   - Vá em `Run > Edit Configurations...`
   - Selecione (ou crie) a configuração `ImdbMovieApiApplication`
   - No campo **Environment variables**, adicione:
     ```
     OMDB_API_KEY=sua-chave-aqui
     ```

4. **Rodar a aplicação**
   Abra `src/main/java/com/felps/imdbmovieapi/ImdbMovieApiApplication.java`
   e clique no ícone de "play" verde ao lado do método `main`, ou use o
   atalho `Shift + F10`.

5. **Testar**
   Com a aplicação rodando (log mostrará `Tomcat started on port(s): 8080`),
   abra no navegador ou use `curl`:
   ```bash
   curl "http://localhost:8080/api/movies?title=Inception"
   ```

---

## Endpoint da API

### `GET /api/movies`

Busca um filme pelo título.

**Query params**

| Parâmetro | Obrigatório | Descrição                          |
|-----------|:------------:|-------------------------------------|
| `title`   | sim          | Título (ou parte do título) do filme |

**Exemplo de requisição**

```bash
curl "http://localhost:8080/api/movies?title=Inception"
```

**Exemplo de resposta (200 OK)**

```json
{
  "title": "Inception",
  "year": "2010",
  "type": "movie",
  "genre": [
    "Action",
    "Adventure",
    "Sci-Fi"
  ],
  "cast": [
    "Leonardo DiCaprio",
    "Joseph Gordon-Levitt",
    "Elliot Page",
    "Ken Watanabe"
  ],
  "rating": "8.8"
}
```

> **Nota sobre `type`:** a OMDb API já retorna séries pelo mesmo parâmetro
> `t=` usado para filmes — o que muda é justamente o valor de `type`
> (`"movie"`, `"series"` ou `"episode"`). Por isso este endpoint **já
> funciona para buscar séries pelo título**, bastando o cliente da API
> observar esse campo na resposta (veja o teste
> `getMovie_shouldReturnSeriesData_endToEnd` em
> `MovieControllerIntegrationTest`, que usa "Breaking Bad" como exemplo).

---

## Tratamento de erros

Todos os erros da API seguem o mesmo formato de resposta JSON:

```json
{
  "timestamp": "2026-08-18T10:15:30",
  "status": 404,
  "error": "Not Found",
  "message": "Nenhum filme encontrado para o titulo: 'asdkfjhaslkdjf'",
  "path": "/api/movies"
}
```

| Cenário                                             | Status HTTP | Exceção lançada                  |
|------------------------------------------------------|:-----------:|-----------------------------------|
| Filme não encontrado na OMDb                          | 404         | `MovieNotFoundException`          |
| Parâmetro `title` vazio/em branco                     | 400         | `InvalidMovieRequestException`    |
| Parâmetro `title` ausente na URL                      | 400         | `MissingServletRequestParameterException` |
| Falha ao chamar a OMDb API (timeout, indisponível...) | 502         | `OmdbServiceException`            |
| Qualquer outro erro inesperado                        | 500         | fallback genérico                 |

---

## Rodando os testes

O projeto tem testes em todas as camadas:

| Camada                      | Classe de teste                          | Tipo                       |
|------------------------------|-------------------------------------------|-----------------------------|
| Aplicação (contexto Spring)  | `ImdbMovieApiApplicationTests`             | smoke test                  |
| Service (regra de negócio)   | `MovieServiceImplTest`                     | unitário (Mockito)          |
| Client (integração externa)  | `OmdbClientTest`                           | unitário (`MockRestServiceServer`) |
| Controller (camada web)      | `MovieControllerTest`                      | fatia web (`@WebMvcTest`)   |
| Ponta a ponta                | `MovieControllerIntegrationTest`           | integração (`@SpringBootTest` + MockMvc) |
| Tratamento de exceções       | `GlobalExceptionHandlerTest`               | unitário                    |

**Nenhum teste faz chamadas reais à internet** — todas as respostas da OMDb
API são simuladas (`Mockito` ou `MockRestServiceServer`), então os testes
rodam de forma rápida e determinística, sem precisar de uma API key válida.

Rodar todos os testes:

```bash
mvn test
```

Ou pelo IntelliJ: clique com o botão direito na pasta `src/test/java` →
`Run 'All Tests'`.

Um relatório de cobertura (JaCoCo) é gerado automaticamente em
`target/site/jacoco/index.html` após rodar `mvn test`.

---

## Executando com Docker

### Opção 1: Docker Compose (recomendado)

1. Crie o arquivo `.env` com sua chave:
   ```bash
   cp .env.example .env
   # edite o .env e coloque sua OMDB_API_KEY
   ```

2. Suba a aplicação:
   ```bash
   docker compose up --build
   ```

3. A API estará disponível em `http://localhost:8080`.

4. Para parar:
   ```bash
   docker compose down
   ```

### Opção 2: Docker "puro"

```bash
# build da imagem
docker build -t imdb-movie-api .

# rodar o container
docker run -p 8080:8080 -e OMDB_API_KEY=sua-chave-aqui imdb-movie-api
```

O `Dockerfile` usa **multi-stage build**: uma etapa com Maven+JDK8 compila e
empacota o `.jar`, e a imagem final usa apenas um JRE 8 enxuto (Alpine),
rodando como usuário não-root — sem Maven, código-fonte ou ferramentas de
build na imagem final.

---

## Estrutura de pastas

```
imdb-movie-api/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── .env.example
├── README.md
└── src/
    ├── main/
    │   ├── java/com/felps/imdbmovieapi/
    │   │   ├── ImdbMovieApiApplication.java
    │   │   ├── controller/
    │   │   │   └── MovieController.java
    │   │   ├── service/
    │   │   │   ├── MovieService.java
    │   │   │   └── impl/MovieServiceImpl.java
    │   │   ├── client/
    │   │   │   └── OmdbClient.java
    │   │   ├── dto/
    │   │   │   ├── MovieResponse.java
    │   │   │   └── omdb/OmdbMovieDto.java
    │   │   ├── config/
    │   │   │   ├── OmdbProperties.java
    │   │   │   └── RestTemplateConfig.java
    │   │   └── exception/
    │   │       ├── ApiError.java
    │   │       ├── GlobalExceptionHandler.java
    │   │       ├── InvalidMovieRequestException.java
    │   │       ├── MovieNotFoundException.java
    │   │       └── OmdbServiceException.java
    │   └── resources/
    │       └── application.properties
    └── test/
        ├── java/com/felps/imdbmovieapi/
        │   ├── ImdbMovieApiApplicationTests.java
        │   ├── controller/
        │   │   ├── MovieControllerTest.java
        │   │   └── MovieControllerIntegrationTest.java
        │   ├── service/MovieServiceImplTest.java
        │   ├── client/OmdbClientTest.java
        │   └── exception/GlobalExceptionHandlerTest.java
        └── resources/
            └── application.properties
```

---

## Próximos passos sugeridos

Ideias para você continuar estudando a partir deste projeto:

- Adicionar **cache** (`@Cacheable`) para não bater na OMDb API repetidamente
  pelo mesmo título.
- Adicionar **paginação/busca por múltiplos filmes** (a OMDb API tem um
  parâmetro `s=` para busca por lista).
- Adicionar **Swagger/OpenAPI** (`springdoc-openapi`) para documentação
  interativa do endpoint.
- Adicionar **rate limiting** para proteger sua própria API do consumo
  excessivo da cota gratuita da OMDb.
- Migrar para **Spring Boot 3 + Java 17/21** quando quiser praticar a
  versão mais atual do framework (o processo de migração em si é um ótimo
  exercício).

---

## Sobre a chave da OMDb API em produção

Se este projeto for além de estudo, nunca deixe a chave em texto plano em
repositórios públicos. Use um serviço de *secrets* (variáveis de ambiente do
provedor de nuvem, Docker secrets, Vault, etc.) para injetar `OMDB_API_KEY`
em tempo de execução, exatamente como já está preparado aqui.

---

Este projeto foi criado utilizando Claude.
