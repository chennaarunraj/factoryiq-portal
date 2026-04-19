# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy parent POM
COPY backend/pom.xml ./backend/pom.xml

# Copy ALL backend source (parent needs all modules referenced)
COPY backend ./backend

# Build only program-service module (with dependencies)
WORKDIR /workspace/backend
RUN mvn -pl program-service -am clean package -DskipTests -B --no-transfer-progress

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /workspace/backend/program-service/target/program-service-*.jar app.jar

EXPOSE 8081
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8081} -jar app.jar"]
