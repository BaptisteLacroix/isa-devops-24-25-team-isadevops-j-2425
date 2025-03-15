pipeline {
    agent { label 'agenthost' }
    stages {
        stage('[BE] Build') {
            steps {
                dir('backend') {
                    echo '🛠️ Pipeline is building the backend project !'
                    sh 'mvn clean compile'
                }
            }
        }
        stage('[BE] Unit tests') {
                    steps {
                        dir('backend') {
                            echo '🧪 Pipeline is launching backend unit tests !'
                            sh 'mvn test'
                        }
                    }
                }
        stage('[BE] Integration tests') {
            when{
                anyOf {
                     branch 'dev'
                     environment name: 'CHANGE_TARGET', value: 'dev'
                }
            }
            steps {
                dir('backend') {
                    echo '🧩 Pipeline is launching backend integration tests !'
                    sh 'mvn verify'
                }
            }
        }
        stage('[CLI] Build') {
             steps {
                 dir('cli') {
                     echo '🛠️ Pipeline is building cli the project !'
                     sh 'mvn clean compile'
                 }
             }
         }
         stage('[Docker Backend] Build') {
             when {
                 anyOf {
                     branch 'dev'
                     environment name: 'CHANGE_TARGET', value: 'dev'
                 }
             }
             steps {
                 dir('backend') {
                    sh './build.sh'
                 }
             }
         }
         stage('[CLI] Unit tests') {
             steps {
                 dir('cli') {
                     echo '🧪 Pipeline is launching cli unit tests !'
                     sh 'mvn test'
                 }
             }
         }
         stage('[CLI] Integration tests') {
             when{
                 anyOf {
                     branch 'dev'
                     environment name: 'CHANGE_TARGET', value: 'dev'
                 }
             }
             steps {
                 dir('cli') {
                     echo '🧩 Pipeline is launching cli integration tests !'
                     sh 'mvn verify'
                 }
             }
        }
    }
}
