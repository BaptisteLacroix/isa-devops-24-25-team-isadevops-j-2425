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
                    sh './build.sh'
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
                        sh "mvn verify sonar:sonar -Dsonar.projectKey=KiwiCardCLI -Dsonar.projectName='KiwiCardCLI' -Dsonar.branch.name=${env.GIT_BRANCH} -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml,target/site/jacoco-it/jacoco.xml -Dsonar.junit.reportsPaths=target/surefire-reports,target/failsafe-reports"
                    }
                }
                dir('backend') {
                    withSonarQubeEnv("KiwiCardSonar") {
                        sh "mvn verify sonar:sonar -Dsonar.projectKey=KiwiCard -Dsonar.projectName='KiwiCard' -Dsonar.branch.name=${env.GIT_BRANCH} -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml,target/site/jacoco-it/jacoco.xml -Dsonar.junit.reportsPaths=target/surefire-reports,target/failsafe-reports"
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
                            def date = new Date().format('yyMMdd-HHmm ')
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
    }
}
