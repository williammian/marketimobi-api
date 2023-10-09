FROM openjdk:8-jdk-alpine
COPY target/marketimobi-api-*.jar app.jar
RUN apk --update add fontconfig ttf-dejavu
ENTRYPOINT ["java", "-jar", "/app.jar"]