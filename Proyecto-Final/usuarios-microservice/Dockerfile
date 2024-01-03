FROM gradle:7.2.0-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle bootJar --no-daemon

FROM openjdk:11.0.12-jre-slim-buster

ENV spring.datasource.username=root
ENV spring.datasource.password=12345678
ENV DB_NAME=User-db
ENV DB_HOST=user-db

RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/user-microservice.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/user-microservice.jar"]