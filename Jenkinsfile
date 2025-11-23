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
