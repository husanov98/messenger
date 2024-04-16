FROM openjdk:oraclelinux8
WORKDIR /hack
COPY target/*.jar hack.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","hack.jar"]

