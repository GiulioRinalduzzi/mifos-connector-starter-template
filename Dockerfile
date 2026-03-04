FROM gradle:8.12.1-jdk21 AS build

WORKDIR /workspace

COPY . .

RUN gradle clean bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

ARG JAR_FILE=build/libs/ph-ee-connector-starter-1.0.0-SNAPSHOT.jar
COPY --from=build /workspace/${JAR_FILE} app.jar

EXPOSE 8080
EXPOSE 5010

ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5010"

ENTRYPOINT ["sh", "-c", "java ${JAVA_TOOL_OPTIONS} -jar app.jar"]
