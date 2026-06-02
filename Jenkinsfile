pipeline {
    agent any

    environment {
        // Docker Credentials and Image details
        DOCKER_HUB_CREDENTIALS_ID = 'dockerhub-omprakash006'
        DOCKER_IMAGE_NAME         = 'Omprakash006/bank-management-system'
        DOCKER_TAG                = "${BUILD_NUMBER}"


        // SonarQube Configuration
        SONAR_CREDENTIALS_ID      = 'sonar-token-Omprakash006'
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
                // Run Maven in Docker container using named volumes to cache Maven dependencies and OWASP database
                // OWASP Dependency-Check 12.x uses NVD API 2.0. An NVD API key (optional) speeds up downloads:
                // Add -DnvdApiKey=... and Jenkins credential 'nvd-api-key' for faster updates.
                // Without a key, downloads are rate-limited but still work.
                bat 'docker run --rm -v maven-repo:/root/.m2 -v dependency-check-data:/root/.dependency-check -v "%WORKSPACE%":/app -w /app maven:3.8.6-eclipse-temurin-17 mvn org.owasp:dependency-check-maven:check -Dformat=HTML'
            }
            post {
                always {
                    // Archive the generated HTML report (allowEmptyArchive prevents failure if scan has no report)
                    archiveArtifacts artifacts: '**/target/dependency-check-report.html', fingerprint: true, allowEmptyArchive: true
                }
            }
        }

        stage('SonarQube Vulnerability Scan') {
            steps {
                echo 'Running SonarQube vulnerability and code quality checks...'
                // catchError: SonarQube scan is non-blocking. Pipeline continues even if token is expired or SonarQube is unreachable.
                catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                    withCredentials([string(credentialsId: "${SONAR_CREDENTIALS_ID}", variable: 'SONAR_TOKEN')]) {
                        bat "docker run --rm --add-host host.docker.internal:host-gateway -v maven-repo:/root/.m2 -v \"%WORKSPACE%\":/app -w /app maven:3.8.6-eclipse-temurin-17 mvn sonar:sonar \"-Dsonar.host.url=${SONAR_HOST_URL}\" \"-Dsonar.login=%SONAR_TOKEN%\""
                    }
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
                withCredentials([usernamePassword(
                    credentialsId: "${DOCKER_HUB_CREDENTIALS_ID}",
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    powershell '''
                        $password = $env:DOCKER_PASS
                        if ($password) { $password = $password.Trim() }
                        $username = $env:DOCKER_USER
                        if ($username) { $username = $username.Trim() }
                        
                        $passwdBytes = [System.Text.Encoding]::UTF8.GetBytes($password)
                        
                        $si = New-Object System.Diagnostics.ProcessStartInfo
                        $si.FileName = "docker"
                        $si.Arguments = "login -u $username --password-stdin"
                        $si.UseShellExecute = $false
                        $si.RedirectStandardInput = $true
                        $si.RedirectStandardOutput = $true
                        $si.RedirectStandardError = $true
                        
                        $p = [System.Diagnostics.Process]::Start($si)
                        $p.StandardInput.BaseStream.Write($passwdBytes, 0, $passwdBytes.Length)
                        $p.StandardInput.BaseStream.Flush()
                        $p.StandardInput.Close()
                        
                        $stdout = $p.StandardOutput.ReadToEnd()
                        $stderr = $p.StandardError.ReadToEnd()
                        $p.WaitForExit()
                        
                        Write-Output $stdout
                        if ($p.ExitCode -ne 0) {
                            Write-Error $stderr
                            exit $p.ExitCode
                        }
                    '''
                    bat "docker push ${DOCKER_IMAGE_NAME}:${DOCKER_TAG}"
                    bat "docker push ${DOCKER_IMAGE_NAME}:latest"
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
