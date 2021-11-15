FROM openjdk:11
VOLUME /tmp
ARG TAG=1.2.0
ARG JAR_FILE=target/aws_course_p2-${TAG}.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]