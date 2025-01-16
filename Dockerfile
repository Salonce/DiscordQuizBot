FROM maven@sha256:cab0e1f1ede6c0f56118b59dff1bf3e12bafa51ed32677afd29f2818890d9fba AS maven
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn clean package -DskipTests


FROM eclipse-temurin@sha256:04ea31625d7771f3272bdc533a2871c00a8268f1a6774528b2a7389515f7b5b1 AS jre
RUN apk update && apk add --no-cache curl
COPY --from=maven /app/target/discordQuizBot.jar /app/discordQuizBot.jar
ENTRYPOINT ["java", "-jar", "/app/discordQuizBot.jar"]
