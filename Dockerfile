# Fase de construcción con instalación de Maven
FROM amazoncorretto:17-al2023 AS build

# Instalar Maven
RUN yum update -y && \
    yum install -y maven

RUN mvn clean install

# Fase final con Amazon Corretto 17
FROM amazoncorretto:17-al2023
COPY --from=build /backOfficeWeb/target/backOffice-0.0.1-SNAPSHOT.jar /demo.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/demo.jar"]
