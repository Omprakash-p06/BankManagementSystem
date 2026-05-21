pipeline {
    agent any

    environment {
        // Docker Credentials and Image details
        DOCKER_HUB_CREDENTIALS_ID = 'docker-hub-credentials'
        DOCKER_IMAGE_NAME         = 'vishwajith1312/bank-management-system'
        DOCKER_TAG                = "${BUILD_NUMBER}"
        
        // Azure Credentials and Details
        AZURE_CREDENTIALS_ID      = 'azure-sp-credentials'
        AZURE_RESOURCE_GROUP      = 'BankManagementRG'
        AZURE_APP_SERVICE_NAME    = 'bank-management-app'
        
        // SonarQube Configuration
        SONAR_CREDENTIALS_ID      = 'sonar-token'
        SONAR_HOST_URL            = 'http://host.docker.internal:9000'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code from Git repository...'
                checkout scm
            }
        }

        stage('Dependency Check') {
            steps {
                echo 'Running OWASP Dependency-Check vulnerability scan on third-party dependencies...'
                // Run Maven in Docker container using a named volume (maven-repo) to cache downloaded files and NVD vulnerability databases
                bat 'docker run --rm -v maven-repo:/root/.m2 -v "%WORKSPACE%":/app -w /app maven:3.8.6-eclipse-temurin-17 mvn org.owasp:dependency-check-maven:check -Dformat=HTML'
            }
            post {
                always {
                    // Archive the generated HTML report
                    archiveArtifacts artifacts: '**/target/dependency-check-report.html', fingerprint: true
                }
            }
        }

        stage('SonarQube Vulnerability Scan') {
            steps {
                echo 'Running SonarQube vulnerability and code quality checks...'
                withCredentials([string(credentialsId: "${SONAR_CREDENTIALS_ID}", variable: 'SONAR_TOKEN')]) {
                    bat "docker run --rm -v maven-repo:/root/.m2 -v \"%WORKSPACE%\":/app -w /app maven:3.8.6-eclipse-temurin-17 mvn sonar:sonar \"-Dsonar.host.url=${SONAR_HOST_URL}\" \"-Dsonar.login=%SONAR_TOKEN%\""
                }
            }
        }

        stage('Build Artifact') {
            steps {
                echo 'Compiling project and packaging WAR artifact...'
                bat 'docker run --rm -v maven-repo:/root/.m2 -v "%WORKSPACE%":/app -w /app maven:3.8.6-eclipse-temurin-17 mvn clean package -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                echo "Building Docker container image: ${DOCKER_IMAGE_NAME}:${DOCKER_TAG}..."
                bat "docker build -t ${DOCKER_IMAGE_NAME}:${DOCKER_TAG} -t ${DOCKER_IMAGE_NAME}:latest -f Dockerfile ."
            }
        }

        stage('Docker Push') {
            steps {
                echo 'Pushing Docker image to Docker Registry...'
                withCredentials([usernamePassword(credentialsId: "${DOCKER_HUB_CREDENTIALS_ID}", usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    bat "docker login -u %DOCKER_USER% -p %DOCKER_PASS%"
                    bat "docker push ${DOCKER_IMAGE_NAME}:${DOCKER_TAG}"
                    bat "docker push ${DOCKER_IMAGE_NAME}:latest"
                }
            }
        }

        stage('Deploy to Azure') {
            steps {
                echo 'Deploying Dockerized container to Azure App Service...'
                withCredentials([azureServicePrincipal(credentialsId: "${AZURE_CREDENTIALS_ID}")]) {
                    // Login to Azure using Service Principal credentials
                    bat "az login --service-principal -u %AZURE_CLIENT_ID% -p %AZURE_CLIENT_SECRET% --tenant %AZURE_TENANT_ID%"
                    
                    // Update Azure Web App to use the new Docker image
                    bat "az webapp config container set --name ${AZURE_APP_SERVICE_NAME} --resource-group ${AZURE_RESOURCE_GROUP} --docker-custom-image-name ${DOCKER_IMAGE_NAME}:${DOCKER_TAG}"
                }
            }
        }
    }

    post {
        success {
            echo "CI/CD Pipeline succeeded for build #${BUILD_NUMBER}!"
        }
        failure {
            echo "CI/CD Pipeline failed on build #${BUILD_NUMBER}. Check logs for details."
        }
    }
}
