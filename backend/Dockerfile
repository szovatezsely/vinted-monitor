# syntax=docker/dockerfile:1.7

# ---- Build stage ----------------------------------------------------------
FROM gradle:8.10-jdk17 AS builder
WORKDIR /home/gradle/src

# Cache dependencies first
COPY --chown=gradle:gradle settings.gradle.kts build.gradle.kts gradle.properties ./
RUN gradle --no-daemon dependencies || true

# Now build with sources
COPY --chown=gradle:gradle src ./src
RUN gradle --no-daemon bootJar

# ---- Runtime stage --------------------------------------------------------
FROM eclipse-temurin:17-jre

WORKDIR /app
RUN mkdir -p /app/data && \
    useradd -r -u 1001 -g root vinted && \
    chown -R vinted:root /app

COPY --from=builder --chown=vinted:root /home/gradle/src/build/libs/*.jar /app/app.jar

USER vinted
EXPOSE 8080
VOLUME ["/app/data"]

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
