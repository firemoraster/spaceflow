# ---- build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B clean package -DskipTests

# ---- runtime stage ----
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app
RUN groupadd -r spaceflow && useradd -r -g spaceflow spaceflow
COPY --from=build /app/target/spaceflow-*.jar app.jar
USER spaceflow
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
