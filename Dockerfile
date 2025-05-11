# Use Eclipse Temurin JDK 21 as the base image for running Java applications
FROM eclipse-temurin:21-jdk
# Copy the compiled JAR file from the target directory to the container and rename it to app.jar
COPY target/*.jar app.jar
# Expose port 8080
EXPOSE 8080
# Define the command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "/app.jar"]