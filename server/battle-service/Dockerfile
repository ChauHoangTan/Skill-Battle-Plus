FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app
COPY ../pom.xml ../pom.xml
COPY ../.. .
RUN mvn clean package -pl battle-service -am -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/battle-service/target/battle-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
