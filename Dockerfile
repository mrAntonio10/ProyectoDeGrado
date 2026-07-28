# # Fase de construcción con instalación de Maven
# FROM amazoncorretto:21-al2023 AS build

# # Instalar Maven
# RUN yum update -y && \
#     yum install -y maven

# COPY . .
# RUN mvn clean install

# # Fase final con Amazon Corretto 21
# FROM amazoncorretto:21-al2023
# COPY --from=build /backOfficeWeb/target/backOffice-0.0.1-SNAPSHOT.jar /demo.jar
# EXPOSE 8080
# ENTRYPOINT ["java", "-jar", "-XX:ActiveProcessorCount=1", "-Xms256m", "-Xmx500m", "/demo.jar"]

# =====================================================
# STAGE 1: Construcción
# Aquí sí necesitamos Maven y JDK
# =====================================================
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests


# =====================================================
# STAGE 2: Ejecución
# Aquí solamente necesitamos Java para ejecutar el JAR
# =====================================================
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

COPY --from=build \
    /app/target/backOffice-0.0.1-SNAPSHOT.jar \
    /app/demo.jar

EXPOSE 8080

ENTRYPOINT [
    "java",
    "-XX:ActiveProcessorCount=1",
    "-Xms256m",
    "-Xmx500m",
    "-jar",
    "/app/demo.jar"
]