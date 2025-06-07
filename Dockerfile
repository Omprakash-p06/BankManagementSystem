# PROJECT_ROOT/Dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/BankManagementSystem-*.war app.war
EXPOSE 8080
CMD ["java", "-jar", "app.war"]