# syntax=docker/dockerfile:1
# BuildKit cache: ~/.m2 shared across services — avoids re-downloading Central on every compose build.
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY config-server/pom.xml config-server/pom.xml
COPY discovery-server/pom.xml discovery-server/pom.xml
COPY gateway-service/pom.xml gateway-service/pom.xml
COPY auth-service/pom.xml auth-service/pom.xml
COPY delivery-service/pom.xml delivery-service/pom.xml
COPY tracking-service/pom.xml tracking-service/pom.xml
COPY admin-service/pom.xml admin-service/pom.xml
RUN --mount=type=cache,target=/root/.m2/repository,id=smart-courier-m2 \
    mvn -B -pl auth-service -am dependency:go-offline
COPY auth-service auth-service
RUN --mount=type=cache,target=/root/.m2/repository,id=smart-courier-m2 \
    mvn -B -pl auth-service -am clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/auth-service/target/auth-service-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
