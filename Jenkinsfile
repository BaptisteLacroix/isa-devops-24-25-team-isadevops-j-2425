pipeline {
    agent { label 'agenthost' }
    tools {
        jfrog 'jfrog-cli'
    }
    stages {
        stage('[BE] Build') {
            steps {
                dir('backend') {
                    echo '🛠️ Pipeline is building the backend project !'
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
        stage('[BE] Publish to jfrog') {
            when{
                anyOf {
                     branch 'dev'
                     branch 'main'
                     branch 'feat/56-jfrog-jenkins'
                }
            }
            steps {
                dir('backend') {
                    script {
                        echo '📦 Building the backend project !'
                        sh 'mvn install -DskipTests'
                        def folderName
                        def fileName
                        if( env.GIT_BRANCH == "main" ){
                            def date = new Date().format('yyMMdd-HHmm')
                            fileName = "kiwi-card-${date}.jar"
                            folderName = "release/${date}"
                        }else{
                            def date = new Date().format('yyMMdd-HHmm')
                            fileName = "kiwi-card-${date}-SNAPSHOT.jar"
                            folderName = "snapshot/${date}"
                        }
                        echo "📤 Publishing the backend project to jfrog folder ${folderName} as ${fileName}"
                        jf "rt u *.jar kiwi-card-generic-local/${folderName}/${fileName}"
                    }
                }
            }
        }
        stage('[CLI] Build') {
             steps {
                 dir('cli') {
                     echo '🛠️ Pipeline is building the CLI project !'
                     sh 'mvn clean compile'
                 }
             }
         }
         stage('[BE] Docker Build') {
             when {
                 anyOf {
                     branch 'dev'
                     environment name: 'CHANGE_TARGET', value: 'dev'
                 }
             }
             steps {
                 dir('backend') {
                     echo '🐋📷 Pipeline is building the docker image of the backend project!'
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
