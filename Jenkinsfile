pipeline {
    agent any

    tools {
        maven "MAVEN_HOME"
    }

    stages {

        stage('Clone') {
            steps {
                timeout(time: 2, unit: 'MINUTES') {
                    git branch: 'main',
                        credentialsId: 'githubtoken1',
                        url: 'https://github.com/nilver987/PDS-2025-2-test-sonar-test-oreginal.git'
                }
            }
        }

        stage('Build') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    sh "mvn -U -DskipTests clean package -f turismobackend/pom.xml"
                }
            }
        }

        stage('Test') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    sh "mvn -U clean test -f turismobackend/pom.xml"
                }
            }
        }

        stage('Sonar') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    withSonarQubeEnv('sonarqube') {
                        sh "mvn -U verify org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184:sonar -Dsonar.projectKey=turismo -f turismobackend/pom.xml"
                    }
                }
            }
        }

        stage('Quality gate') {
            steps {
                timeout(time: 3, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Deploy') {
            steps {
                echo "Iniciando deploy (simulado)..."
                echo "spring-boot:run -f turismobackend/pom.xml"
            }
        }
    }
}
