
FROM openjdk:17-jdk-slim AS build


WORKDIR /app


COPY .mvn/ .mvn
COPY pom.xml .


RUN ./mvnw dependency:go-offline


COPY src ./src
RUN ./mvnw clean package -DskipTests


FROM openjdk:17-jdk-slim

WORKDIR /app


COPY --from=build /app/target/theezzfix-0.0.1.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
