# Estagio 1: compila o jar com Maven.
# Copio o pom antes do src pra aproveitar o cache do Docker: enquanto as
# dependencias nao mudam, o build nao baixa tudo de novo.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# Estagio 2: imagem final, so o JRE e o jar.
# Nao leva Maven nem codigo-fonte, o que deixa a imagem bem menor.
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/sales-monolith-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# JAVA_OPTS fica como variavel pra eu ajustar heap e coletor de lixo nos
# ensaios de carga sem reconstruir a imagem. O exec garante que o java seja
# o processo 1 do container e receba os sinais de parada.
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
