FROM gradle:8.1.1-jdk17-jammy AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:17.0.7_7-jre-alpine
# indicando el puerto para exponer, debo pasar el flag -p para habilitarlo o -P para publicarlos todos.
EXPOSE 8888

RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/servidor-configuracion.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/servidor-configuracion.jar"]
