FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app
COPY ../pom.xml ../pom.xml
COPY ../.. .
RUN mvn clean package -pl auth-service -am -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/auth-service/target/auth-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
