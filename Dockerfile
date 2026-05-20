# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /app

# Copy pom.xml and download dependencies to utilize Docker layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and compile the WAR package
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the compiled war file from the builder stage
COPY --from=builder /app/target/BankManagementSystem-*.war app.war

EXPOSE 8080
CMD ["java", "-jar", "app.war"]