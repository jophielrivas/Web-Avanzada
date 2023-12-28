FROM gradle:7.4.0-jdk17 as builder
WORKDIR /app
COPY . ./
RUN gradle bootJar


FROM openjdk:17-jdk-slim
ENV APPLICATION_PORT=8080
ENV LOG_DIR=/logs
VOLUME $LOG_DIR
ARG JAR_FILE=/app/build/libs/*.jar
EXPOSE $APPLICATION_PORT
COPY --from=builder $JAR_FILE app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]