FROM openjdk:17-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY flashcardApp-0.0.1-SNAPSHOT app.jar
ENTRYPOINT ["java","-jar","/app.jar"]