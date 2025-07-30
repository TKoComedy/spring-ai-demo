FROM openjdk:8-jre-alpine

WORKDIR /app

COPY target/spring-ai-demo-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"] 