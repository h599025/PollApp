FROM gradle:latest AS builder

COPY settings.gradle.kts gradlew build.gradle.kts ./

COPY src src
COPY gradle gradle

RUN ./gradlew bootJar

# Rename and move the resulting JAR file
RUN mv build/libs/demo-0.0.1-SNAPSHOT.jar /app.jar

# Runtime stage
FROM openjdk:21-jdk-slim

# Copy the built JAR from the builder stage
COPY --from=builder /app.jar /app.jar

EXPOSE 5173 8080

CMD ["java", "-jar", "app.jar"]
