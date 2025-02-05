pipeline {
    agent { label 'agenthost' }
    stages {
        stage('Clean') {
            steps {
                sh 'pwd'
                dir('backend') {
                    sh 'pwd'
                    sh 'mvn clean'
                }
                sh 'pwd'
            }
        }
        stage('Verify') {
            steps {
                dir('backend') {
                    echo 'Pipeline is launching the unit tests !'
                    sh 'mvn test'
                }
            }
        }
    }
}
