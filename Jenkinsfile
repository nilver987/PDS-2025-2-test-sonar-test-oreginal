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
        stage('Lint') {
            steps {
                dir('turismobackend') {
                    sh "mvn spotless:apply"
                    sh "mvn pmd:check"
                }
            }
        }
        stage('Compile') {
            steps {
                dir('turismobackend') {
                    sh "mvn -U -DskipTests compile"
                }
            }
        }
        
        stage('Security Scan') {
            steps {
                dir('turismobackend') {
                    sh "mvn org.owasp:dependency-check-maven:check"
                }
            }
        }
        stage('Validate Environment') {
            steps {
                script {
                    if (!env.JWT_SECRET) {
                        error("❌ Falta variable JWT_SECRET")
                    }
                }
            }
        }
        stage('Docs') {
            steps {
                dir('turismobackend') {
                    sh "mvn -U clean install -DskipTests -PgenerateDocs"
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
