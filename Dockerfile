FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /usr/src/app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src src
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /opt/app
COPY --from=builder /usr/src/app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/opt/app/app.jar"]