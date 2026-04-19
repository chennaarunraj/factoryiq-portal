# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy parent POM and service POM
COPY backend/pom.xml ./backend/pom.xml
COPY backend/program-service/pom.xml ./backend/program-service/pom.xml

# Copy source code
COPY backend/program-service/src ./backend/program-service/src

# Build the service
RUN mvn -f ./backend/program-service/pom.xml clean package -DskipTests -B --no-transfer-progress

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /workspace/backend/program-service/target/program-service-*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8081} -jar app.jar"]
