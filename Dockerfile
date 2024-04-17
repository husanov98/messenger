FROM openjdk:oraclelinux8
VOLUME /tmp
WORKDIR /app
COPY target/*.jar hack.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","hack.jar"]

