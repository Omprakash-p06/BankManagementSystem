# Bank Management System - Lab Exam Project Setup Guide

This guide provides the complete setup, integration, deployment, and security auditing instructions for the Bank Management System.

---

## 🚀 1. Docker Setup & Execution

The project uses a **multi-stage Docker build**. This compiles the Spring Boot Java WAR application inside a builder container (`maven:3.8.5-openjdk-17`) and deploys it on a lightweight execution container (`openjdk:17-jdk-slim`), removing the need for local Java/Maven installations.

### Running Locally with Docker Compose

1. Start the database and application containers in the background:
   ```bash
   docker-compose up -d --build
   ```
2. The containers will:
   - Run a MySQL 8.0 instance on port `3306` with database `bank_management`.
   - Build and start the Spring Boot app on port `8080`.
3. Stop the environment:
   ```bash
   docker-compose down
   ```

### Pushing Docker Image to Registry

To push the built image to Docker Hub:
```bash
# Tag the image
docker tag bankmanagementsystem_app:latest <your-dockerhub-username>/bank-management-system:latest

# Log in
docker login

# Push the image
docker push <your-dockerhub-username>/bank-management-system:latest
```

---

## 🛠️ 2. Jenkins CI/CD Pipeline

The declarative [Jenkinsfile](file:///C:/Users/HP/Desktop/BankManagementSystem/Jenkinsfile) automates the entire software delivery lifecycle, integrating testing, scanning, packaging, containerization, and cloud deployment.

### Pipeline Stages
1. **Checkout**: Automatically pulls code from Git.
2. **Dependency Check**: Runs the OWASP Dependency-Check scanner to find vulnerabilities in Maven dependencies.
3. **SonarQube Scan**: Conducts code quality analysis and vulnerability scans.
4. **Build Artifact**: Compiles the Spring Boot project and creates a deployable `.war` file.
5. **Docker Build**: Builds the container image using [Dockerfile](file:///C:/Users/HP/Desktop/BankManagementSystem/Dockerfile).
6. **Docker Push**: Authenticates and pushes the image to Docker Hub.

### Required Jenkins Credentials
Configure these in Jenkins under *Manage Jenkins -> Credentials*:
- `docker-hub-credentials`: Username & Password for your Docker registry.
- `sonar-token`: Authentication token generated in your SonarQube account.

---

## ☁️ 3. Deployment & Netlify Integration

We separate the deployment into a highly scalable, serverless frontend and a robust, containerized backend:

```
                  ┌──────────────────────┐
                  │  Netlify Deployment  │ (Static Frontend HTML/CSS)
                  │   (Netlify Site)     │
                  └──────────┬───────────┘
                             │
                    HTTPS API Request
                             │
                             ▼
                  ┌──────────────────────┐
                  │  Production Server   │ (Containerized Java Backend)
                  │    (Docker Image)    │
                  └──────────┬───────────┘
                             │
                         JDBC Link
                             │
                             ▼
                  ┌──────────────────────┐
                  │   Database Server    │ (MySQL Instance)
                  └──────────────────────┘
```

### Backend: Container Deployment
1. Run the Docker container on your target server.
2. Ensure it is connected to your MySQL instance.
3. Configure the environment variables:
   - `SPRING_DATASOURCE_URL`: `jdbc:mysql://<your-database-host>:3306/bank_management`
   - `SPRING_DATASOURCE_USERNAME`: `<db-user>`
   - `SPRING_DATASOURCE_PASSWORD`: `<db-password>`

### Frontend: Netlify Deployment
The frontend is hosted on Netlify using the configuration defined in `netlify.toml`.

1. Create a new Netlify site from this repository.
2. Build command:
   ```bash
   node scripts/netlify-build.js
   ```
3. Publish directory:
   ```
   src/main/webapp
   ```
4. Add a Netlify environment variable for the backend URL:
   - `BASE_URL`: `https://<your-backend-domain>/BankManagementSystem`
5. Deploy the site.

Notes:
- `netlify.toml` includes clean URL redirects for the HTML pages.
- Ensure the backend CORS configuration allows your Netlify domain.

---

## 🔒 4. Dependency & Security Audits

The project includes built-in security auditing plugins.

### OWASP Dependency Check
Scans the project's third-party dependencies for known vulnerabilities (CVEs) and generates an HTML report.
- Run the scan via Maven:
  ```bash
  mvn org.owasp:dependency-check-maven:check
  ```
- View the generated report at: `target/dependency-check-report.html`.

### SonarQube Code Quality Scan
Performs static application security testing (SAST) to detect bugs, code smells, and security vulnerabilities.
- Run the scan using:
  ```bash
  mvn sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.login=<your-sonar-token>
  ```
- Access the results dashboard on your local or cloud SonarQube instance (`http://localhost:9000`).
