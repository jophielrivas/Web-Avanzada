FROM gradle:7.2.0-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle bootJar --no-daemon

FROM openjdk:11.0.12-jre-slim-buster
EXPOSE 8181

RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/WebApp.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/WebApp.jar"]