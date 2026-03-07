# Fase de construcción con Java 17 Slim
FROM maven:3.8.5-eclipse-temurin-17-alpine AS build

WORKDIR /app
COPY . .
# Se usa DskipTests para hacer el build mas veloz
RUN mvn clean install -DskipTests

# Fase final con Java 17 Slim
FROM eclipse-temurin:17-jre-alpine
COPY --from=build /app/backOfficeWeb/target/backOffice-0.0.1-SNAPSHOT.jar /demo.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "-Dserver.port=8084", "-XX:ActiveProcessorCount=1", "-Xms256m", "-Xmx500m", "/demo.jar"]
