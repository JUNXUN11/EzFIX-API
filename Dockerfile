# Use a lightweight Java runtime as the base image
FROM openjdk:17-jdk-slim AS build

# Set the working directory for the build
WORKDIR /app

# Copy the Maven wrapper and the pom.xml to resolve dependencies
COPY .mvn/ .mvn
COPY mvnw .
COPY pom.xml .

# Make the Maven wrapper executable
RUN chmod +x mvnw

# Download dependencies (this caches dependencies for faster incremental builds)
RUN ./mvnw dependency:go-offline

# Copy the source code and build the application without running tests
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Create a new stage with a lightweight Java runtime to run the app
FROM openjdk:17-jdk-slim

# Set the working directory in the final container
WORKDIR /app

# Copy the built JAR file from the previous stage
COPY --from=build /app/target/theezzfix-0.0.1.jar /app/app.jar

# Expose the default port used by Spring Boot (8080)
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "app.jar"]
