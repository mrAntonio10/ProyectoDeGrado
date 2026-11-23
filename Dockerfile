# =====================================================
# STAGE 1: Construcción
# =====================================================
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests


# =====================================================
# STAGE 2: Ejecución
# =====================================================
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

COPY --from=build /app/backOfficeWeb/target/backOffice-0.0.1-SNAPSHOT.jar /app/demo.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:ActiveProcessorCount=1", "-Xms256m", "-Xmx500m", "-jar", "/app/demo.jar"]
