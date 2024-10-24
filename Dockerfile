FROM maven:3.6.3-jdk-17 AS build
COPY upb/upb .

RUN mvn clean install
RUN mvn clean package

# Etapa final
FROM amazoncorretto:17-alpine
COPY --from=build /target/upb-0.0.1-SNAPSHOT.jar /demo.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "/demo.jar"]

##https://www.youtube.com/watch?v=9MR6VMZ9MBo&t=905s
