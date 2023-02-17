FROM openjdk:latest
ADD target/*.jar EIS-MOCK.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/EIS-MOCK.jar"]