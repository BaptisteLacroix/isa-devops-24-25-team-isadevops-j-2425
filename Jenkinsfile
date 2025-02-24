pipeline {
    agent { label 'agenthost' }
    stages {
        stage('Build') {
            steps {
                sh 'pwd'
                dir('backend') {
                    sh 'pwd'
                    sh 'mvn compile'
                }
                sh 'pwd'
            }
        }
        stage('Unit tests') {
                    steps {
                        dir('backend') {
                            echo 'Pipeline is launching unit tests !'
                            sh 'mvn test'
                        }
                    }
                }
        stage('Integration tests') {
            when{
                branch 'dev'
            }
            steps {
                dir('backend') {
                    echo 'Pipeline is launching integration tests !'
                    sh 'mvn verify'
                }
            }
        }
    }
}
