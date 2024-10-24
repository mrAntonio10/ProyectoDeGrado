# Fase de construcción con instalación de Maven
FROM amazoncorretto:17-al2023 AS build
WORKDIR /app

# Instalar Maven
RUN yum update -y && \
    yum install -y maven

# Copiar el proyecto y construir
COPY . .
RUN mvn clean install

# Fase final con Amazon Corretto 17
FROM amazoncorretto:17-al2023
COPY --from=build /app/target/*.jar /app/demo.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/demo.jar"]

