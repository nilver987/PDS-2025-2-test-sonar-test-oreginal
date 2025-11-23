pipeline {
    agent any

    tools {
        // Install the Maven version configured as "M3" and add it to the path.
        maven "MAVEN_HOME"
    }

    stages {
        stage('Clone') {
            steps {
                timeout(time: 2, unit: 'MINUTES'){
                    git branch: 'main', 
                        credentialsId: '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', 
                        url: 'https://github.com/nilver987/PDS-2025-2-test-sonar-test.git'
                }
            }
        }
        stage('Build') {
            steps {
                timeout(time: 8, unit: 'MINUTES'){
                    sh "mvn -DskipTests clean package -f turismobackend/pom.xml"
                }
            }
        }
        stage('Test') {
            steps {
                timeout(time: 10, unit: 'MINUTES'){
                    // Se cambia <test> por <install> para que se genere el reporte de jacoco
                    sh "mvn clean install -f turismobackend/pom.xml"
                }
            }
        }
        stage('Sonar') {
            steps {
                timeout(time: 4, unit: 'MINUTES'){
                    withSonarQubeEnv('sonarqube'){
                        sh "mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.0.2155:sonar -Pcoverage -f turismobackend/pom.xml"
                    }
                }
            }
        }
        stage('Quality gate') {
            steps {

                sleep(10) //seconds

                timeout(time: 4, unit: 'MINUTES'){
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        stage('Deploy') {
            steps {
                timeout(time: 8, unit: 'MINUTES'){
                    // Ejecutar mvn spring-boot:run
                    echo "mvn spring-boot:run -f turismobackend/pom.xml"
                }			
                //echo "mvn spring-boot:run -f turismobackend/pom.xml"
            }
        }
    }
}
