# Fase de construcción con instalación de Maven
FROM amazoncorretto:17-al2023 AS build

# Instalar Maven
RUN yum update -y && \
    yum install -y maven

# Instalar la fuente "Zilla Slab"
RUN yum install -y fontconfig && \
    mkdir -p /usr/share/fonts/truetype/zilla-slab && \
    curl -o /usr/share/fonts/truetype/zilla-slab/ZillaSlab-Regular.ttf https://github.com/google/fonts/raw/main/ofl/zillaslab/ZillaSlab-Regular.ttf && \
    fc-cache -f -v

COPY . .
RUN mvn clean install

# Fase final con Amazon Corretto 17
FROM amazoncorretto:17-al2023
COPY --from=build /backOfficeWeb/target/backOffice-0.0.1-SNAPSHOT.jar /demo.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/demo.jar"]
