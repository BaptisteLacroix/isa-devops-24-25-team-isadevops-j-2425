pipeline {
    agent { label 'agenthost' }
    stages {
        stage('Build') {
            steps {
                dir('backend') {
                     echo '🛠️ Pipeline is building the project !'
                    sh 'mvn clean compile'
                }
            }
        }
        stage('Unit tests') {
                    steps {
                        dir('backend') {
                            echo '🧪 Pipeline is launching unit tests !'
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
                    echo '🧩 Pipeline is launching integration tests !'
                    sh 'mvn verify'
                }
            }
        }
    }
}
