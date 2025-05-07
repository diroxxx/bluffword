# Użyj oficjalnego obrazu z JDK
FROM eclipse-temurin:20-jdk-alpine

# Ustaw katalog roboczy
WORKDIR /app

# Skopiuj jar z folderu target
COPY target/bluffword-backend-*.jar app.jar

# Otwórz port aplikacji
EXPOSE 8080

# Uruchom aplikację
ENTRYPOINT ["java", "-jar", "app.jar"]
