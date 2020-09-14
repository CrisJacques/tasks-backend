pipeline {
    agent any
    stages {
        stage ('Subir banco de testes e Sonar'){
            steps{
                dir('E:\\Meu GitHub\\CursoJenkins'){
                    bat 'docker-compose up -d'
                }
            }
        }
        stage ('Subir Tomcat'){
            steps{
                dir('E:\\Meu GitHub\\CursoJenkins\\projeto\\apache-tomcat-8.5.50\\bin'){
                    bat 'startup.bat -d'
                }  
            }
        }
        stage ('Build Backend'){
            steps {
                dir('C:\\Users\\cjn_1\\.jenkins\\workspace\\Pipeline'){
                    sleep(60)
                    bat 'mvn clean package -DskipTests=true'
                }
                
            }
        }
        stage ('Unit Tests'){
            steps {
                bat 'mvn test'
            }
        }
        stage ('Sonar Analysis'){
            environment {
                scannerHome = tool 'SONAR_SCANNER'
            }
            steps {
                withSonarQubeEnv('SONAR_LOCAL') {
                    bat "${scannerHome}/bin/sonar-scanner -e -Dsonar.projectKey=DeployBack -Dsonar.host.url=http://192.168.99.100:9000 -Dsonar.login=4fe0ad99876e4b575f35bf61f41b2169f0e0dbf2 -Dsonar.java.binaries=target -Dsonar.coverage.exclusions=**/.mvn/**,**/src/test/**,**/model/**,**Application.java"
                }
            }
        }
        stage ('Quality Gate'){
            steps {
                sleep(45)
                timeout(time: 1, unit: 'MINUTES'){
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        stage('Deploy Backend'){
            steps {
                deploy adapters: [tomcat8(credentialsId: 'TomcatLogin', path: '', url: 'http://localhost:8080')], contextPath: 'tasks-backend', war: 'target/tasks-backend.war'
            }
        }
        stage ('API Test'){
            steps {
                dir('api-test'){
                    git credentialsId: 'github_login', url: 'https://github.com/CrisJacques/tasks-api-test'
                    bat 'mvn test'
                }
            }
        }
        stage('Deploy Frontend'){
            steps {
                dir('frontend'){
                    git credentialsId: 'github_login', url: 'https://github.com/CrisJacques/tasks-frontend'
                    bat 'mvn clean package'
                    deploy adapters: [tomcat8(credentialsId: 'TomcatLogin', path: '', url: 'http://localhost:8080')], contextPath: 'tasks', war: 'target/tasks.war'
                }
            }
        }
        stage ('Functional Test'){
            steps {
                dir('functional-test'){
                    git credentialsId: 'github_login', url: 'https://github.com/CrisJacques/tasks-functional-tests'
                    bat 'mvn test'
                }
            }
        }
        stage ('Derruba Sonar e Banco de Testes'){
            steps{
                bat 'docker stop pg-tasks'
                bat 'docker stop sonar'
                bat 'docker stop pg-sonar'
            }
        }
        stage ('Deploy Prod'){
            steps {
                sleep(30)
                bat 'docker-compose build'
                bat 'docker-compose up -d'
            }
        }
        stage ('Health Check'){
            steps {
                sleep(60)
                dir('functional-test'){
                    bat 'mvn verify -Dskip.surefire.tests'
                }
            }
        }
    }
    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml, api-test/target/surefire-reports/*.xml, functional-test/target/surefire-reports/*.xml, functional-test/target/failsafe-reports/*.xml'
            archiveArtifacts artifacts: 'target/tasks-backend.war, frontend/target/tasks.war', onlyIfSuccessful: true
        }
        unsuccessful{
            emailext attachLog: true, body: 'See attached log', subject: 'Build $BUILD_NUMBER has failed', to: 'cjn_1707@hotmail.com'
        }
        fixed{
            emailext attachLog: true, body: 'See attached log', subject: 'Build is fine!!!', to: 'cjn_1707@hotmail.com'
        }

    }
}
