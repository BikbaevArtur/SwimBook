FROM maven:3.9.9-eclipse-temurin-21 as builder
WORKDIR /opt/app
COPY mvnw pom.xml ./
COPY src ./src
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /opt/app
EXPOSE 8080
COPY --from=builder /opt/app/target/SwimBook-0.0.1-SNAPSHOT.jar /opt/app/SwimBook-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/opt/app/SwimBook-0.0.1-SNAPSHOT.jar"]