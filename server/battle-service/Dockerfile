FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY /battle-service/target/battle-service-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]