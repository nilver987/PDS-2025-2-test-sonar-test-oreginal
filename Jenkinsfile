pipeline {
    agent any

    tools {
        maven "MAVEN_HOME"
    }

    stages {

        stage('Clone') {
            steps {
                timeout(time: 3, unit: 'MINUTES') {
                    git branch: 'main',
                        credentialsId: 'githubtoken1',
                        url: 'https://github.com/nilver987/PDS-2025-2-test-sonar-test-oreginal.git'
                }
            }
        }

        stage('Build') {
            steps {
                timeout(time: 25, unit: 'MINUTES') {
                    dir('turismobackend') {
                        sh "mvn -U -DskipTests clean package"
                    }
                }
            }
        }

        stage('Test') {
            steps {
                timeout(time: 15, unit: 'MINUTES') {
                    dir('turismobackend') {
                        sh "mvn -U test"
                    }
                }
            }
        }

        stage('Sonar') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    dir('turismobackend') {
                        withSonarQubeEnv('sonarqube') {
                            sh "mvn -U verify org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184:sonar"
                        }
                    }
                }
            }
        }

        stage('Quality gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Deploy') {
            steps {
                dir('turismobackend') {
                    echo "spring-boot:run ..."
                }
            }
        }

    }
}
