FROM gradle:8.12.1-jdk21 AS build

WORKDIR /workspace

COPY . .

RUN gradle clean bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /workspace/build/libs/*.jar app.jar

EXPOSE 8080

# JDWP remote debug is disabled by default.
# To enable locally: docker run -e JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5010" -p 5010:5010 ...
ENV JAVA_TOOL_OPTIONS=""

ENTRYPOINT ["java", "-jar", "app.jar"]
