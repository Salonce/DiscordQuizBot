FROM maven@sha256:4c50122665c495f188f0072b41d19253c89c3d14d3c0c22a020e53a6ddd9fba7 AS builder
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn clean package -DskipTests

FROM eclipse-temurin@sha256:10ad0bcc8eef2729dac7fe5938ef615c7ac46eac9016163ca01986715df4fcd8
RUN apk update && apk add --no-cache curl
COPY --from=builder /app/target/discord_quiz.jar todolist.jar
ENTRYPOINT ["java", "-jar", "/discord_quiz.jar"]