FROM alpine/java:21.0.2-jdk

WORKDIR /app

COPY target/fileStorage-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]


