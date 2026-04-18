# ─────────────────────────────────────────
# Stage 1: Build
# ─────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

# Copy parent POM first (program-service inherits from backend/pom.xml)
COPY pom.xml ./pom.xml
COPY program-service/pom.xml ./program-service/pom.xml

# Download dependencies in a cacheable layer before copying source
RUN mvn -f ./program-service/pom.xml dependency:go-offline -B --no-transfer-progress 2>/dev/null || true

# Copy source code
COPY program-service/src ./program-service/src

# Build the service, skipping tests
RUN mvn -f ./program-service/pom.xml clean package -DskipTests -B --no-transfer-progress

# ─────────────────────────────────────────
# Stage 2: Runtime
# ─────────────────────────────────────────
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the fat JAR produced by spring-boot-maven-plugin
COPY --from=build /workspace/program-service/target/program-service-*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8081} -jar app.jar"]
