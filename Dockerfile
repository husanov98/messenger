FROM openjdk:oraclelinux8
VOLUME /tmp
WORKDIR /hack
COPY target/*.jar hack.jar
EXPOSE 8443
ENTRYPOINT ["java","-jar","hack.jar"]

