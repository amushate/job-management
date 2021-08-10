FROM openjdk:8-jdk-alpine
MAINTAINER amushate
COPY target/job-management-0.0.1-SNAPSHOT.jar job-management-0.0.1.jar
RUN apk update && apk add bash
ENTRYPOINT ["java","-jar","/job-management-0.0.1.jar"]