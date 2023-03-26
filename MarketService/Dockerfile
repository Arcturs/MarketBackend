FROM maven:3.8.3-openjdk-17-slim as BUILDER
ARG VERSION=0.0.1-SNAPSHOT
WORKDIR /opt/build/market
COPY pom.xml /opt/build/market/
COPY src /opt/build/market/src/
RUN mvn -f /opt/build/market/pom.xml clean package -B -DskipTests


FROM openjdk:17-alpine
WORKDIR /opt/app/market
COPY --from=BUILDER /opt/build/market/target/*.jar /opt/app/market/market.jar

RUN apk --no-cache add curl

ENV SERVER_PORT=8080

EXPOSE ${SERVER_PORT}

ENTRYPOINT ["java","-jar", "/opt/app/market/market.jar"]