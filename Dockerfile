FROM openjdk:13-jdk-alpine as build

ENV APP_HOME=/app

WORKDIR $APP_HOME
COPY . $APP_HOME

# Only build the project, without executing tests
RUN ./gradlew build -x test

FROM openjdk:13-jdk-alpine

ENV ARTIFACT_NAME=kotlin-playground.jar
ENV APP_HOME=/app

WORKDIR $APP_HOME

# Copy built jar
ARG JAR_FILE=$APP_HOME/build/libs/*.jar
COPY --from=build ${JAR_FILE} $ARTIFACT_NAME

EXPOSE 8080

ENTRYPOINT java -jar $ARTIFACT_NAME
