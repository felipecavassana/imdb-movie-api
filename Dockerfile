# syntax=docker/dockerfile:1

# ---------- Etapa 1: build ----------
# Imagem com Maven + JDK 8 apenas para compilar e empacotar a aplicacao.
FROM maven:3.9.3-eclipse-temurin-8-alpine AS build
WORKDIR /app

# Copia primeiro o pom.xml e baixa as dependencias: isso aproveita o cache
# de camadas do Docker, entao o download so roda de novo se o pom.xml mudar.
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Agora copia o codigo fonte e empacota o jar (sem rodar os testes aqui;
# os testes devem ser rodados no pipeline de CI / localmente com "mvn test").
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- Etapa 2: runtime ----------
# Imagem final, bem mais leve: contem apenas o JRE 8 e o jar gerado,
# sem Maven nem codigo fonte.
FROM eclipse-temurin:8-jre-alpine
WORKDIR /app

# Roda como usuario nao-root por seguranca.
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/imdb-movie-api.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
