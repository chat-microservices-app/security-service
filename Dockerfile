FROM eclipse-temurin:17-jdk-alpine as builder

# DOCKER DIR
WORKDIR /app

# Copying the jar file to the docker image
COPY target/*.jar app.jar

# extract layers of application jar
RUN java -Djarmode=layertools -jar app.jar extract

# Multi-Stage Build
FROM eclipse-temurin:17-jre-alpine

# Port used to connect with the application
ENV SERVER_PORT 8000
EXPOSE $SERVER_PORT

# Default timezone
ENV TZ Los_Angeles/America

# Default Spring profile
ENV SPRING_PROFILES_ACTIVE prod

VOLUME /tmp

ARG DEPENDENCY=/app

COPY --from=builder ${DEPENDENCY}/dependencies/ ./
COPY --from=builder ${DEPENDENCY}/spring-boot-loader/ ./
COPY --from=builder ${DEPENDENCY}/snapshot-dependencies/ ./
COPY --from=builder ${DEPENDENCY}/application/ ./

# Copy the Spring Boot fat JarLauncher into the image and use it to run the application
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]