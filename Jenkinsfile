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
                    sh 'mvn clean compile'
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
        stage('[BE] Unit tests') {
            steps {
                dir('backend') {
                    echo '🧪 Pipeline is launching backend unit tests !'
                    sh 'mvn test'
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
                    sh './build-local.sh'
                }
            }
        }
        stage('SonarQube Analysis') {
            when {
                anyOf {
                    branch 'dev'
                    branch 'main'
                    environment name: 'CHANGE_TARGET', value: 'dev'
                }
            }
            steps {
                dir('cli') {
                    withSonarQubeEnv("KiwiCardSonar") {
                        sh "mvn verify sonar:sonar -Dsonar.projectKey=KiwiCardCLI -Dsonar.projectName='KiwiCardCLI' -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml,target/site/jacoco-it/jacoco.xml -Dsonar.junit.reportsPaths=target/surefire-reports,target/failsafe-reports"
                    }
                }
                dir('backend') {
                    withSonarQubeEnv("KiwiCardSonar") {
                        sh "mvn verify sonar:sonar -Dsonar.projectKey=KiwiCard -Dsonar.projectName='KiwiCard' -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml,target/site/jacoco-it/jacoco.xml -Dsonar.junit.reportsPaths=target/surefire-reports,target/failsafe-reports"
                    }
                }
            }
        }
        stage('[BE] Jfrog push') {
            when {
                anyOf {
                    branch 'dev'
                    branch 'main'
                }
            }
            steps {
                dir('backend') {
                    script {
                        echo '📦 Building the backend project !'
                        sh 'mvn install -DskipTests'
                        def folderName
                        def fileName = 'kiwi-card-be'
                        if (env.GIT_BRANCH == 'main') {
                            def date = new Date().format('yyMMdd-HHmm')
                            fileName = fileName + "-${date}.jar"
                            folderName = "release/${date}"
                        }else {
                            def date = new Date().format('yyMMdd-HHmm')
                            fileName = fileName + "-${date}-SNAPSHOT.jar"
                            folderName = "snapshot/${date}"
                        }
                        echo "📤 Publishing the backend project to jfrog folder ${folderName} as ${fileName}"
                        jf "rt u *.jar kiwi-card-be-generic-local/${folderName}/${fileName}"
                    }
                }
            }
        }
        stage('[CLI] Jfrog push') {
            when {
                anyOf {
                    branch 'dev'
                    branch 'main'
                }
            }
            steps {
                dir('cli') {
                    script {
                        echo '📦 Building the cli project !'
                        sh 'mvn install -DskipTests'
                        def folderName
                        def fileName = 'kiwi-card-cli'
                        if (env.GIT_BRANCH == 'main') {
                            def date = new Date().format('yyMMdd-HHmm')
                            fileName = fileName + "-${date}.jar"
                            folderName = "release/${date}"
                        }else {
                            def date = new Date().format('yyMMdd-HHmm')
                            fileName = fileName + "-${date}-SNAPSHOT.jar"
                            folderName = "snapshot/${date}"
                        }
                        echo "📤 Publishing the cli project to jfrog folder ${folderName} as ${fileName}"
                        jf "rt u *.jar kiwi-card-cli-generic-local/${folderName}/${fileName}"
                    }
                }
            }
        }
        stage('[BE][CLI] Docker Push') {
            when {
                anyOf {
                    branch 'dev'
                    branch 'main'
                    branch 'feat/102-push-docker-hub'
                }
            }
           steps {
               dir('backend') {
                   sh './build.sh'
                   echo '📦🐋 Push de l\'image backend sur DockerHub'
                   sh '''
                       docker login -u $DOCKERHUB_USERNAME -p $DOCKERHUB_PASSWORD
                       docker tag teamj/kiwicard-spring-backend $DOCKERHUB_USERNAME/kiwicard-spring-backend:latest
                       docker push $DOCKERHUB_USERNAME/kiwicard-spring-backend:latest
                   '''
               }
               dir('cli') {
                   sh './build.sh'
                   echo '📦🐋 Push de l\'image CLI sur DockerHub'
                   sh '''
                       docker login -u $DOCKERHUB_USERNAME -p $DOCKERHUB_PASSWORD
                       docker tag teamj/kiwicard-cli $DOCKERHUB_USERNAME/kiwicard-cli:latest
                       docker push $DOCKERHUB_USERNAME/kiwicard-cli:latest
                   '''
               }
           }

        }
    }
}
