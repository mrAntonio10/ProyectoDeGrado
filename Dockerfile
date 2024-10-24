# Fase de construcción con instalación de Maven
FROM amazoncorretto:17-al2023 AS build

# Instalar Maven
RUN yum update -y && \
    yum install -y maven

# Copiar el proyecto y construir
COPY . .
RUN mvn clean install

# Fase final con Amazon Corretto 17
FROM amazoncorretto:17-al2023
COPY --from=build /target/*.jar /demo.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "demo.jar"]


